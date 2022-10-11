package com.ukarim.smppgui.protocol;

import com.ukarim.smppgui.protocol.pdu.Pdu;
import java.nio.ByteBuffer;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class PduParserTest {

    @Test
    void testParseHeaderPdu() throws SmppException {
        ByteBuffer buffer = ByteBuffer.wrap(new byte[]{
                0x00, 0x00, 0x00, 0x10,
                (byte) 0x80, 0x00, 0x00, 0x00,
                0x00, 0x00, 0x00, 0x03,
                0x00, 0x00, 0x00, (byte) 0xEA
        }, 16, 0);
        List<Pdu> pdus = PduParser.parsePdu(buffer);
        Assertions.assertEquals(1, pdus.size());
        Pdu pdu = pdus.get(0);
        Assertions.assertEquals(SmppCmd.GENERIC_NACK, pdu.getCmd());
        Assertions.assertEquals(SmppStatus.ESME_RINVCMDID, pdu.getSts());
        Assertions.assertEquals(234, pdu.getSeqNum());
    }
}