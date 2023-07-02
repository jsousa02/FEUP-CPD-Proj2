package pt.up.fe.cpd.g13.common.utils;

import java.nio.ByteBuffer;

public class DebugUtils {

    public static void printBuffer(ByteBuffer buffer) {
        var dupBuffer = buffer.duplicate();

        var currPos = dupBuffer.position();
        var currLimit = dupBuffer.limit();

        dupBuffer.position(0);
        dupBuffer.limit(dupBuffer.capacity());

        var builder = new StringBuilder();
        for (int i = 0; i < dupBuffer.capacity(); i++) {
            var currByte = Integer.toString(dupBuffer.get(), 16);

            if (i == currPos) builder.append("[ ");
            if (i == currLimit) builder.append("] ");
            builder.append(currByte).append(' ');
        }

        System.out.println(builder.toString());

    }
}
