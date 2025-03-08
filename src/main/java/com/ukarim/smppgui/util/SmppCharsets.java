package com.ukarim.smppgui.util;

import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.charset.UnsupportedCharsetException;
import java.util.List;
import java.util.Map;

public final class SmppCharsets {

    private SmppCharsets() {}

    private static final Map<String, Charset> CHARSET_MAP = Map.of(
            GsmCharset.INSTANCE_8BIT.name(), GsmCharset.INSTANCE_8BIT,
            GsmCharset.INSTANCE_7BIT.name(), GsmCharset.INSTANCE_7BIT,
            "IA5:ASCII" , new CharsetWrapper("IA5:ASCII", StandardCharsets.US_ASCII),
            "UCS2" , new CharsetWrapper("UCS2", StandardCharsets.UTF_16BE),
            "LATIN-1:ISO-8859-1" , new CharsetWrapper("LATIN-1:ISO-8859-1", StandardCharsets.ISO_8859_1)
    );

    public static Charset forName(String name) {
        var c = CHARSET_MAP.get(name);
        if (c == null) {
            throw new UnsupportedCharsetException("Unknown SMPP charset: " + name);
        }
        return c;
    }

    public static List<Charset> getCharsets() {
        return List.copyOf(CHARSET_MAP.values());
    }

    private static class CharsetWrapper extends Charset {

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
}
