package com.ukarim.smppgui.protocol.pdu;

import com.ukarim.smppgui.protocol.SmppCmd;
import com.ukarim.smppgui.protocol.SmppStatus;
import com.ukarim.smppgui.protocol.Tlv;
import com.ukarim.smppgui.util.StringUtils;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Collections;
import java.util.List;

public class SubmitSmPdu implements ReqPdu {

    private final SmppStatus sts = SmppStatus.ESME_ROK;

    private int seqNum;

    private final Address srcAddress;

    private final Address destAddress;

    private String serviceType;

    private byte esmClass;

    private byte protocolId;

    private byte priorityFlag;

    private String scheduleDeliveryTime;

    private String validityPeriod;

    private byte registeredDelivery;

    private final byte dataCoding;

    private final byte[] shortMessage;

    private List<Tlv> tlvs = Collections.emptyList();

    public SubmitSmPdu(Address srcAddress, Address destAddress, byte[] shortMessage, byte dataCoding) {
        this.srcAddress = srcAddress;
        this.destAddress = destAddress;
        this.shortMessage = shortMessage;
        this.dataCoding = dataCoding;
    }

    public Address getSrcAddress() {
        return srcAddress;
    }

    public Address getDestAddress() {
        return destAddress;
    }

    public byte[] getShortMessage() {
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

    public void setEsmClass(byte esmClass) {
        this.esmClass = esmClass;
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

    public List<Tlv> getTlvs() {
        return tlvs;
    }

    public void setTlvs(List<Tlv> tlvs) {
        this.tlvs = tlvs;
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
    public void setSeqNum(int seqNum) {
        this.seqNum = seqNum;
    }

    @Override
    public ByteBuffer toByteBuffer() {
        int cmdLen = calcLen();
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

        int smLen = shortMessage.length;
        buffer.put((byte) smLen);
        buffer.put(shortMessage, 0, smLen);

        for (var tlv : tlvs) {
            short tlvLen = tlv.getLen();
            buffer.putShort(tlv.getTag())
                    .putShort(tlvLen)
                    .put(tlv.getValue(), 0, tlvLen);
        }

        return buffer;
    }

    private int calcLen() {
        int len = 28; // sum of lens of fixed-sized fields
        len += StringUtils.cStrLen(serviceType);
        len += StringUtils.cStrLen(srcAddress.getAddr());
        len += StringUtils.cStrLen(destAddress.getAddr());
        len += StringUtils.cStrLen(scheduleDeliveryTime);
        len += StringUtils.cStrLen(validityPeriod);
        len += shortMessage.length;
        for (var tlv : tlvs) {
            len += (tlv.getLen() + 4); // +2 for tag and +2 for len fields
        }
        return len;
    }
}
