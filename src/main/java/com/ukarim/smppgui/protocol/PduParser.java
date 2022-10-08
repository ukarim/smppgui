package com.ukarim.smppgui.protocol;

import com.ukarim.smppgui.protocol.pdu.HeaderPdu;
import com.ukarim.smppgui.protocol.pdu.Pdu;
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
        if (!buffer.hasRemaining()) {
            return null;
        }
        buffer.mark();
        int cmdLen = buffer.getInt();
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
            default:
                throw new SmppException("Unsupported smpp command: %s", cmd);
        }
    }
}
