package com.ukarim.smppgui.protocol.pdu;

import com.ukarim.smppgui.protocol.SmppCmd;
import com.ukarim.smppgui.protocol.SmppConstants;
import com.ukarim.smppgui.protocol.SmppStatus;
import com.ukarim.smppgui.util.StringUtils;
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

    public String getSystemId() {
        return systemId;
    }

    public String getSystemType() {
        return systemType;
    }

    public void setSystemType(String systemType) {
        this.systemType = systemType;
    }

    public byte getTon() {
        return ton;
    }

    public void setTon(byte ton) {
        this.ton = ton;
    }

    public byte getNpi() {
        return npi;
    }

    public void setNpi(byte npi) {
        this.npi = npi;
    }

    public String getAddrRange() {
        return addrRange;
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

        StringUtils.putCStr(buffer, systemId);
        StringUtils.putCStr(buffer, password);
        StringUtils.putCStr(buffer, systemType);

        buffer.put(SmppConstants.SMPP_34_INTERFACE_VER)
                .put(ton)
                .put(npi);

        StringUtils.putCStr(buffer, addrRange);
        return buffer;
    }

    private int calcLen() {
        int len = 16 + 3; // header + 3 fields by 1 byte
        return len
                + StringUtils.cStrLen(systemId)
                + StringUtils.cStrLen(password)
                + StringUtils.cStrLen(systemType)
                + StringUtils.cStrLen(addrRange);
    }
}
