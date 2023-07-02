package pt.up.fe.cpd.g13.common.network.serialization;

public record SerializationAdapter<PacketType>(
        Class<PacketType> packetType,
        PacketEncoder<PacketType> encoder,
        PacketDecoder<PacketType> decoder) {
}
