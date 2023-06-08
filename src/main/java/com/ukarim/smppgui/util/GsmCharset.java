package com.ukarim.smppgui.util;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;

public final class GsmCharset extends Charset {

    public static final GsmCharset INSTANCE_8BIT = new GsmCharset("GSM-8bit", false);
    public static final GsmCharset INSTANCE_7BIT = new GsmCharset("GSM-7bit", true);

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

    private final boolean packed;

    private GsmCharset(String name, boolean packed) {
        super(name, null);
        this.packed = packed;
    }

    @Override
    public boolean contains(Charset cs) {
        return false;
    }

    @Override
    public CharsetDecoder newDecoder() {
        return packed ? new Decoder7Bit(this) : new Decoder(this);
    }

    @Override
    public CharsetEncoder newEncoder() {
        return packed ? new Encoder7Bit(this) : new Encoder(this);
    }

    private static class Decoder extends CharsetDecoder {

        protected Decoder(Charset cs) {
            super(cs, 1, 2);
        }

        protected Decoder(Charset cs, float a, float b) {
            super(cs, a, b);
        }

        @Override
        protected CoderResult decodeLoop(ByteBuffer in, CharBuffer out) {
            while (in.hasRemaining()) {
                if (!out.hasRemaining()) {
                    return CoderResult.OVERFLOW;
                }
                int code = readFromIn(in);
                if (code == EXT_PREFIX) {
                    if (!in.hasRemaining()) {
                        // wait for next byte after extension prefix
                        in.position(in.position() - 1);
                        return CoderResult.UNDERFLOW;
                    }
                    int extCode = readFromIn(in);
                    out.put(EXT_CHAR_TABLE[extCode]);
                } else {
                    out.put(CHAR_TABLE[code]);
                }
            }
            return CoderResult.UNDERFLOW;
        }

        protected int readFromIn(ByteBuffer in) {
            return in.get();
        }
    }

    private static class Encoder extends CharsetEncoder {

        protected Encoder(Charset cs) {
            super(cs, 1, 2);
        }

        protected Encoder(Charset cs, float a, float b) {
            super(cs, a, b);
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
                    writeToOut(code, out);
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
                            writeToOut(EXT_PREFIX, out);
                            writeToOut(extCode, out);
                        }
                    } else {
                        // use '?' sign for unsupported characters
                        writeToOut(0x3F, out);
                    }
                }
            }
            return CoderResult.UNDERFLOW;
        }

        protected void writeToOut(int code, ByteBuffer out) {
            out.put((byte) code);
        }
    }

    /* 7bit encoding & decoding algorithms are heavily inspired by cloudhopper's GSMBitPacker utility */

    private static class Decoder7Bit extends Decoder {

        private byte prevCode;
        private byte bits;
        private int bitpos = -1;

        protected Decoder7Bit(Charset cs) {
            super(cs, 8.0f/7.0f, 8.0f/7.0f);
        }

        @Override
        protected CoderResult implFlush(CharBuffer out) {
            byte code = (byte) (((bits & 0xFF) >> bitpos) & 0x7F);
            if (code != 0) {
                char[] table = EXT_PREFIX == prevCode ? EXT_CHAR_TABLE : CHAR_TABLE;
                out.put(table[code]);
            }
            return CoderResult.UNDERFLOW;
        }

        @Override
        protected void implReset() {
            bits = 0;
            bitpos = -1;
        }

        @Override
        protected int readFromIn(ByteBuffer in) {
            if (bitpos == -1 || bitpos == 0) {
                bits = in.get();
                bitpos = 0;
            }
            byte code = (byte) (((bits & 0xFF) >> bitpos) & 0x7F);
            if (bitpos >= 2) {
                bits = in.get();
                code |= (byte) ((bits << (8 - bitpos)) & 0x7F);
            }
            bitpos = (bitpos + 7) % 8;
            this.prevCode = code;
            return (int) code & 0x0000ff;
        }
    }

    private static class Encoder7Bit extends Encoder {

        private byte packed;
        private int bitpos;

        protected Encoder7Bit(Charset cs) {
            super(cs, 7.0f/8.0f, 14.0f/8.0f);
        }

        @Override
        protected CoderResult implFlush(ByteBuffer out) {
            out.put(packed);
            return CoderResult.UNDERFLOW;
        }

        @Override
        protected void implReset() {
            packed = 0;
            bitpos = 0;
        }

        @Override
        protected void writeToOut(int code, ByteBuffer out) {
            byte b = (byte) (code & 0x7F);
            packed |= (byte) ((b & 0xFF) << bitpos);
            if (bitpos >= 2) {
                out.put(packed);
                packed = (byte) (b >> (8 - bitpos));
            }
            bitpos = (bitpos + 7) % 8;
            if (bitpos == 0) {
                out.put(packed);
                packed = 0;
            }
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
