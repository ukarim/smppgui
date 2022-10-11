package com.ukarim.smppgui.protocol.pdu;

import com.ukarim.smppgui.protocol.SmppCmd;
import com.ukarim.smppgui.protocol.SmppConstants;
import com.ukarim.smppgui.protocol.SmppStatus;
import com.ukarim.smppgui.util.ByteUtils;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class BindPdu implements Pdu {

    private final SmppCmd cmd;
    private final SmppStatus sts = SmppStatus.ESME_ROK; // always OK for this type of PDU
    private final int seqNum;
    private final String systemId;
    private final String password;
    private String systemType;
    private byte ton = 0;
    private byte npi = 0;
    private String addrRange = null;

    public BindPdu(SmppCmd cmd, int seqNum, String systemId, String password) {
        this.cmd = cmd;
        this.seqNum = seqNum;
        this.systemId = systemId;
        this.password = password;
    }

    public void setSystemType(String systemType) {
        this.systemType = systemType;
    }

    public void setTon(byte ton) {
        this.ton = ton;
    }

    public void setNpi(byte npi) {
        this.npi = npi;
    }

    public void setAddrRange(String addrRange) {
        this.addrRange = addrRange;
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
        int cmdLen = calcLen();
        var buffer = ByteBuffer.allocate(cmdLen);
        buffer.order(ByteOrder.BIG_ENDIAN); // according to SMPP spec

        buffer.putInt(cmdLen)
                .putInt(cmd.getCmdId())
                .putInt(sts.getStatusId())
                .putInt(seqNum);

        ByteUtils.putCStr(buffer, systemId);
        ByteUtils.putCStr(buffer, password);
        ByteUtils.putCStr(buffer, systemType);

        buffer.put(SmppConstants.SMPP_34_INTERFACE_VER)
                .put(ton)
                .put(npi);

        ByteUtils.putCStr(buffer, addrRange);
        return buffer;
    }

    private int calcLen() {
        int len = 16 + 3; // header + 3 fields by 1 byte
        return len
                + ByteUtils.cStrLen(systemId)
                + ByteUtils.cStrLen(password)
                + ByteUtils.cStrLen(systemType)
                + ByteUtils.cStrLen(addrRange);
    }
}
