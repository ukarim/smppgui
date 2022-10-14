package com.ukarim.smppgui.util;

import com.ukarim.smppgui.protocol.SmppCmd;
import com.ukarim.smppgui.protocol.SmppStatus;
import com.ukarim.smppgui.protocol.pdu.HeaderPdu;

public final class FmtUtils {

    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();

    private FmtUtils() {}

    public static String toHexString(byte... bytes) {
        if (bytes == null) {
            return "null";
        }
        char[] hexChars = new char[bytes.length * 2];
        for (int i = 0; i < bytes.length; i++) {
            int v = bytes[i] & 0xFF;
            hexChars[i * 2] = HEX_ARRAY[v >>> 4];
            hexChars[i * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static String fmtInt(int n) {
        byte b4 = (byte) (n >> 24);
        byte b3 = (byte) (n >> 16);
        byte b2 = (byte) (n >> 8);
        byte b1 = (byte) (n >> 0);
        return "0x" + toHexString(b4, b3, b2, b1);
    }

    public static String fmtByte(byte n) {
        return "0x" + toHexString(n);
    }

    public static String fmtHeaderPdu(HeaderPdu pdu) {
        var builder = new StringBuilder();
        SmppCmd cmd = pdu.getCmd();
        SmppStatus sts = pdu.getSts();
        builder
                .append("---\n")
                .append("command_id:      ")
                .append(fmtInt(cmd.getCmdId())).append(" (").append(cmd).append(")\n")
                .append("command_status:  ")
                .append(fmtInt(sts.getStatusId())).append(" (").append(sts.getStatusDesc()).append(")\n")
                .append("sequence_number: ")
                .append(pdu.getSeqNum())
                .append("\n")
                .append("---");
        return builder.toString();
    }
}
