package pt.up.fe.cpd.g13.common.network.packet.game;

import pt.up.fe.cpd.g13.common.network.serialization.PacketDecoder;
import pt.up.fe.cpd.g13.common.network.serialization.PacketEncoder;

import java.nio.ByteBuffer;

public class GameAbortPacket {

    public static class Encoder implements PacketEncoder<GameAbortPacket> {
        @Override
        public void encode(GameAbortPacket packet, ByteBuffer buffer) {}
    }

    public static class Decoder implements PacketDecoder<GameAbortPacket> {
        @Override
        public GameAbortPacket decode(ByteBuffer buffer) {
            return new GameAbortPacket();
        }
    }
}
