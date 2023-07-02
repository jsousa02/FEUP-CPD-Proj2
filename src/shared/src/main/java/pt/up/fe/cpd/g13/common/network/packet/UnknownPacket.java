package pt.up.fe.cpd.g13.common.network.packet;

import pt.up.fe.cpd.g13.common.network.serialization.PacketDecoder;
import pt.up.fe.cpd.g13.common.network.serialization.PacketEncoder;

import java.nio.ByteBuffer;

public class UnknownPacket {

    public static class Encoder implements PacketEncoder<UnknownPacket> {
        @Override
        public void encode(UnknownPacket packet, ByteBuffer buffer) {}
    }

    public static class Decoder implements PacketDecoder<UnknownPacket> {
        @Override
        public UnknownPacket decode(ByteBuffer buffer) {
            return new UnknownPacket();
        }
    }
}
