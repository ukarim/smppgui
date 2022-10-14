package com.ukarim.smppgui.protocol.pdu;

public class Address {

    private final byte ton;

    private final byte npi;

    private final String addr;

    public Address(byte ton, byte npi, String addr) {
        this.ton = ton;
        this.npi = npi;
        this.addr = addr;
    }

    public byte getTon() {
        return ton;
    }

    public byte getNpi() {
        return npi;
    }

    public String getAddr() {
        return addr;
    }
}
