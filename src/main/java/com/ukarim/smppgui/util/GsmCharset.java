package com.ukarim.smppgui.util;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;

public final class GsmCharset extends Charset {

    public static final GsmCharset INSTANCE = new GsmCharset();

    private static final byte EXT_PREFIX = 0x1B;

    private static final char[] CHAR_TABLE = new char[] {
            // char at index 27 (0x1B) is not used, but displayed as a space

            '@', '£', '$', '¥', 'è', 'é', 'ù', 'ì', 'ò', 'Ç', '\n', 'Ø', 'ø', '\r', 'Å', 'å',
            'Δ', '_', 'Φ', 'Γ', 'Λ', 'Ω', 'Π', 'Ψ', 'Σ', 'Θ', 'Ξ', ' ', 'Æ', 'æ', 'ß', 'É',
            ' ', '!', '"', '#', '¤', '%', '&', '\'', '(', ')', '*', '+', ',', '-', '.', '/',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', ':', ';', '<', '=', '>', '?',
            '¡', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O',
            'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'Ä', 'Ö', 'Ñ', 'Ü', '§',
            '¿', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o',
            'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'ä', 'ö', 'ñ', 'ü', 'à'
    };

    private static final char[] EXT_CHAR_TABLE = new char[] {
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, '\n', 0, 0, 0, 0, 0,
            0, 0, 0, 0, '^', 0, 0, 0, 0, 0, 0, ' ', 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, '{', '}', 0, 0, 0, 0, 0, '\\',
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, '[', '~', ']', 0,
            '|', 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, '€', 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
    };

    private GsmCharset() {
        super("GSM-7", null);
    }

    @Override
    public boolean contains(Charset cs) {
        return false;
    }

    @Override
    public CharsetDecoder newDecoder() {
        return new Decoder(this);
    }

    @Override
    public CharsetEncoder newEncoder() {
        return new Encoder(this);
    }

    private static class Decoder extends CharsetDecoder {

        protected Decoder(Charset cs) {
            super(cs, 1, 2);
        }

        @Override
        protected CoderResult decodeLoop(ByteBuffer in, CharBuffer out) {
            while (in.hasRemaining()) {
                if (!out.hasRemaining()) {
                    return CoderResult.OVERFLOW;
                }
                byte code = in.get();
                if (code == EXT_PREFIX) {
                    if (!in.hasRemaining()) {
                        // wait for next byte after extension prefix
                        in.position(in.position() - 1);
                        return CoderResult.UNDERFLOW;
                    }
                    byte extCode = in.get();
                    out.put(EXT_CHAR_TABLE[extCode]);
                } else {
                    out.put(CHAR_TABLE[code]);
                }
            }
            return CoderResult.UNDERFLOW;
        }
    }

    private static class Encoder extends CharsetEncoder {

        protected Encoder(Charset cs) {
            super(cs, 1, 2);
        }

        @Override
        protected CoderResult encodeLoop(CharBuffer in, ByteBuffer out) {
            while (in.hasRemaining()) {
                if (!out.hasRemaining()) {
                    return CoderResult.OVERFLOW;
                }
                char ch = in.get();
                int code = findCharCode(CHAR_TABLE, ch);
                if (code != -1) {
                    out.put((byte) code);
                } else {
                    int extCode = findCharCode(EXT_CHAR_TABLE, ch);
                    if (extCode != -1) {
                        if (out.remaining() < 2) {
                            // need more space

                            // unread last character
                            // will read it again at the next cycle
                            in.position(in.position() - 1);
                            return CoderResult.OVERFLOW;
                        } else {
                            out.put(EXT_PREFIX);
                            out.put((byte) extCode);
                        }
                    } else {
                        // use '?' sign for unsupported characters
                        out.put((byte) 0x3F);
                    }
                }
            }
            return CoderResult.UNDERFLOW;
        }
    }

    private static int findCharCode(char[] charTable, char ch) {
        int res = -1;
        int len = charTable.length;
        if (ch == ' ') {
            // because 0x1B (extension prefix) also represented as space inside CHAR_TABLE
            return 32;
        }

        // search char table backwards
        // most of commonly used characters are at the end of array
        for (int i = len - 1; i >= 0; i--) {
            if (charTable[i] == ch) {
                res = i;
                break;
            }
        }
        return res;
    }
}
