package pt.up.fe.cpd.g13.common.network.packet;

import pt.up.fe.cpd.g13.common.network.serialization.PacketDecoder;
import pt.up.fe.cpd.g13.common.network.serialization.PacketEncoder;

import java.nio.ByteBuffer;

public record PacketHeader(byte packetId, short payloadLength) {

    public static class Encoder implements PacketEncoder<PacketHeader> {
        @Override
        public void encode(PacketHeader packet, ByteBuffer buffer) {
            buffer.put(packet.packetId());
            buffer.putShort(packet.payloadLength());
        }
    }

    public static class Decoder implements PacketDecoder<PacketHeader> {
        @Override
        public PacketHeader decode(ByteBuffer rawData) {
            var packetId = rawData.get();
            var payloadLength = rawData.getShort();

            return new PacketHeader(packetId, payloadLength);
        }
    }
}
