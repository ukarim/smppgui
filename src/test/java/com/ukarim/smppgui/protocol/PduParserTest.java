package com.ukarim.smppgui.protocol;

import com.ukarim.smppgui.protocol.pdu.BindRespPdu;
import com.ukarim.smppgui.protocol.pdu.DeliverSmPdu;
import com.ukarim.smppgui.protocol.pdu.Pdu;
import com.ukarim.smppgui.protocol.pdu.SubmitSmRespPdu;
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

        Assertions.assertFalse(buffer.hasRemaining());
    }

    @Test
    void testParseBindRespPdu() throws SmppException {
        byte[] arr = new byte[] {
                0x00, 0x00, 0x00, 0x1A,
                (byte) 0x80, 0x00, 0x00, 0x02,
                0x00, 0x00, 0x00, 0x00,
                0x00, 0x00, 0x00, (byte) 0xEA,
                0x74, 0x65, 0x73, 0x74, 0x5F, 0x73, 0x6D, 0x73, 0x63, 0x00
        };
        ByteBuffer buffer = ByteBuffer.wrap(arr, arr.length, 0);
        List<Pdu> pdus = PduParser.parsePdu(buffer);
        Assertions.assertEquals(1, pdus.size());
        Pdu pdu = pdus.get(0);
        Assertions.assertEquals(SmppCmd.BIND_TRANSMITTER_RESP, pdu.getCmd());
        Assertions.assertEquals(SmppStatus.ESME_ROK, pdu.getSts());
        Assertions.assertEquals(234, pdu.getSeqNum());
        Assertions.assertEquals(BindRespPdu.class, pdu.getClass());
        Assertions.assertEquals("test_smsc", ((BindRespPdu) pdu).getSystemId());

        Assertions.assertFalse(buffer.hasRemaining());
    }

    @Test
    void testParseSubmitSmRespPduFailed() throws SmppException {
        byte[] arr = new byte[] {
                0x00, 0x00, 0x00, 0x10,
                (byte) 0x80, 0x00, 0x00, 0x04,
                0x00, 0x00, 0x00, 0x45,
                0x00, 0x00, 0x00, (byte) 0xEA
        };
        ByteBuffer buffer = ByteBuffer.wrap(arr, arr.length, 0);
        List<Pdu> pdus = PduParser.parsePdu(buffer);
        Assertions.assertEquals(1, pdus.size());
        Pdu pdu = pdus.get(0);
        Assertions.assertEquals(SmppCmd.SUBMIT_SM_RESP, pdu.getCmd());
        Assertions.assertEquals(SmppStatus.ESME_RSUBMITFAIL, pdu.getSts());
        Assertions.assertEquals(234, pdu.getSeqNum());
        Assertions.assertEquals(SubmitSmRespPdu.class, pdu.getClass());
        Assertions.assertFalse(buffer.hasRemaining());
    }

    @Test
    void testParseSubmitSmRespPduSucceed() throws SmppException {
        byte[] arr = new byte[] {
                0x00, 0x00, 0x00, 0x1A,
                (byte) 0x80, 0x00, 0x00, 0x04,
                0x00, 0x00, 0x00, 0x45,
                0x00, 0x00, 0x00, (byte) 0xEA,
                0x39, 0x31, 0x38, 0x32, 0x37, 0x33, 0x36, 0x34, 0x35, 0x00
        };
        ByteBuffer buffer = ByteBuffer.wrap(arr, arr.length, 0);
        List<Pdu> pdus = PduParser.parsePdu(buffer);
        Assertions.assertEquals(1, pdus.size());
        Pdu pdu = pdus.get(0);
        Assertions.assertEquals(SmppCmd.SUBMIT_SM_RESP, pdu.getCmd());
        Assertions.assertEquals(SmppStatus.ESME_RSUBMITFAIL, pdu.getSts());
        Assertions.assertEquals(234, pdu.getSeqNum());
        Assertions.assertEquals(SubmitSmRespPdu.class, pdu.getClass());
        Assertions.assertEquals("918273645", ((SubmitSmRespPdu) pdu).getMessageId());
        Assertions.assertFalse(buffer.hasRemaining());
    }

    @Test
    void testParseDeliverSmPdu() throws SmppException {
        byte[] arr = new byte[] {
                0x00, 0x00, 0x00, 0x45, // command_length
                0x00, 0x00, 0x00, 0x05, // command_id
                0x00, 0x00, 0x00, 0x00, // command_status
                0x00, 0x00, 0x00, 0x66, // sequence_number
                0x73, 0x6d, 0x73, 0x63, 0x73, 0x69, 0x6d, 0x00, // service_type
                0x00, 0x00, 0x37, 0x37, 0x30, 0x31, 0x32, 0x31, 0x31, 0x30, 0x30, 0x30, 0x30, 0x00, // source_addr_ton, source_addr_npi, source_addr
                0x00, 0x00, 0x31, 0x30, 0x30, 0x31, 0x00, // dest_addr_ton, dest_addr_npi, destination_addr
                0x00, // esm class
                0x00, // protocol_id
                0x01, // priority_flag
                0x00, // schedule_delivery_time
                0x00, // validity_period
                0x00, // registered_delivery
                0x00, // replace_if_present_flag
                0x00, // data_coding
                0x00, // sm_default_msg_id
                0x04, // sm_length
                0x54, 0x65, 0x73, 0x74, // short_message
                0x02, 0x01, 0x00, 0x01, 0x03, // privacy_indicator tlv
                0x12, 0x04, 0x00, 0x01, 0x02, // ms_validity tlv
        };
        ByteBuffer buffer = ByteBuffer.wrap(arr, arr.length, 0);

        List<Pdu> pdus = PduParser.parsePdu(buffer);
        Assertions.assertEquals(1, pdus.size());
        Pdu pdu = pdus.get(0);
        Assertions.assertEquals(SmppCmd.DELIVER_SM, pdu.getCmd());
        Assertions.assertEquals(SmppStatus.ESME_ROK, pdu.getSts());
        Assertions.assertEquals(102, pdu.getSeqNum());
        Assertions.assertEquals(DeliverSmPdu.class, pdu.getClass());

        DeliverSmPdu deliverSmPdu = (DeliverSmPdu) pdu;
        Assertions.assertEquals("smscsim", deliverSmPdu.getServiceType());
        Assertions.assertEquals(0, deliverSmPdu.getSrcAddress().getTon());
        Assertions.assertEquals(0, deliverSmPdu.getSrcAddress().getNpi());
        Assertions.assertEquals("77012110000", deliverSmPdu.getSrcAddress().getAddr());
        Assertions.assertEquals(0, deliverSmPdu.getDestAddress().getTon());
        Assertions.assertEquals(0, deliverSmPdu.getDestAddress().getNpi());
        Assertions.assertEquals("1001", deliverSmPdu.getDestAddress().getAddr());
        Assertions.assertEquals(0, deliverSmPdu.getProtocolId());
        Assertions.assertEquals(1, deliverSmPdu.getPriorityFlag());
        Assertions.assertEquals("", deliverSmPdu.getScheduleDeliveryTime());
        Assertions.assertEquals("", deliverSmPdu.getValidityPeriod());
        Assertions.assertEquals(0, deliverSmPdu.getRegisteredDelivery());
        Assertions.assertArrayEquals(new byte[]{ 0x54, 0x65, 0x73, 0x74 }, deliverSmPdu.getShortMessage());

        Assertions.assertFalse(buffer.hasRemaining());
    }
}
