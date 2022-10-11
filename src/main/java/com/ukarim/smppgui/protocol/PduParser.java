package com.ukarim.smppgui.protocol;

import com.ukarim.smppgui.protocol.pdu.BindRespPdu;
import com.ukarim.smppgui.protocol.pdu.HeaderPdu;
import com.ukarim.smppgui.protocol.pdu.Pdu;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.ArrayList;
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
                consumeBytes(buffer, cmdLen - 16); // TODO consume systemId (and possible TLVs)
                return bindRespPdu;
            }
            default:
                // pdus unsupported on client side
                throw new SmppException("Unsupported smpp command: %s", cmd);
        }
    }

    private static void consumeBytes(ByteBuffer buffer, int n) {
        buffer.position(buffer.position() + n);
    }

    private static boolean hasBytes(Buffer buffer, int n) {
        return (buffer.limit() - buffer.position()) >= n;
    }
}
