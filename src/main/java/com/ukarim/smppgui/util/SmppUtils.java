package com.ukarim.smppgui.util;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class SmppUtils {

    private static final int UDH_MAX_CONTENT_LENGTH = 134;

    private static final int VALID_TIME_LEN = 16;
    private static final String VALID_TIME_REGEX = "[0-9][0-9][0-1][0-9][0-3][0-9][0-2][0-9][0-5][0-9][0-5][0-9][0-9][0-4][0-9](\\+|\\-|R)";

    private SmppUtils() {}

    public static boolean isValidSmppTime(String s) {
        if (s.length() != VALID_TIME_LEN) {
            return false;
        }
        return s.matches(VALID_TIME_REGEX);
    }

    public static byte[] toUcs2Bytes(String s) {
        return s.getBytes(StandardCharsets.UTF_16BE);
    }

    public static List<byte[]> toUdhPartsInUcs2(String s) {
        if (s == null) {
            return Collections.emptyList();
        }
        int len = s.length();
        int count = (int) Math.ceil(len / UDH_MAX_CONTENT_LENGTH);
        var partsList = new ArrayList<byte[]>(count);

        byte[] bytes = toUcs2Bytes(s);
        for (int i = 0; i < count; i++) {
            int startIdx = i * UDH_MAX_CONTENT_LENGTH;
            int endIdx = Math.min(startIdx + UDH_MAX_CONTENT_LENGTH, len);
            int partLen = endIdx - startIdx;
            byte[] udhPart = new byte[partLen + 6]; // plus 6 for udh headers
            udhPart[0] = 0x05;
            udhPart[1] = 0x00;
            udhPart[2] = 0x03;
            udhPart[3] = 0x01; // maybe accept id as method argument?
            udhPart[4] = (byte) count;
            udhPart[5] = (byte) i;
            System.arraycopy(bytes, startIdx, udhPart, 6, partLen);
            partsList.add(udhPart);
        }
        return partsList;
    }
}
