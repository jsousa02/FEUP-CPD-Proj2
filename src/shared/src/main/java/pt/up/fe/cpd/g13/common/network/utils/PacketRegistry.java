package pt.up.fe.cpd.g13.common.network.utils;

import pt.up.fe.cpd.g13.common.network.packet.UnknownPacket;
import pt.up.fe.cpd.g13.common.network.packet.auth.AuthRequestPacket;
import pt.up.fe.cpd.g13.common.network.packet.auth.AuthResponsePacket;
import pt.up.fe.cpd.g13.common.network.packet.game.*;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class PacketRegistry {

    private static final Map<Class<?>, Byte> packetIdsFromPackets = new HashMap<>();
    private static final Map<Byte, Class<?>> packetsFromPacketIds = new TreeMap<>();

    static {
        registerPacket(AuthRequestPacket.class, (byte) 0x01);
        registerPacket(AuthResponsePacket.class, (byte) 0x02);
        registerPacket(GameAbortPacket.class, (byte) 0x03);
        registerPacket(GameEndPacket.class, (byte) 0x04);
        registerPacket(GameStartPacket.class, (byte) 0x05);
        registerPacket(GameUpdatePacket.class, (byte) 0x06);
        registerPacket(PlayRequestPacket.class, (byte) 0x07);
        registerPacket(PlayResponsePacket.class, (byte) 0x08);
        registerPacket(UnknownPacket.class, (byte) 0xFF);
    }

    private static void registerPacket(Class<?> packet, byte id) {
        packetsFromPacketIds.put(id, packet);
        packetIdsFromPackets.put(packet, id);
    }

    public static Class<?> getPacketFromId(byte id) {
        return packetsFromPacketIds.getOrDefault(id, UnknownPacket.class);
    }

    public static byte getIdFromPacketType(Class<?> packetType) {
        return packetIdsFromPackets.getOrDefault(packetType, (byte) 0xFF);
    }
}
