package pt.up.fe.cpd.g13.common.network.utils;

import pt.up.fe.cpd.g13.common.utils.DebugUtils;

import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.LinkedList;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class IOBuffers implements Closeable {

    private ByteBuffer readBuffer = ByteBuffer.allocate(1024);
    private ByteBuffer writeBuffer = ByteBuffer.allocate(1024);
    private LinkedList<Object> pendingPacketWrites = new LinkedList<>();
    private boolean isAccepting = true;

    private final SelectionKey key;

    public IOBuffers(SelectionKey key) {
        this.key = key;
        readBuffer.limit(0);
    }

    public void readFrom(SocketChannel channel) throws IOException {
        readBuffer.compact();

        int bytesRead;
        while ((bytesRead = channel.read(readBuffer)) > 0);

        if (bytesRead == -1) {
            channel.close();
        }

        readBuffer.flip();
    }

    public void writeTo(SocketChannel channel) throws IOException {
        writeBuffer.flip();

        int bytesWritten;
        while ((bytesWritten = channel.write(writeBuffer)) > 0);

        if (bytesWritten == -1) {
            channel.close();
        }

        writeBuffer.compact();
        updateWriteInterest();
    }

    public boolean keepDuplicateReadBufferIf(Predicate<ByteBuffer> mapper) {
        var duplicatedBuffer = readBuffer.duplicate();
        if (mapper.test(duplicatedBuffer)) {
            this.readBuffer = duplicatedBuffer;
            return true;
        }

        return false;
    }

    public boolean keepDuplicateWriteBufferIf(Predicate<ByteBuffer> mapper) {
        var duplicatedBuffer = writeBuffer.duplicate();
        if (mapper.test(duplicatedBuffer)) {
            this.writeBuffer = duplicatedBuffer;
            return true;
        }

        return false;
    }

    public void addPendingPacket(Object packet) {
        synchronized (this) {
            if (isClosed()) return;

            pendingPacketWrites.add(packet);
            updateWriteInterest();
        }
    }

    public void flushPendingPackets(Predicate<Object> predicate) {
        LinkedList<Object> packetsToBeWritten;
        synchronized (this) {
            packetsToBeWritten = pendingPacketWrites;
            pendingPacketWrites = new LinkedList<>();
        }

        var iterator = packetsToBeWritten.iterator();

        while (iterator.hasNext()) {
            var element = iterator.next();
            if (!predicate.test(element))
                return;

            iterator.remove();
        }

        synchronized (this) {
            packetsToBeWritten.addAll(pendingPacketWrites);
            pendingPacketWrites = packetsToBeWritten;

            if (!isAccepting && pendingPacketWrites.isEmpty()) {
                key.cancel();

                var selector = key.selector();
                selector.wakeup();
            }
        }
    }

    private synchronized void updateWriteInterest() {
        var shouldWrite = !pendingPacketWrites.isEmpty() || writeBuffer.position() != 0;

        if (shouldWrite)
            key.interestOpsOr(SelectionKey.OP_WRITE);
        else
            key.interestOpsAnd(~SelectionKey.OP_WRITE);

        var selector = key.selector();
        selector.wakeup();
    }

    public synchronized boolean isClosed() {
        return !(isAccepting && key.isValid());
    }

    @Override
    public synchronized void close() {
        isAccepting = false;

        if (pendingPacketWrites.isEmpty()) {
            key.cancel();

            var selector = key.selector();
            selector.wakeup();
        }
    }
}
