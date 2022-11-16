package com.ukarim.smppgui.protocol;

public final class Tlv {

    private final short tag;
    private final short len;
    private final byte[] value;

    public Tlv(short tag, short len, byte[] value) {
        this.tag = tag;
        this.len = len;
        this.value = value;
    }

    public short getTag() {
        return tag;
    }

    public short getLen() {
        return len;
    }

    public byte[] getValue() {
        return value;
    }
}
