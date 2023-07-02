package pt.up.fe.cpd.g13.common.network.serialization;

import pt.up.fe.cpd.g13.common.network.packet.UnknownPacket;
import pt.up.fe.cpd.g13.common.network.utils.PacketRegistry;
import pt.up.fe.cpd.g13.common.network.packet.PacketHeader;
import pt.up.fe.cpd.g13.common.network.packet.auth.AuthRequestPacket;
import pt.up.fe.cpd.g13.common.network.packet.auth.AuthResponsePacket;
import pt.up.fe.cpd.g13.common.network.packet.game.*;

import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class Serializer {
    public static Serializer createDefaultSerializer() {
        var serializer = new Serializer();

        var adapters = List.of(
                new SerializationAdapter<>(PacketHeader.class, new PacketHeader.Encoder(), new PacketHeader.Decoder()),
                new SerializationAdapter<>(UnknownPacket.class, new UnknownPacket.Encoder(), new UnknownPacket.Decoder()),
                new SerializationAdapter<>(AuthRequestPacket.class, new AuthRequestPacket.Encoder(), new AuthRequestPacket.Decoder()),
                new SerializationAdapter<>(AuthResponsePacket.class, new AuthResponsePacket.Encoder(), new AuthResponsePacket.Decoder()),
                new SerializationAdapter<>(GameAbortPacket.class, new GameAbortPacket.Encoder(), new GameAbortPacket.Decoder()),
                new SerializationAdapter<>(GameEndPacket.class, new GameEndPacket.Encoder(), new GameEndPacket.Decoder()),
                new SerializationAdapter<>(GameStartPacket.class, new GameStartPacket.Encoder(), new GameStartPacket.Decoder()),
                new SerializationAdapter<>(GameUpdatePacket.class, new GameUpdatePacket.Encoder(), new GameUpdatePacket.Decoder()),
                new SerializationAdapter<>(PlayRequestPacket.class, new PlayRequestPacket.Encoder(), new PlayRequestPacket.Decoder()),
                new SerializationAdapter<>(PlayResponsePacket.class, new PlayResponsePacket.Encoder(), new PlayResponsePacket.Decoder())
        );

        adapters.forEach(serializer::registerPacket);
        return serializer;
    }

    private final Map<Class<?>, SerializationAdapter<?>> supportedPackets = new HashMap<>();
    private final ByteBuffer temporaryBuffer = ByteBuffer.allocate(1024);

    public void registerPacket(SerializationAdapter<?> adapter) {
        supportedPackets.put(adapter.packetType(), adapter);
    }

    @SuppressWarnings("unchecked")
    private <T> PacketDecoder<T> getDecoderForPacketType(Class<T> packetType) {
        var adapter = supportedPackets.get(packetType);
        if (adapter == null)
            return null;

        return (PacketDecoder<T>) adapter.decoder();
    }

    @SuppressWarnings("unchecked")
    private <T> PacketEncoder<T> getEncoderForPacketType(Class<T> packetType) {
        var adapter = supportedPackets.get(packetType);
        if (adapter == null) {
            return null;
        }

        return (PacketEncoder<T>) adapter.encoder();
    }

    public Object decodePacket(ByteBuffer buffer) {
        var headerDecoder = getDecoderForPacketType(PacketHeader.class);
        assert headerDecoder != null;

        var header = headerDecoder.decode(buffer);

        var packetType = PacketRegistry.getPacketFromId(header.packetId());

        var payloadDecoder = getDecoderForPacketType(packetType);
        assert payloadDecoder != null;

        return payloadDecoder.decode(buffer);
    }

    public Optional<Object> tryToDecodePacket(ByteBuffer buffer) {
        try {
            return Optional.of(decodePacket(buffer));
        } catch (BufferUnderflowException e) {
            return Optional.empty();
        }
    }

    public <T> void encodePacket(T packet, ByteBuffer buffer) {
        @SuppressWarnings("unchecked")
        var packetType = (Class<T>) packet.getClass();

        var payloadEncoder = getEncoderForPacketType(packetType);
        assert payloadEncoder != null;

        payloadEncoder.encode(packet, temporaryBuffer);

        temporaryBuffer.flip();

        var packetId = PacketRegistry.getIdFromPacketType(packetType);

        var header = new PacketHeader(packetId, (short) temporaryBuffer.remaining());
        var headerEncoder = getEncoderForPacketType(PacketHeader.class);
        assert headerEncoder != null;

        headerEncoder.encode(header, buffer);

        buffer.put(temporaryBuffer);
        temporaryBuffer.clear();
    }

    public <T> boolean tryToEncodePacket(T packet, ByteBuffer buffer) {
        try {
            encodePacket(packet, buffer);
            return true;
        } catch (BufferOverflowException e) {
            return false;
        }
    }
}
