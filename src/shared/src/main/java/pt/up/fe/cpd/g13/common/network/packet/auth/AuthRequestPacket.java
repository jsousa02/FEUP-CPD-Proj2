package pt.up.fe.cpd.g13.common.network.packet.auth;

import pt.up.fe.cpd.g13.common.network.serialization.PacketEncoder;
import pt.up.fe.cpd.g13.common.network.serialization.BufferUtils;
import pt.up.fe.cpd.g13.common.network.serialization.PacketDecoder;

import java.nio.ByteBuffer;

public record AuthRequestPacket(String username, String password) {
    public static class Encoder implements PacketEncoder<AuthRequestPacket> {
        @Override
        public void encode(AuthRequestPacket packet, ByteBuffer buffer) {
            BufferUtils.putString(buffer, packet.username());
            BufferUtils.putString(buffer, packet.password());
        }
    }

    public static class Decoder implements PacketDecoder<AuthRequestPacket> {
        @Override
        public AuthRequestPacket decode(ByteBuffer rawData) {
            var username = BufferUtils.getString(rawData);
            var password = BufferUtils.getString(rawData);

            return new AuthRequestPacket(username, password);
        }
    }
}
