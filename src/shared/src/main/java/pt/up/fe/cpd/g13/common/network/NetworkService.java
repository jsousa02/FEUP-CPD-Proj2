package pt.up.fe.cpd.g13.common.network;

import pt.up.fe.cpd.g13.common.network.event.NetworkListener;
import pt.up.fe.cpd.g13.common.network.event.PacketListener;
import pt.up.fe.cpd.g13.common.network.serialization.Serializer;
import pt.up.fe.cpd.g13.common.service.Service;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketAddress;
import java.nio.channels.*;

public class NetworkService extends Service {

    public static NetworkService bind(SocketAddress address) {
        return new NetworkService(Role.SERVER, address);
    }

    public static NetworkService connect(SocketAddress address) {
        return new NetworkService(Role.CLIENT, address);
    }

    private enum Role {
        SERVER, CLIENT;

    }
    private final Serializer serializer = Serializer.createDefaultSerializer();
    private final Role role;
    private final SocketAddress address;

    private Selector selector;
    private NetworkListener networkListener = null;
    private int tries = 0;

    private NetworkService(Role role, SocketAddress address) {
        super(NetworkService.class);

        this.role = role;
        this.address = address;
    }

    public void setNetworkListener(NetworkListener networkListener) {
        this.networkListener = networkListener;
    }


    @Override
    protected void runWithResources(Runnable service) throws IOException, InterruptedException {
        try (var selector = Selector.open()) {

            this.selector = selector;

            switch (role) {
                case SERVER -> runAsServer(service);
                case CLIENT -> runAsClient(service);
            }
        }
    }

    private void runAsServer(Runnable callback) throws IOException {
        try (var channel = ServerSocketChannel.open()) {
            registerChannel(channel, SelectionKey.OP_ACCEPT);
            channel.bind(address);

            callback.run();
        }
    }

    private void runAsClient(Runnable callback) throws IOException, InterruptedException {
        while (tries < 3) {
            tries++;

            if (Thread.currentThread().isInterrupted())
                return;

            try (var channel = SocketChannel.open()) {
                var key = registerChannel(channel, SelectionKey.OP_CONNECT);
                key.attach(new NetworkAttachment(key));

                channel.connect(address);

                callback.run();

            } catch (UnresolvedAddressException ignored) {}

            Thread.sleep(5000);
        }

        System.out.println("Could not connect to game server");
        System.exit(1);
    }

    protected boolean tick() throws IOException {
        try {
            logger.entering("NetworkService", "tick");

            selector.select();

            var selectedKeys = selector.selectedKeys();
            var iterator = selectedKeys.iterator();

            while (iterator.hasNext()) {
                var key = iterator.next();
                var channel = key.channel();
                var attachment = (NetworkAttachment) key.attachment();

                if (key.isAcceptable())
                    handleAcceptableChannel((ServerSocketChannel) channel);

                try {
                    if (key.isConnectable())
                        handleConnectableChannel((SocketChannel) channel, attachment);

                    if (key.isReadable())
                        handleReadableChannel((SocketChannel) channel, attachment);

                    if (key.isWritable())
                        handleWritableChannel((SocketChannel) channel, attachment);

                } catch (CancelledKeyException e) {
                    channel.close();

                    if (networkListener != null) {
                        networkListener.onDisconnect(attachment);
                    }

                    attachment.withListener(listener -> listener.dispatchDisconnect(attachment));
                }

                iterator.remove();
            }

            return true;
        } catch (ConnectException | ReconnectException e) {
            return false;
        }
    }

    private SelectionKey registerChannel(SelectableChannel channel, int interestSet) throws IOException {
        channel.configureBlocking(false);
        return channel.register(selector, interestSet);
    }

    private void handleAcceptableChannel(ServerSocketChannel channel) throws IOException {
        var acceptedChannel = channel.accept();

        var key = registerChannel(acceptedChannel, SelectionKey.OP_READ);
        var handle = new NetworkAttachment(key);
        key.attach(handle);

        tries = 0;

        if (networkListener != null) {
            networkListener.onConnect(handle);
        }
    }

    private void handleConnectableChannel(SocketChannel channel, NetworkAttachment handle) throws IOException {
        if (channel.finishConnect()) {

            var key = channel.keyFor(selector);
            key.interestOpsAnd(~SelectionKey.OP_CONNECT);
            key.interestOpsOr(SelectionKey.OP_READ);

            tries = 0;

            if (networkListener != null)
                networkListener.onConnect(handle);
        }
    }

    private void handleReadableChannel(SocketChannel channel, NetworkAttachment handle) throws IOException {
        var buffers = handle.getBuffers();

        buffers.readFrom(channel);

        while (buffers.keepDuplicateReadBufferIf(readBuffer -> {
            var packet = serializer.tryToDecodePacket(readBuffer).orElse(null);
            if (packet == null) {
                return false;
            }

            logger.config("Received packet %s".formatted(packet.toString()));

            handle.processPacket(packet);
            return true;
        }));
    }

    private void handleWritableChannel(SocketChannel channel, NetworkAttachment handle) throws IOException {
        var buffers = handle.getBuffers();

        buffers.flushPendingPackets(packet -> {

            logger.config("Sent packet %s".formatted(packet.toString()));

            return buffers.keepDuplicateWriteBufferIf(writeBuffer ->
                    serializer.tryToEncodePacket(packet, writeBuffer));
        });

        buffers.writeTo(channel);
    }
}
