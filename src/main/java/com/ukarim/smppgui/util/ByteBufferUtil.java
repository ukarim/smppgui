package com.ukarim.smppgui.util;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class ByteBufferUtil {

    private ByteBufferUtil() {}

    public static void putCStr(ByteBuffer buffer, String s) {
        if (s != null) {
            buffer.put(s.getBytes(StandardCharsets.UTF_8));
        }
        buffer.put((byte) 0); // null terminator
    }
}
