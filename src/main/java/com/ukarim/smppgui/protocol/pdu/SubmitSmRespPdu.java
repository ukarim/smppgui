package com.ukarim.smppgui.protocol.pdu;

import com.ukarim.smppgui.protocol.SmppCmd;
import com.ukarim.smppgui.protocol.SmppStatus;
import java.nio.ByteBuffer;

public class SubmitSmRespPdu implements RespPdu {

    private final SmppStatus sts;

    private final int seqNum;

    private String messageId;

    public SubmitSmRespPdu(SmppStatus sts, int seqNum) {
        this.sts = sts;
        this.seqNum = seqNum;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    @Override
    public SmppCmd getCmd() {
        return SmppCmd.SUBMIT_SM_RESP;
    }

    @Override
    public SmppStatus getSts() {
        return sts;
    }

    @Override
    public int getSeqNum() {
        return seqNum;
    }

    @Override
    public ByteBuffer toByteBuffer() {
        // don't need this on client side
        throw new UnsupportedOperationException("(SubmitSmRespPdu --> ByteBuffer) operation not supported");
    }
}
