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

    public static short shortFromHex(String str) {
        byte[] bytes = bytesFromHex(str);
        byte b1 = bytes[0];
        byte b2 = bytes[1];
        return (short) (((b1 & 0xFF) << 8) | (b2 & 0xFF));
    }

    public static byte[] bytesFromHex(String str) {
        str = str.replaceAll("0x", "");
        int len = str.length();
        byte[] out = new byte[len/2];
        for (int i = 0; i < len; i = i + 2) {
            char c1 = str.charAt(i);
            char c2 = str.charAt(i + 1);
            out[i/2] = (byte) Integer.parseInt(("" + c1) + c2, 16);
        }
        return out;
    }
}
