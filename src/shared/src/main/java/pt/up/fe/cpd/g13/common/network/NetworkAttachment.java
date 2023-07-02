package pt.up.fe.cpd.g13.common.network;

import pt.up.fe.cpd.g13.common.network.event.PacketListener;
import pt.up.fe.cpd.g13.common.network.utils.IOBuffers;

import java.nio.channels.SelectionKey;
import java.util.function.Consumer;
import java.util.logging.Logger;

public class NetworkAttachment implements PacketHandle {

    private final IOBuffers buffers;

    private PacketListener listener = null;

    public NetworkAttachment(SelectionKey key) {
        this.buffers = new IOBuffers(key);
    }

    public IOBuffers getBuffers() {
        return buffers;
    }

    @Override
    public synchronized void setPacketListener(PacketListener packetListener) {
        synchronized (buffers) {
            this.listener = packetListener;
            if (buffers.isClosed()) listener.dispatchDisconnect(this);
        }
    }

    public synchronized void withListener(Consumer<PacketListener> consumer) {
        if (listener == null) return;
        consumer.accept(listener);
    }


    @Override
    public void sendPacket(Object packet) {
        var logger = Logger.getLogger(NetworkService.class.getName());
        logger.entering("NetworkAttachment", "sendPacket", packet);

        buffers.addPendingPacket(packet);

        logger.exiting("NetworkAttachment", "sendPacket", packet);
    }

    @Override
    public boolean isValid() {
        return !buffers.isClosed();
    }

    @Override
    public void disconnect() {
        buffers.close();
    }

    public synchronized void processPacket(Object packet) {
        if (listener == null)
            return;

        listener.dispatch(this, packet);
    }
}
