package com.ukarim.smppgui.protocol.pdu;

import com.ukarim.smppgui.protocol.SmppCmd;
import com.ukarim.smppgui.protocol.SmppStatus;
import com.ukarim.smppgui.protocol.Tlv;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.List;

public class BindRespPdu implements Pdu {

    private final SmppCmd cmd;
    private final SmppStatus sts;
    private final int seqNum;
    private String systemId;
    private List<Tlv> tlvs = Collections.emptyList();

    public BindRespPdu(SmppCmd cmd, SmppStatus sts, int seqNum) {
        this.cmd = cmd;
        this.sts = sts;
        this.seqNum = seqNum;
    }

    public String getSystemId() {
        return systemId;
    }

    public void setSystemId(String systemId) {
        this.systemId = systemId;
    }

    public List<Tlv> getTlvs() {
        return tlvs;
    }

    public void setTlvs(List<Tlv> tlvs) {
        this.tlvs = tlvs;
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
