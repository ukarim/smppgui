package com.ukarim.smppgui.protocol.pdu;

import com.ukarim.smppgui.protocol.SmppCmd;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class BindPduTest {

    @Test
    void testToByteBuffer() {
        var expected = new byte[] {
                0x00, 0x00, 0x00, 0x2D,
                0x00, 0x00, 0x00, 0x02,
                0x00, 0x00, 0x00, 0x00,
                0x00, 0x00, 0x00, (byte) 0xEA,
                0x74 ,0x65 ,0x73 ,0x74 ,0x5F ,0x75 ,0x73 ,0x65 ,0x72, 0x00,
                0x73, 0x65, 0x63, 0x72, 0x65, 0x74, 0x00,
                0x73, 0x6D, 0x70, 0x70, 0x67, 0x75, 0x69, 0x00,
                0x34,
                0x00,
                0x00,
                0x00
        };
        var bindPdu = new BindPdu(SmppCmd.BIND_TRANSMITTER, 234, "test_user", "secret");
        bindPdu.setSystemType("smppgui");
        var actual = bindPdu.toByteBuffer().array();
        Assertions.assertArrayEquals(expected, actual);
    }
}