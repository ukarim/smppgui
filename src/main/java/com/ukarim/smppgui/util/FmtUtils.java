package com.ukarim.smppgui.util;

import com.ukarim.smppgui.protocol.SmppCmd;
import com.ukarim.smppgui.protocol.SmppConstants;
import com.ukarim.smppgui.protocol.SmppStatus;
import com.ukarim.smppgui.protocol.pdu.BindPdu;
import com.ukarim.smppgui.protocol.pdu.BindRespPdu;
import com.ukarim.smppgui.protocol.pdu.DeliverSmRespPdu;
import com.ukarim.smppgui.protocol.pdu.Pdu;
import com.ukarim.smppgui.protocol.pdu.SubmitSmPdu;
import com.ukarim.smppgui.protocol.pdu.SubmitSmRespPdu;
import java.nio.charset.StandardCharsets;

public final class FmtUtils {

    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();

    private FmtUtils() {}

    public static String toHexString(byte... bytes) {
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

    public static String fmtByte(byte n) {
        return "0x" + toHexString(n);
    }

    public static String fmtPdu(Pdu pdu) {
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
                shortMsgStr = "hex(" + toHexString(shortMsg) + ")";
            } else {
                var charset = SmppConstants.DATA_CODING_UCS2 == submitSm.getDataCoding()
                        ? StandardCharsets.UTF_16BE
                        : StandardCharsets.US_ASCII;
                shortMsgStr = new String(shortMsg, charset);
            }
            builder.append("[short_message]: ").append(shortMsgStr);
        }

        return builder.toString();
    }


}
