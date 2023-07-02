package pt.up.fe.cpd.g13.common.network.event;

import pt.up.fe.cpd.g13.common.network.PacketHandle;

public interface NetworkListener {

    void onConnect(PacketHandle handle);

    void onDisconnect(PacketHandle handle);
}
