package com.ukarim.smppgui.util;

import static com.ukarim.smppgui.protocol.SmppConstants.DATA_CODING_DEFAULT;
import static com.ukarim.smppgui.protocol.SmppConstants.DATA_CODING_IA5;
import static com.ukarim.smppgui.protocol.SmppConstants.DATA_CODING_LATIN1;
import static com.ukarim.smppgui.protocol.SmppConstants.DATA_CODING_UCS2;

import com.ukarim.smppgui.protocol.SmppCmd;
import com.ukarim.smppgui.protocol.SmppConstants;
import com.ukarim.smppgui.protocol.SmppStatus;
import com.ukarim.smppgui.protocol.Tlv;
import com.ukarim.smppgui.protocol.pdu.BindPdu;
import com.ukarim.smppgui.protocol.pdu.BindRespPdu;
import com.ukarim.smppgui.protocol.pdu.DeliverSmRespPdu;
import com.ukarim.smppgui.protocol.pdu.Pdu;
import com.ukarim.smppgui.protocol.pdu.SubmitSmPdu;
import com.ukarim.smppgui.protocol.pdu.SubmitSmRespPdu;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public final class FmtUtils {

    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();

    private static final Map<Short, Tuple2<String, Function<byte[], String>>> KNOWN_TLVS;

    static {
        KNOWN_TLVS = new HashMap<>();
        KNOWN_TLVS.put((short) 0x0005, Tuple2.of("dest_addr_subunit", FmtUtils::fmtByteArr));
        KNOWN_TLVS.put((short) 0x0006, Tuple2.of("dest_network_type", FmtUtils::fmtByteArr));
        KNOWN_TLVS.put((short) 0x0007, Tuple2.of("dest_bearer_type", FmtUtils::fmtByteArr));
        KNOWN_TLVS.put((short) 0x0008, Tuple2.of("dest_telematics_id", FmtUtils::fmtByteArr));
        KNOWN_TLVS.put((short) 0x000D, Tuple2.of("source_addr_subunit", FmtUtils::fmtByteArr));
        KNOWN_TLVS.put((short) 0x000E, Tuple2.of("source_network_type", FmtUtils::fmtByteArr));
        KNOWN_TLVS.put((short) 0x000F, Tuple2.of("source_bearer_type", FmtUtils::fmtByteArr));
        KNOWN_TLVS.put((short) 0x0010, Tuple2.of("source_telematics_id", FmtUtils::fmtByteArr));
        KNOWN_TLVS.put((short) 0x0017, Tuple2.of("qos_time_to_live", FmtUtils::fmtByteArr));
        KNOWN_TLVS.put((short) 0x0019, Tuple2.of("payload_type", FmtUtils::fmtByteArr));
        KNOWN_TLVS.put((short) 0x001D, Tuple2.of("additional_status_info_text", FmtUtils::fmtByteArr));
        KNOWN_TLVS.put((short) 0x001E, Tuple2.of("receipted_message_id", FmtUtils::fmtCStr));
        KNOWN_TLVS.put((short) 0x0030, Tuple2.of("ms_msg_wait_facilities", FmtUtils::fmtByteArr));
        KNOWN_TLVS.put((short) 0x0201, Tuple2.of("privacy_indicator", FmtUtils::fmtByteArr));
        KNOWN_TLVS.put((short) 0x0202, Tuple2.of("source_subaddress", FmtUtils::fmtByteArr));
        KNOWN_TLVS.put((short) 0x0203, Tuple2.of("dest_subaddress", FmtUtils::fmtByteArr));
        KNOWN_TLVS.put((short) 0x0204, Tuple2.of("user_message_reference", FmtUtils::fmtByteArr));
        KNOWN_TLVS.put((short) 0x0205, Tuple2.of("user_response_code", FmtUtils::fmtByteArr));
        KNOWN_TLVS.put((short) 0x020A, Tuple2.of("source_port", FmtUtils::fmtByteArr));
        KNOWN_TLVS.put((short) 0x020B, Tuple2.of("destination_port", FmtUtils::fmtByteArr));
        KNOWN_TLVS.put((short) 0x020C, Tuple2.of("sar_msg_ref_num", FmtUtils::fmtByteArr));
        KNOWN_TLVS.put((short) 0x020D, Tuple2.of("language_indicator", FmtUtils::fmtByteArr));
        KNOWN_TLVS.put((short) 0x020E, Tuple2.of("sar_total_segments", FmtUtils::fmtByteArr));
        KNOWN_TLVS.put((short) 0x020F, Tuple2.of("sar_segment_seqnum", FmtUtils::fmtByteArr));
        KNOWN_TLVS.put((short) 0x0210, Tuple2.of("SC_interface_version", FmtUtils::fmtByteArr));
        KNOWN_TLVS.put((short) 0x0302, Tuple2.of("callback_num_pres_ind", FmtUtils::fmtByteArr));
        KNOWN_TLVS.put((short) 0x0303, Tuple2.of("callback_num_atag", FmtUtils::fmtByteArr));
        KNOWN_TLVS.put((short) 0x0304, Tuple2.of("number_of_messages", FmtUtils::fmtByteArr));
        KNOWN_TLVS.put((short) 0x0381, Tuple2.of("callback_num", FmtUtils::fmtByteArr));
        KNOWN_TLVS.put((short) 0x0420, Tuple2.of("dpf_result", FmtUtils::fmtByteArr));
        KNOWN_TLVS.put((short) 0x0421, Tuple2.of("set_dpf", FmtUtils::fmtByteArr));
        KNOWN_TLVS.put((short) 0x0422, Tuple2.of("ms_availability_status", FmtUtils::fmtByteArr));
        KNOWN_TLVS.put((short) 0x0423, Tuple2.of("network_error_code", FmtUtils::fmtByteArr));
        KNOWN_TLVS.put((short) 0x0424, Tuple2.of("message_payload", FmtUtils::fmtByteArr));
        KNOWN_TLVS.put((short) 0x0425, Tuple2.of("delivery_failure_reason", FmtUtils::fmtByteArr));
        KNOWN_TLVS.put((short) 0x0426, Tuple2.of("more_messages_to_send", FmtUtils::fmtByteArr));
        KNOWN_TLVS.put((short) 0x0427, Tuple2.of("message_state", FmtUtils::fmtTlvMessageState));
        KNOWN_TLVS.put((short) 0x0501, Tuple2.of("ussd_service_op", FmtUtils::fmtByteArr));
        KNOWN_TLVS.put((short) 0x1201, Tuple2.of("display_time", FmtUtils::fmtByteArr));
        KNOWN_TLVS.put((short) 0x1203, Tuple2.of("sms_signal", FmtUtils::fmtByteArr));
        KNOWN_TLVS.put((short) 0x1204, Tuple2.of("ms_validity", FmtUtils::fmtByteArr));
        KNOWN_TLVS.put((short) 0x130C, Tuple2.of("alert_on_message_delivery", FmtUtils::fmtByteArr));
        KNOWN_TLVS.put((short) 0x1380, Tuple2.of("its_reply_type", FmtUtils::fmtByteArr));
        KNOWN_TLVS.put((short) 0x1383, Tuple2.of("its_session_info", FmtUtils::fmtByteArr));
    }

    private FmtUtils() {}

    private static String toHexString(byte... bytes) {
        if (bytes == null) {
            return "null";
        }
        char[] hexChars = new char[bytes.length * 2];
        for (int i = 0; i < bytes.length; i++) {
            int v = bytes[i] & 0xFF;
            hexChars[i * 2] = HEX_ARRAY[v >>> 4];
            hexChars[i * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static String fmtInt(int n) {
        byte b4 = (byte) (n >> 24);
        byte b3 = (byte) (n >> 16);
        byte b2 = (byte) (n >> 8);
        byte b1 = (byte) (n >> 0);
        return "0x" + toHexString(b4, b3, b2, b1);
    }

    public static String fmtShort(short n) {
        byte b2 = (byte) (n >> 8);
        byte b1 = (byte) (n >> 0);
        return "0x" + toHexString(b2, b1);
    }

    public static String fmtByte(byte n) {
        return "0x" + toHexString(n);
    }

    public static String fmtPdu(Pdu pdu, Charset defaultCharset) {
        SmppCmd cmd = pdu.getCmd();
        SmppStatus sts = pdu.getSts();
        int seqNum = pdu.getSeqNum();

        // header
        var builder = new StringBuilder();
        String fmtCmd = fmtInt(cmd.getCmdId()) + " (" + cmd + ")";
        builder.append("[command_id]: ").append(fmtCmd).append("\n");
        String fmtSts = fmtInt(sts.getStatusId()) + " (" + sts.getStatusDesc() + ")";
        builder.append("[command_status]: ").append(fmtSts).append("\n");
        builder.append("[sequence_number]: ").append(seqNum).append("\n");

        if (pdu instanceof BindPdu) {
            var bindPdu = (BindPdu) pdu;
            builder.append("[system_id]: ").append(bindPdu.getSystemId()).append("\n");
            builder.append("[system_type]: ").append(bindPdu.getSystemType()).append("\n");
            builder.append("[interface_version]: 0x34\n");
            var fmtTon = fmtByte(bindPdu.getTon());
            builder.append("[addr_ton]: ").append(fmtTon).append("\n");
            var fmtNpi = fmtByte(bindPdu.getNpi());
            builder.append("[addr_npi]: ").append(fmtNpi).append("\n");
            builder.append("[addr_range]: ").append(bindPdu.getAddrRange());
        }

        if (pdu instanceof BindRespPdu) {
            var bindRespPdu = (BindRespPdu) pdu;
            builder.append("[system_id]: ").append(bindRespPdu.getSystemId());
            fmtTlvs(bindRespPdu.getTlvs(), builder);
        }

        if (pdu instanceof SubmitSmRespPdu) {
            var submitSmResp = (SubmitSmRespPdu) pdu;
            builder.append("[message_id]: ").append(submitSmResp.getMessageId());
        }

        if (pdu instanceof DeliverSmRespPdu) {
            builder.append("[message_id]: null"); // always null according to smpp spec
        }

        if (pdu instanceof SubmitSmPdu) { // this for DeliverSmPdu too
            var submitSm = (SubmitSmPdu) pdu;
            builder.append("[service_type]: ").append(submitSm.getServiceType()).append("\n");

            var srcAddr = submitSm.getSrcAddress();
            builder.append("[source_addr_ton]: ").append(fmtByte(srcAddr.getTon())).append("\n");
            builder.append("[source_addr_npi]: ").append(fmtByte(srcAddr.getNpi())).append("\n");
            builder.append("[source_addr]: ").append(srcAddr.getAddr()).append("\n");

            var destAddr = submitSm.getDestAddress();
            builder.append("[dest_addr_ton]: ").append(fmtByte(destAddr.getTon())).append("\n");
            builder.append("[dest_addr_npi]: ").append(fmtByte(destAddr.getNpi())).append("\n");
            builder.append("[destination_addr]: ").append(destAddr.getAddr()).append("\n");

            builder.append("[esm_class]: ").append(fmtByte(submitSm.getEsmClass())).append("\n");
            builder.append("[protocol_id]: ").append(fmtByte(submitSm.getProtocolId())).append("\n");
            builder.append("[priority_flag]: ").append(fmtByte(submitSm.getPriorityFlag())).append("\n");
            builder.append("[schedule_delivery_time]: ").append(submitSm.getScheduleDeliveryTime()).append("\n");
            builder.append("[validity_period]: ").append(submitSm.getValidityPeriod()).append("\n");
            builder.append("[registered_delivery]: ").append(fmtByte(submitSm.getRegisteredDelivery())).append("\n");
            builder.append("[data_coding]: ").append(fmtByte(submitSm.getDataCoding())).append("\n");
            byte[] shortMsg = submitSm.getShortMessage();
            boolean containsUdh = (submitSm.getEsmClass() & SmppConstants.ESM_UDH_MASK) != 0;
            String shortMsgStr;
            if (containsUdh) {
                shortMsgStr = fmtByteArr(shortMsg);
            } else {
                shortMsgStr = decodeStr(shortMsg, submitSm.getDataCoding(), defaultCharset);
            }
            builder.append("[short_message]: ").append(shortMsgStr);
            fmtTlvs(submitSm.getTlvs(), builder);
        }

        return builder.toString();
    }

    private static void fmtTlvs(List<Tlv> tlvList, StringBuilder builder) {
        tlvList.forEach(tlv -> {
            short tag = tlv.getTag();
            byte[] value = tlv.getValue();
            var tlvConf = KNOWN_TLVS.get(tag);
            if (tlvConf == null) {
                builder.append("\n[tlv(").append(fmtShort(tag)).append(")]: ")
                        .append(fmtByteArr(value));
            } else {
                String tagName = tlvConf.getFirst();
                var valueFormatter = tlvConf.getSecond();
                builder.append("\n[").append(tagName).append("]: ")
                        .append(valueFormatter.apply(value));
            }
        });
    }

    private static String decodeStr(byte[] bytes, byte dataCoding, Charset defaultCharset) {
        Charset charset;
        switch (dataCoding) {
            case DATA_CODING_UCS2:
                charset = StandardCharsets.UTF_16BE;
                break;
            case DATA_CODING_LATIN1:
                charset = StandardCharsets.ISO_8859_1;
                break;
            case DATA_CODING_IA5:
                charset = StandardCharsets.US_ASCII;
                break;
            case DATA_CODING_DEFAULT:
                charset = defaultCharset;
                break;
            default:
                // print hex bytes for unsupported charsets
                return fmtByteArr(bytes);
        }
        String output;
        try {
            output = new String(bytes, charset);
        } catch (Exception e) {
            output = fmtByteArr(bytes);
        }
        return output;
    }

    private static String fmtByteArr(byte[] b) {
        return "hex(" + toHexString(b) + ")";
    }

    private static String fmtCStr(byte[] b) {
        return new String(b, StandardCharsets.US_ASCII);
    }


    // ------------------------- TLV specific functions ------------------------- //

    private static String fmtTlvMessageState(byte[] b) {
        byte msgState = b[0];
        String desc;
        switch (msgState) {
            case 1:
                desc = "ENROUTE";
                break;
            case 2:
                desc = "DELIVERED";
                break;
            case 3:
                desc = "EXPIRED";
                break;
            case 4:
                desc = "DELETED";
                break;
            case 5:
                desc = "UNDELIVERABLE";
                break;
            case 6:
                desc = "ACCEPTED";
                break;
            case 7:
                desc = "UNKNOWN";
                break;
            case 8:
                desc = "REJECTED";
                break;
            default:
                desc = "";
        }
        return "" + msgState + " (" + desc + ")";
    }
}
