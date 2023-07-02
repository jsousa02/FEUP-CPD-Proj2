package pt.up.fe.cpd.g13.common.network.packet.game;

import pt.up.fe.cpd.g13.common.network.serialization.PacketDecoder;
import pt.up.fe.cpd.g13.common.network.serialization.PacketEncoder;

import java.nio.ByteBuffer;

public class PlayRequestPacket {

    public static class Encoder implements PacketEncoder<PlayRequestPacket> {
        @Override
        public void encode(PlayRequestPacket packet, ByteBuffer buffer) {}
    }

    public static class Decoder implements PacketDecoder<PlayRequestPacket> {
        @Override
        public PlayRequestPacket decode(ByteBuffer buffer) {
            return new PlayRequestPacket();
        }
    }
}
