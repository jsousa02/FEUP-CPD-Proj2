package pt.up.fe.cpd.g13.common.network.event;

import pt.up.fe.cpd.g13.common.network.PacketHandle;
import pt.up.fe.cpd.g13.common.network.packet.UnknownPacket;
import pt.up.fe.cpd.g13.common.network.packet.auth.AuthRequestPacket;
import pt.up.fe.cpd.g13.common.network.packet.auth.AuthResponsePacket;
import pt.up.fe.cpd.g13.common.network.packet.game.*;

import java.util.concurrent.Executor;
import java.util.function.Function;

public class PacketListener {

    private Executor executor = null;

    public Executor getExecutor() {
        return executor;
    }

    public void setExecutor(Executor executor) {
        this.executor = executor;
    }

    public void onAuthRequestPacket(PacketHandle handle, AuthRequestPacket packet) {}
    public void onAuthResponsePacket(PacketHandle handle, AuthResponsePacket packet) {}
    public void onGameAbortPacket(PacketHandle handle, GameAbortPacket packet) {}
    public void onGameEndPacket(PacketHandle handle, GameEndPacket packet) {}
    public void onGameStartPacket(PacketHandle handle, GameStartPacket packet) {}
    public void onGameUpdatePacket(PacketHandle handle, GameUpdatePacket packet) {}
    public void onPlayRequestPacket(PacketHandle handle, PlayRequestPacket packet) {}
    public void onPlayResponsePacket(PacketHandle handle, PlayResponsePacket packet) {}

    public void onDisconnect(PacketHandle handle) {}

    private void dispatchOnCurrentThread(PacketHandle handle, Object packet) {
        if (packet instanceof AuthRequestPacket authRequestPacket) {
            onAuthRequestPacket(handle, authRequestPacket);
        } else if (packet instanceof AuthResponsePacket authResponsePacket) {
            onAuthResponsePacket(handle, authResponsePacket);
        } else if (packet instanceof GameAbortPacket gameAbortPacket) {
            onGameAbortPacket(handle, gameAbortPacket);
        } else if (packet instanceof GameEndPacket gameEndPacket) {
            onGameEndPacket(handle, gameEndPacket);
        } else if (packet instanceof GameStartPacket gameStartPacket) {
            onGameStartPacket(handle, gameStartPacket);
        } else if (packet instanceof GameUpdatePacket gameUpdatePacket) {
            onGameUpdatePacket(handle, gameUpdatePacket);
        } else if (packet instanceof PlayRequestPacket playRequestPacket) {
            onPlayRequestPacket(handle, playRequestPacket);
        } else if (packet instanceof PlayResponsePacket playResponsePacket) {
            onPlayResponsePacket(handle, playResponsePacket);
        }
    }

    public void dispatch(PacketHandle handle, Object packet) {
        var executor = getExecutor();
        if (executor == null || packet instanceof UnknownPacket)
            return;

        executor.execute(() -> dispatchOnCurrentThread(handle, packet));
    }

    public void dispatchDisconnect(PacketHandle handle) {
        var executor = getExecutor();
        if (executor == null)
            return;

        executor.execute(() -> onDisconnect(handle));
    }
}
