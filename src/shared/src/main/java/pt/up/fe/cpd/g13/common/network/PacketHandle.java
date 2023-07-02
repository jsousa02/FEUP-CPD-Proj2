package pt.up.fe.cpd.g13.common.network;

import pt.up.fe.cpd.g13.common.network.event.PacketListener;

import java.util.concurrent.Executor;

public interface PacketHandle {

    void setPacketListener(PacketListener packetListener);

    void sendPacket(Object packet);

    boolean isValid();
    void disconnect();
}
