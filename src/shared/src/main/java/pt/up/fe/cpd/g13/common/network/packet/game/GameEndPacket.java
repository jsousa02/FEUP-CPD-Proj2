package pt.up.fe.cpd.g13.common.network.packet.game;

import pt.up.fe.cpd.g13.common.network.serialization.BufferUtils;
import pt.up.fe.cpd.g13.common.network.serialization.PacketDecoder;
import pt.up.fe.cpd.g13.common.network.serialization.PacketEncoder;

import java.nio.ByteBuffer;

public record GameEndPacket(boolean won, char[] targetWord, String currentPlayerUsername) {
    public static class Encoder implements PacketEncoder<GameEndPacket> {

        @Override
        public void encode(GameEndPacket packet, ByteBuffer buffer) {
            BufferUtils.putBoolean(buffer, packet.won());

            var targetWord = packet.targetWord();
            buffer.putInt(targetWord.length);

            for (char c : targetWord) {
                buffer.putChar(c);
            }

            BufferUtils.putString(buffer, packet.currentPlayerUsername());
        }
    }

    public static class Decoder implements PacketDecoder<GameEndPacket> {
        @Override
        public GameEndPacket decode(ByteBuffer buffer) {
            var won = BufferUtils.getBoolean(buffer);

            var updatedWordLength = buffer.getInt();
            char[] targetWord = new char[updatedWordLength];

            for (int i = 0; i < targetWord.length; i++) {
                targetWord[i] = buffer.getChar();
            }

            var winnerUsername = BufferUtils.getString(buffer);

            return new GameEndPacket(won, targetWord, winnerUsername);
        }
    }
}
