package pt.up.fe.cpd.g13.common.network.packet.game;

import pt.up.fe.cpd.g13.common.network.serialization.BufferUtils;
import pt.up.fe.cpd.g13.common.network.serialization.PacketDecoder;
import pt.up.fe.cpd.g13.common.network.serialization.PacketEncoder;

import java.nio.ByteBuffer;

public record GameUpdatePacket(char[] updatedWord, char letterPlayed, String playerUsername) {
    public static class Encoder implements PacketEncoder<GameUpdatePacket> {
        @Override
        public void encode(GameUpdatePacket packet, ByteBuffer buffer) {
            var updatedWord = packet.updatedWord();
            buffer.putInt(updatedWord.length);

            for (char c : updatedWord) {
                buffer.putChar(c);
            }

            buffer.putChar(packet.letterPlayed());
            BufferUtils.putString(buffer, packet.playerUsername());
        }
    }

    public static class Decoder implements PacketDecoder<GameUpdatePacket> {
        @Override
        public GameUpdatePacket decode(ByteBuffer buffer) {
            var updatedWordLength = buffer.getInt();
            char[] updatedWord = new char[updatedWordLength];

            for (int i = 0; i < updatedWord.length; i++) {
                updatedWord[i] = buffer.getChar();
            }

            var letterPlayed = buffer.getChar();
            var username = BufferUtils.getString(buffer);

            return new GameUpdatePacket(updatedWord, letterPlayed, username);
        }
    }
}
