package com.ukarim.smppgui.util;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class ByteUtils {

    private ByteUtils() {}

    public static void putCStr(ByteBuffer buffer, String s) {
        if (s != null) {
            buffer.put(s.getBytes(StandardCharsets.UTF_8));
        }
        buffer.put((byte) 0); // null terminator
    }

    // TODO works only for 1-byte encoding
    public static String readCStr(ByteBuffer buffer, int limit) {
        var builder = new StringBuilder();
        int i = 0;
        byte b;
        while (i < limit && ((b = buffer.get()) != 0)) { // stop at the null terminator
            builder.append((char) b);
            i++;
        }
        return builder.toString();
    }

    public static int cStrLen(String s) {
        // unnecessary garbage. fix later
        int len = s == null ? 0 : s.getBytes(StandardCharsets.UTF_8).length;
        return len + 1; // + null terminator
    }
}
