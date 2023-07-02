package pt.up.fe.cpd.g13.common.network.serialization;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class BufferUtils {

    public static void putString(ByteBuffer buffer, String value, Charset charset) {
        var rawData = charset.encode(value);
        buffer.putInt(rawData.limit());
        buffer.put(rawData);
    }

    public static void putString(ByteBuffer buffer, String value) {
        putString(buffer, value, StandardCharsets.US_ASCII);
    }

    public static String getString(ByteBuffer buffer, Charset charset) {
        var bufferLength = buffer.getInt();
        var stringBuffer = buffer.slice(buffer.position(), bufferLength);

        var decodedString = charset.decode(stringBuffer).toString();
        buffer.position(buffer.position() + bufferLength);

        return decodedString;
    }

    public static String getString(ByteBuffer buffer) {
        return getString(buffer, StandardCharsets.US_ASCII);
    }

    public static void putBoolean(ByteBuffer buffer, boolean value) {
        var encodedBoolean = (byte) (value ? 1 : 0);
        buffer.put(encodedBoolean);
    }

    public static boolean getBoolean(ByteBuffer buffer) {
        var encodedBoolean = buffer.get();
        return encodedBoolean != 0;
    }
}
