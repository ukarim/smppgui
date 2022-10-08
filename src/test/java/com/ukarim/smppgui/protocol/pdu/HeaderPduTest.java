package com.ukarim.smppgui.protocol.pdu;

import com.ukarim.smppgui.protocol.SmppCmd;
import com.ukarim.smppgui.protocol.SmppStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class HeaderPduTest {

    @Test
    void testToByteBuffer() {
        var expected = new byte[] {
                0x00, 0x00, 0x00, 0x10,
                (byte) 0x80, 0x00, 0x00, 0x00,
                0x00, 0x00, 0x00, 0x03,
                0x00, 0x00, 0x00, (byte) 0xEA
        };
        var genericNack = new HeaderPdu(SmppCmd.GENERIC_NACK, SmppStatus.ESME_RINVCMDID, 234);
        byte[] actual = genericNack.toByteBuffer().array();
        Assertions.assertArrayEquals(expected, actual);
    }
}
