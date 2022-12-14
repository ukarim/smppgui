package com.ukarim.smppgui.util;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class StringUtils {

    private StringUtils() {}

    public static void putCStr(ByteBuffer buffer, String s) {
        if (s != null) {
            buffer.put(s.getBytes(StandardCharsets.US_ASCII));
        }
        buffer.put((byte) 0); // null terminator
    }

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
        int len = s == null ? 0 : s.getBytes(StandardCharsets.US_ASCII).length;
        return len + 1; // + null terminator
    }

    public static boolean isEmpty(String s) {
        return s == null || s.isBlank();
    }
}
