package com.ukarim.smppgui.protocol.pdu;

import com.ukarim.smppgui.protocol.SmppCmd;
import com.ukarim.smppgui.protocol.SmppStatus;
import java.nio.ByteBuffer;

public class BindRespPdu implements Pdu {

    private final SmppCmd cmd;
    private final SmppStatus sts;
    private final int seqNum;
    private String systemId;

    public BindRespPdu(SmppCmd cmd, SmppStatus sts, int seqNum) {
        this.cmd = cmd;
        this.sts = sts;
        this.seqNum = seqNum;
    }



    public void setSystemId(String systemId) {
        this.systemId = systemId;
    }

    @Override
    public SmppCmd getCmd() {
        return cmd;
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
        throw new UnsupportedOperationException("(BindRespPdu --> ByteBuffer) operation not supported");
    }
}
