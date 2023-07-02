package pt.up.fe.cpd.g13.common.network.packet.game;

import pt.up.fe.cpd.g13.common.network.serialization.PacketDecoder;
import pt.up.fe.cpd.g13.common.network.serialization.PacketEncoder;

import java.nio.ByteBuffer;

public record PlayResponsePacket(char letterPlayed) {
    public static class Encoder implements PacketEncoder<PlayResponsePacket> {
        @Override
        public void encode(PlayResponsePacket packet, ByteBuffer buffer) {
            buffer.putChar(packet.letterPlayed());
        }
    }

    public static class Decoder implements PacketDecoder<PlayResponsePacket> {
        @Override
        public PlayResponsePacket decode(ByteBuffer buffer) {
            var letterPlayed = buffer.getChar();
            return new PlayResponsePacket(letterPlayed);
        }
    }
}
