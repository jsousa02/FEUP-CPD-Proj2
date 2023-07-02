package pt.up.fe.cpd.g13.common.network.serialization;

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.util.Optional;

public interface PacketDecoder<PacketType> {
    PacketType decode(ByteBuffer buffer);
}
