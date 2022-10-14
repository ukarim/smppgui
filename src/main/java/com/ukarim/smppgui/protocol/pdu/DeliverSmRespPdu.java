package com.ukarim.smppgui.protocol.pdu;

import com.ukarim.smppgui.protocol.SmppCmd;
import com.ukarim.smppgui.protocol.SmppStatus;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class DeliverSmRespPdu implements Pdu {

    private final SmppStatus sts;

    private final int seqNum;

    public DeliverSmRespPdu(SmppStatus sts, int seqNum) {
        this.sts = sts;
        this.seqNum = seqNum;
    }

    @Override
    public SmppCmd getCmd() {
        return SmppCmd.DELIVER_SM_RESP;
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
        var buffer = ByteBuffer.allocate(17);
        buffer.order(ByteOrder.BIG_ENDIAN); // according to SMPP spec
        buffer.putInt(17)
                .putInt(SmppCmd.DELIVER_SM_RESP.getCmdId())
                .putInt(sts.getStatusId())
                .putInt(seqNum)
                .put((byte) 0x00); // This field is unused and is set to NULL
        return buffer;
    }
}
