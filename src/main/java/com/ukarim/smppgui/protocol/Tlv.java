package com.ukarim.smppgui.protocol;

import java.util.HashMap;
import java.util.Map;

public final class Tlv {

    private static final Map<Short, String> KNOWN_TLVS;

    static {
        KNOWN_TLVS = new HashMap<>();
        KNOWN_TLVS.put((short) 0x0005, "dest_addr_subunit");
        KNOWN_TLVS.put((short) 0x0006, "dest_network_type");
        KNOWN_TLVS.put((short) 0x0007, "dest_bearer_type");
        KNOWN_TLVS.put((short) 0x0008, "dest_telematics_id");
        KNOWN_TLVS.put((short) 0x000D, "source_addr_subunit");
        KNOWN_TLVS.put((short) 0x000E, "source_network_type");
        KNOWN_TLVS.put((short) 0x000F, "source_bearer_type");
        KNOWN_TLVS.put((short) 0x0010, "source_telematics_id");
        KNOWN_TLVS.put((short) 0x0017, "qos_time_to_live");
        KNOWN_TLVS.put((short) 0x0019, "payload_type");
        KNOWN_TLVS.put((short) 0x001D, "additional_status_info_text");
        KNOWN_TLVS.put((short) 0x001E, "receipted_message_id");
        KNOWN_TLVS.put((short) 0x0030, "ms_msg_wait_facilities");
        KNOWN_TLVS.put((short) 0x0201, "privacy_indicator");
        KNOWN_TLVS.put((short) 0x0202, "source_subaddress");
        KNOWN_TLVS.put((short) 0x0203, "dest_subaddress");
        KNOWN_TLVS.put((short) 0x0204, "user_message_reference");
        KNOWN_TLVS.put((short) 0x0205, "user_response_code");
        KNOWN_TLVS.put((short) 0x020A, "source_port");
        KNOWN_TLVS.put((short) 0x020B, "destination_port");
        KNOWN_TLVS.put((short) 0x020C, "sar_msg_ref_num");
        KNOWN_TLVS.put((short) 0x020D, "language_indicator");
        KNOWN_TLVS.put((short) 0x020E, "sar_total_segments");
        KNOWN_TLVS.put((short) 0x020F, "sar_segment_seqnum");
        KNOWN_TLVS.put((short) 0x0210, "SC_interface_version");
        KNOWN_TLVS.put((short) 0x0302, "callback_num_pres_ind");
        KNOWN_TLVS.put((short) 0x0303, "callback_num_atag");
        KNOWN_TLVS.put((short) 0x0304, "number_of_messages");
        KNOWN_TLVS.put((short) 0x0381, "callback_num");
        KNOWN_TLVS.put((short) 0x0420, "dpf_result");
        KNOWN_TLVS.put((short) 0x0421, "set_dpf");
        KNOWN_TLVS.put((short) 0x0422, "ms_availability_status");
        KNOWN_TLVS.put((short) 0x0423, "network_error_code");
        KNOWN_TLVS.put((short) 0x0424, "message_payload");
        KNOWN_TLVS.put((short) 0x0425, "delivery_failure_reason");
        KNOWN_TLVS.put((short) 0x0426, "more_messages_to_send");
        KNOWN_TLVS.put((short) 0x0427, "message_state");
        KNOWN_TLVS.put((short) 0x0501, "ussd_service_op");
        KNOWN_TLVS.put((short) 0x1201, "display_time");
        KNOWN_TLVS.put((short) 0x1203, "sms_signal");
        KNOWN_TLVS.put((short) 0x1204, "ms_validity");
        KNOWN_TLVS.put((short) 0x130C, "alert_on_message_delivery");
        KNOWN_TLVS.put((short) 0x1380, "its_reply_type");
        KNOWN_TLVS.put((short) 0x1383, "its_session_info");
    }

    private final short tag;
    private final short len;
    private final byte[] value;

    public Tlv(short tag, short len, byte[] value) {
        this.tag = tag;
        this.len = len;
        this.value = value;
    }

    public short getTag() {
        return tag;
    }

    public short getLen() {
        return len;
    }

    public byte[] getValue() {
        return value;
    }

    public String getName() {
        return KNOWN_TLVS.get(tag);
    }
}
