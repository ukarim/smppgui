package com.ukarim.smppgui.protocol;

import com.ukarim.smppgui.protocol.pdu.Address;
import com.ukarim.smppgui.protocol.pdu.BindRespPdu;
import com.ukarim.smppgui.protocol.pdu.DeliverSmPdu;
import com.ukarim.smppgui.protocol.pdu.HeaderPdu;
import com.ukarim.smppgui.protocol.pdu.Pdu;
import com.ukarim.smppgui.protocol.pdu.SubmitSmRespPdu;
import com.ukarim.smppgui.util.StringUtils;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class PduParser {

    private PduParser() {}

    public static List<Pdu> parsePdu(ByteBuffer buffer) throws SmppException {
        buffer.flip();
        var pduList = new ArrayList<Pdu>();
        Pdu pdu;
        while ((pdu = parseSinglePdu(buffer)) != null) {
            pduList.add(pdu);
        }
        return pduList;
    }

    private static Pdu parseSinglePdu(ByteBuffer buffer) throws SmppException {
        if (!hasBytes(buffer, 4)) { // check if contains at least cmd_len (4bytes)
            return null;
        }
        buffer.mark();
        int cmdLen = buffer.getInt();
        int maxPos = buffer.position() + cmdLen - 4; // minus 4 (for cmd_len itself)
        if (cmdLen > SmppConstants.MAX_PDU_LEN) {
            buffer.reset();
            throw new SmppException("Too large PDU received. cmd_len is %s, but max allowed is %s",
                    cmdLen, SmppConstants.MAX_PDU_LEN);
        }
        if (buffer.remaining() < (cmdLen - 4)) { // check if contains full pdu
            buffer.reset();
            return null;
        }
        var cmd = SmppCmd.fromCmdId(buffer.getInt());
        var sts = SmppStatus.fromStatusId(buffer.getInt());
        int seqNum = buffer.getInt();
        switch (cmd) {
            case UNBIND:
            case UNBIND_RESP:
            case GENERIC_NACK:
            case CANCEL_SM_RESP:
            case REPLACE_SM_RESP:
            case ENQUIRE_LINK:
            case ENQUIRE_LINK_RESP:
                return new HeaderPdu(cmd, sts, seqNum);
            case BIND_RECEIVER_RESP:
            case BIND_TRANSCEIVER_RESP:
            case BIND_TRANSMITTER_RESP: {
                var bindRespPdu = new BindRespPdu(cmd, sts, seqNum);
                String systemId = StringUtils.readCStr(buffer, cmdLen - 16);
                bindRespPdu.setSystemId(systemId);
                List<Tlv> tlvs = parseTlvs(buffer);
                bindRespPdu.setTlvs(tlvs);
                consumeRemainBytes(buffer, maxPos); // Consume remaining. Actually must be dead code
                return bindRespPdu;
            }
            case SUBMIT_SM_RESP: {
                var submitSmResp = new SubmitSmRespPdu(sts, seqNum);
                String messageId = StringUtils.readCStr(buffer, cmdLen - 16);
                submitSmResp.setMessageId(messageId);
                return submitSmResp;
            }
            case DELIVER_SM: {
                String serviceType = StringUtils.readCStr(buffer, cmdLen - 16);
                byte srcAddrTon = buffer.get();
                byte srcAddrNpi = buffer.get();
                String srcAddr = readCStr(buffer, maxPos);
                byte destTon = buffer.get();
                byte destNpi = buffer.get();
                String destAddr = readCStr(buffer, maxPos);
                byte esmClass = buffer.get();
                byte protocolId = buffer.get();
                byte priorityFlag = buffer.get();
                String schedDeliveryTime = readCStr(buffer, maxPos);
                String validityPeriod = readCStr(buffer, maxPos);
                byte regDelivery = buffer.get();
                byte replaceIfPresent = buffer.get(); // do not remove
                byte dataCoding = buffer.get();
                byte smDefMsgId = buffer.get();  // do not remove
                byte smLen = buffer.get();

                byte[] shortMsg = new byte[smLen];
                copyToByteArr(buffer, shortMsg);

                List<Tlv> tlvs = parseTlvs(buffer);
                consumeRemainBytes(buffer, maxPos); // Consume remaining. Actually must be dead code
                var deliverSm = new DeliverSmPdu(
                        seqNum,
                        new Address(srcAddrTon, srcAddrNpi, srcAddr),
                        new Address(destTon, destNpi, destAddr),
                        shortMsg,
                        dataCoding
                );
                deliverSm.setServiceType(serviceType);
                deliverSm.setEsmClass(esmClass);
                deliverSm.setProtocolId(protocolId);
                deliverSm.setPriorityFlag(priorityFlag);
                deliverSm.setScheduleDeliveryTime(schedDeliveryTime);
                deliverSm.setValidityPeriod(validityPeriod);
                deliverSm.setRegisteredDelivery(regDelivery);
                deliverSm.setTlvs(tlvs);
                return deliverSm;
            }
            default:
                // pdus unsupported on client side
                throw new SmppException("Parsing for PDU '%s' not implemented", cmd);
        }
    }

    private static List<Tlv> parseTlvs(ByteBuffer buffer) {
        if (!buffer.hasRemaining()) {
            return Collections.emptyList();
        }
        var tlvs = new ArrayList<Tlv>();
        while (buffer.hasRemaining()) {
            short tag = buffer.getShort();
            short len = buffer.getShort();
            byte[] value = new byte[len];
            copyToByteArr(buffer, value);
            tlvs.add(new Tlv(tag, len, value));
        }
        return tlvs;
    }

    private static String readCStr(ByteBuffer buffer, int maxPos) {
        int remainedBytes = maxPos - buffer.position();
        return StringUtils.readCStr(buffer, remainedBytes);
    }

    private static void consumeRemainBytes(ByteBuffer buffer, int maxPos) {
        buffer.position(maxPos);
    }

    private static boolean hasBytes(Buffer buffer, int n) {
        return (buffer.limit() - buffer.position()) >= n;
    }

    private static void copyToByteArr(ByteBuffer buffer, byte[] arr) {
        for (int i = 0; i < arr.length; i++) {
            arr[i] = buffer.get();
        }
    }
}
