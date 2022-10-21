package com.ukarim.smppgui.protocol.pdu;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class SubmitSmPduTest {

    @Test
    void testToByteBuffer() {
        var expected = new byte[] {
                0x00, 0x00, 0x00, 0x4F, // command_length
                0x00, 0x00, 0x00, 0x04, // command_id
                0x00, 0x00, 0x00, 0x00, // command_status
                0x00, 0x00, 0x00, 0x66, // sequence_number
                0x73, 0x6D, 0x70, 0x70, 0x67, 0x75, 0x69, 0x00, // service_type
                0x00, 0x00, 0x31, 0x30, 0x30, 0x31, 0x00, // source_addr_ton, source_addr_npi, source_addr
                0x00, 0x00, 0x37, 0x37, 0x30, 0x31, 0x32, 0x31, 0x31, 0x30, 0x30, 0x30, 0x30, 0x00, // dest_addr_ton, dest_addr_npi, destination_addr
                0x00, // esm class
                0x00, // protocol_id
                0x02, // priority_flag
                0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x35, 0x30, 0x30, 0x30, 0x30, 0x30, 0x52, 0x00, // schedule_delivery_time
                0x00, // validity_period
                0x01, // registered_delivery
                0x00, // replace_if_present_flag
                0x08, // data_coding
                0x00, // sm_default_msg_id
                0x08, // sm_length
                0x00, 0x54, 0x00, 0x65, 0x00, 0x73, 0x00, 0x74, // short_message
        };
        Address srcAddr = new Address((byte) 0x00, (byte) 0x00, "1001");
        Address destAddr = new Address((byte) 0x00, (byte) 0x00, "77012110000");
        var submitSm = new SubmitSmPdu(102, srcAddr, destAddr, "Test");
        submitSm.setServiceType("smppgui");
        submitSm.setPriorityFlag((byte) 0x02);
        submitSm.setRegisteredDelivery((byte) 0x01);
        submitSm.setScheduleDeliveryTime("000000000500000R");

        byte[] actual = submitSm.toByteBuffer().array();

        Assertions.assertArrayEquals(expected, actual);
    }
}