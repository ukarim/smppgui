package com.ukarim.smppgui.protocol.pdu;

import com.ukarim.smppgui.protocol.SmppStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class DeliverSmRespPduTest {

    @Test
    void testToByteBuffer() {
        var expected = new byte[] {
                0x00, 0x00, 0x00, 0x11,
                (byte) 0x80, 0x00, 0x00, 0x05,
                0x00, 0x00, 0x00, 0x00,
                0x00, 0x00, 0x00, (byte) 0x84,
                0x00
        };
        var deliverSmResp = new DeliverSmRespPdu(SmppStatus.ESME_ROK, 132);
        byte[] actual = deliverSmResp.toByteBuffer().array();
        Assertions.assertArrayEquals(expected, actual);
    }
}