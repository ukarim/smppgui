package com.ukarim.smppgui.protocol.pdu;

import com.ukarim.smppgui.protocol.SmppCmd;
import com.ukarim.smppgui.protocol.SmppStatus;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class HeaderPdu implements ReqPdu, RespPdu {

    private final SmppCmd cmd;
    private final SmppStatus sts;
    private int seqNum;

    public HeaderPdu(SmppCmd cmd) {
        this(cmd, SmppStatus.ESME_ROK, 0);
    }

    public HeaderPdu(SmppCmd cmd, SmppStatus sts, int seqNum) {
        this.cmd = cmd;
        this.sts = sts;
        this.seqNum = seqNum;
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
    public void setSeqNum(int seqNum) {
        this.seqNum = seqNum;
    }

    @Override
    public ByteBuffer toByteBuffer() {
        var buf = ByteBuffer.allocate(16);
        buf.order(ByteOrder.BIG_ENDIAN); // according to SMPP spec
        return buf.putInt(16)
                .putInt(cmd.getCmdId())
                .putInt(sts.getStatusId())
                .putInt(seqNum);
    }
}
