package com.ukarim.smppgui.protocol.pdu;

import com.ukarim.smppgui.protocol.SmppCmd;
import com.ukarim.smppgui.protocol.SmppConstants;
import com.ukarim.smppgui.protocol.SmppStatus;
import com.ukarim.smppgui.util.StringUtils;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;

public class SubmitSmPdu implements Pdu {

    private final SmppStatus sts = SmppStatus.ESME_ROK;

    private final int seqNum;

    private final Address srcAddress;

    private final Address destAddress;

    private String serviceType;

    private final byte esmClass = (byte) 0x00;

    private byte protocolId;

    private byte priorityFlag;

    private String scheduleDeliveryTime;

    private String validityPeriod;

    private byte registeredDelivery;

    private final byte dataCoding = SmppConstants.DATA_CODING_UCS2;

    private final String shortMessage;

    public SubmitSmPdu(int seqNum, Address srcAddress, Address destAddress, String shortMessage) {
        this.seqNum = seqNum;
        this.srcAddress = srcAddress;
        this.destAddress = destAddress;
        this.shortMessage = shortMessage;
    }

    public Address getSrcAddress() {
        return srcAddress;
    }

    public Address getDestAddress() {
        return destAddress;
    }

    public String getShortMessage() {
        return shortMessage;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public byte getEsmClass() {
        return esmClass;
    }

    public byte getProtocolId() {
        return protocolId;
    }

    public void setProtocolId(byte protocolId) {
        this.protocolId = protocolId;
    }

    public byte getPriorityFlag() {
        return priorityFlag;
    }

    public void setPriorityFlag(byte priorityFlag) {
        this.priorityFlag = priorityFlag;
    }

    public String getScheduleDeliveryTime() {
        return scheduleDeliveryTime;
    }

    public void setScheduleDeliveryTime(String scheduleDeliveryTime) {
        this.scheduleDeliveryTime = scheduleDeliveryTime;
    }

    public String getValidityPeriod() {
        return validityPeriod;
    }

    public void setValidityPeriod(String validityPeriod) {
        this.validityPeriod = validityPeriod;
    }

    public byte getRegisteredDelivery() {
        return registeredDelivery;
    }

    public void setRegisteredDelivery(byte registeredDelivery) {
        this.registeredDelivery = registeredDelivery;
    }

    public byte getDataCoding() {
        return dataCoding;
    }

    @Override
    public SmppCmd getCmd() {
        return SmppCmd.SUBMIT_SM;
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
        byte[] shortMessageBytes = shortMessage.getBytes(StandardCharsets.UTF_16BE);
        int smLen = shortMessageBytes.length;

        int cmdLen = calcLen(smLen);
        var buffer = ByteBuffer.allocate(cmdLen);
        buffer.order(ByteOrder.BIG_ENDIAN); // according to SMPP spec

        buffer.putInt(cmdLen)
                .putInt(SmppCmd.SUBMIT_SM.getCmdId())
                .putInt(sts.getStatusId())
                .putInt(seqNum);

        StringUtils.putCStr(buffer, serviceType);

        buffer.put(srcAddress.getTon())
                .put(srcAddress.getNpi());
        StringUtils.putCStr(buffer, srcAddress.getAddr());

        buffer.put(destAddress.getTon())
                .put(destAddress.getNpi());
        StringUtils.putCStr(buffer, destAddress.getAddr());

        buffer.put(esmClass)
                .put(protocolId)
                .put(priorityFlag);

        StringUtils.putCStr(buffer, scheduleDeliveryTime);
        StringUtils.putCStr(buffer, validityPeriod);

        buffer.put(registeredDelivery);
        buffer.put((byte) 0x00); // replace_if_present_flag
        buffer.put(dataCoding);
        buffer.put((byte) 0x00); // sm_default_msg_id

        buffer.put((byte) smLen);
        buffer.put(shortMessageBytes, 0, smLen);

        return buffer;
    }

    private int calcLen(int smLen) {
        int len = 28; // sum of lens of fixed-sized fields
        len += StringUtils.cStrLen(serviceType);
        len += StringUtils.cStrLen(srcAddress.getAddr());
        len += StringUtils.cStrLen(destAddress.getAddr());
        len += StringUtils.cStrLen(scheduleDeliveryTime);
        len += StringUtils.cStrLen(validityPeriod);
        len += smLen;
        return len;
    }
}
