package pt.up.fe.cpd.g13.common.network.serialization;

import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.util.Optional;

public interface PacketEncoder<PacketType> {

    void encode(PacketType packet, ByteBuffer buffer);
}
