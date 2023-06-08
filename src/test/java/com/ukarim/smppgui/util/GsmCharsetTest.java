package com.ukarim.smppgui.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


class GsmCharsetTest {

    private static final String GSM_7_ALPHABET = "@£$¥èéùìòÇ\nØø\rÅåΔ_ΦΓΛΩΠΨΣΘΞÆæßÉ !\"#¤%&'()*+,-./0123456789:;<=>?¡ABCDEFGHIJKLMNOPQRSTUVWXYZÄÖÑÜ§¿abcdefghijklmnopqrstuvwxyzäöñüà^{}\\[~]|€";

    private static final byte[] GSM_7_ALPHABET_BYTES = new byte[] {
            0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x0A, 0x0B, 0x0C, 0x0D,
            0x0E, 0x0F, 0x10, 0x11, 0x12, 0x13, 0x14, 0x15, 0x16, 0x17, 0x18, 0x19, 0x1A, 0x1C,
            0x1D, 0x1E, 0x1F, 0x20, 0x21, 0x22, 0x23, 0x24, 0x25, 0x26, 0x27, 0x28, 0x29, 0x2A,
            0x2B, 0x2C, 0x2D, 0x2E, 0x2F, 0x30, 0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37, 0x38,
            0x39, 0x3A, 0x3B, 0x3C, 0x3D, 0x3E, 0x3F, 0x40, 0x41, 0x42, 0x43, 0x44, 0x45, 0x46,
            0x47, 0x48, 0x49, 0x4A, 0x4B, 0x4C, 0x4D, 0x4E, 0x4F, 0x50, 0x51, 0x52, 0x53, 0x54,
            0x55, 0x56, 0x57, 0x58, 0x59, 0x5A, 0x5B, 0x5C, 0x5D, 0x5E, 0x5F, 0x60, 0x61, 0x62,
            0x63, 0x64, 0x65, 0x66, 0x67, 0x68, 0x69, 0x6A, 0x6B, 0x6C, 0x6D, 0x6E, 0x6F, 0x70,
            0x71, 0x72, 0x73, 0x74, 0x75, 0x76, 0x77, 0x78, 0x79, 0x7A, 0x7B, 0x7C, 0x7D, 0x7E,
            0x7F, 0x1B, 0x14, 0x1B, 0x28, 0x1B, 0x29, 0x1B, 0x2F, 0x1B, 0x3C, 0x1B, 0x3D, 0x1B,
            0x3E, 0x1B, 0x40, 0x1B, 0x65
    };

    private static final byte[] GSM_7_ALPHABET_BYTES_PACKED = new byte[] {
            -128, -128, 96, 64, 40, 24, 14, -120, -124, 98, -63, 104, 56, 30, -112, -120, 100, 66, -87, 88, 46, -104, -116, -122, -45, -15, 124, 64, 33, -47, -120, 84, 50, -99, 80, 41, -43, -118, -43, 114, -67, 96, 49, -39, -116, 86, -77, -35, 112, 57, -35, -114, -41, -13, -3, -128, 65, -31, -112, 88, 52, 30, -111, 73, -27, -110, -39, 116, 62, -95, 81, -23, -108, 90, -75, 94, -79, 89, -19, -106, -37, -11, 126, -63, 97, -15, -104, 92, 54, -97, -47, 105, -11, -102, -35, 118, -65, -31, 113, -7, -100, 94, -73, -33, -15, 121, -3, -98, -33, -9, -1, 55, -108, 13, 106, -109, -38, -68, 54, -68, 77, 111, -29, -37, 0, 55, 101
    };

    private byte[] readResource(String f) throws Exception {
        Path p = Paths.get(GsmCharsetTest.class.getResource(f).toURI());
        return Files.readAllBytes(p);
    }

    @Test
    void checkEncode() {
        byte[] encoded = GSM_7_ALPHABET.getBytes(GsmCharset.INSTANCE_8BIT);
        Assertions.assertEquals(GSM_7_ALPHABET_BYTES.length, encoded.length);
        Assertions.assertArrayEquals(GSM_7_ALPHABET_BYTES, encoded);
    }

    @Test
    void checkEncodeLoremIpsum() throws Exception {
        String text = new String(readResource("/gsm/lorem_ipsum.txt"), StandardCharsets.US_ASCII);
        byte[] expected = readResource("/gsm/lorem_ipsum.8bit.bin");
        Assertions.assertArrayEquals(expected, text.getBytes(GsmCharset.INSTANCE_8BIT));
    }

    @Test
    void checkDecode() {
        Assertions.assertEquals(GSM_7_ALPHABET, new String(GSM_7_ALPHABET_BYTES, GsmCharset.INSTANCE_8BIT));
    }

    @Test
    void checkDecodeLoremIpsum() throws Exception {
        String text = new String(readResource("/gsm/lorem_ipsum.txt"), StandardCharsets.US_ASCII);
        Assertions.assertEquals(text, new String(readResource("/gsm/lorem_ipsum.8bit.bin"), GsmCharset.INSTANCE_8BIT));
    }

    @Test
    void checkDecode7Bit() {
        Assertions.assertEquals(GSM_7_ALPHABET, new String(GSM_7_ALPHABET_BYTES_PACKED, GsmCharset.INSTANCE_7BIT));
    }

    @Test
    void checkDecode7bitLoremIpsum() throws Exception {
        String text = new String(readResource("/gsm/lorem_ipsum.txt"), StandardCharsets.US_ASCII);
        Assertions.assertEquals(text, new String(readResource("/gsm/lorem_ipsum.7bit.bin"), GsmCharset.INSTANCE_7BIT));
    }

    @Test
    void checkEncode7Bit() {
        Assertions.assertArrayEquals(GSM_7_ALPHABET_BYTES_PACKED, GSM_7_ALPHABET.getBytes(GsmCharset.INSTANCE_7BIT));
    }

    @Test
    void checkEncode7bitLoremIpsum() throws Exception {
        String text = new String(readResource("/gsm/lorem_ipsum.txt"), StandardCharsets.US_ASCII);
        byte[] expected = readResource("/gsm/lorem_ipsum.7bit.bin");
        Assertions.assertArrayEquals(expected, text.getBytes(GsmCharset.INSTANCE_7BIT));
    }
}
