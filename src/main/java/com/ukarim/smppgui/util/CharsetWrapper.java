package com.ukarim.smppgui.util;

import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;

public class CharsetWrapper extends Charset {

    private final Charset cs;

    public CharsetWrapper(String name, Charset cs) {
        super(name, null);
        this.cs = cs;
    }

    @Override
    public boolean contains(Charset cs) {
        return cs.contains(cs);
    }

    @Override
    public CharsetDecoder newDecoder() {
        return cs.newDecoder();
    }

    @Override
    public CharsetEncoder newEncoder() {
        return cs.newEncoder();
    }

    @Override
    public boolean canEncode() {
        return cs.canEncode();
    }
}
