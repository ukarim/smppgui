package com.ukarim.smppgui.protocol;

public class SmppConstants {

    private SmppConstants() {}

    public static final byte SMPP_34_INTERFACE_VER = 0x34;

    public static final int MAX_PDU_LEN = 2048;

    public static final byte DATA_CODING_DEFAULT = 0x00;
    public static final byte DATA_CODING_IA5 = 0x01; // ASCII
    public static final byte DATA_CODING_LATIN1 = 0x03;
    public static final byte DATA_CODING_UCS2 = 0x08;

    public static final byte ESM_DEFAULT = 0x00;

    public static final byte ESM_UDH_MASK = 0x40; // 01000000

}
