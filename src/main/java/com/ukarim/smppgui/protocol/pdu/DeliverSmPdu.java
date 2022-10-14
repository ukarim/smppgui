package com.ukarim.smppgui.protocol.pdu;

import com.ukarim.smppgui.protocol.SmppCmd;
import java.nio.ByteBuffer;

public class DeliverSmPdu extends SubmitSmPdu {

    public DeliverSmPdu(int seqNum, Address srcAddress, Address destAddress, String shortMessage) {
        super(seqNum, srcAddress, destAddress, shortMessage);
    }

    @Override
    public SmppCmd getCmd() {
        return SmppCmd.DELIVER_SM;
    }

    @Override
    public ByteBuffer toByteBuffer() {
        // don't need this on client side
        throw new UnsupportedOperationException("(DeliverSmPdu --> ByteBuffer) operation not supported");
    }
}