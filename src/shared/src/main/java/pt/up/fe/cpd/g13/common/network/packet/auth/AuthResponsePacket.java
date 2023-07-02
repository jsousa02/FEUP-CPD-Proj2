package pt.up.fe.cpd.g13.common.network.packet.auth;

import pt.up.fe.cpd.g13.common.network.serialization.PacketDecoder;
import pt.up.fe.cpd.g13.common.network.serialization.PacketEncoder;

import java.nio.ByteBuffer;

public record AuthResponsePacket(UserState state) {
    public enum UserState {
        LOGGED_IN, REFUSED;
    }

    public static class Encoder implements PacketEncoder<AuthResponsePacket> {
        @Override
        public void encode(AuthResponsePacket packet, ByteBuffer buffer) {
            var state = packet.state();
            buffer.putInt(state.ordinal());
        }
    }

    public static class Decoder implements PacketDecoder<AuthResponsePacket> {
        @Override
        public AuthResponsePacket decode(ByteBuffer rawData) {
            var encodedValue = rawData.getInt();
            var decodedState = UserState.values()[encodedValue];

            return new AuthResponsePacket(decodedState);
        }
    }
}
