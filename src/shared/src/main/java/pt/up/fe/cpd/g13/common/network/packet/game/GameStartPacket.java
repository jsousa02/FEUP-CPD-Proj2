package pt.up.fe.cpd.g13.common.network.packet.game;

import pt.up.fe.cpd.g13.common.network.serialization.PacketDecoder;
import pt.up.fe.cpd.g13.common.network.serialization.PacketEncoder;

import java.nio.ByteBuffer;

public record GameStartPacket(int wordLength) {

    public static class Encoder implements PacketEncoder<GameStartPacket> {
        @Override
        public void encode(GameStartPacket packet, ByteBuffer buffer) {
            buffer.putInt(packet.wordLength());
        }
    }

    public static class Decoder implements PacketDecoder<GameStartPacket> {
        @Override
        public GameStartPacket decode(ByteBuffer buffer) {
            var wordLength = buffer.getInt();
            return new GameStartPacket(wordLength);
        }
    }
}
