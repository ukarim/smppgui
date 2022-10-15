package com.ukarim.smppgui.protocol.pdu;

import com.ukarim.smppgui.protocol.SmppCmd;
import com.ukarim.smppgui.protocol.SmppStatus;
import com.ukarim.smppgui.util.ByteUtils;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;

public class SubmitSmPdu implements Pdu {

    private final SmppStatus sts = SmppStatus.ESME_ROK;

    private final int seqNum;

    private final String serviceType = "smppgui";

    private final Address srcAddress;

    private final Address destAddress;

    private byte protocolId;

    private byte priorityFlag;

    private String scheduleDeliveryTime;

    private String validityPeriod;

    private byte registeredDelivery;

    private final String shortMessage;

    public SubmitSmPdu(int seqNum, Address srcAddress, Address destAddress, String shortMessage) {
        this.seqNum = seqNum;
        this.srcAddress = srcAddress;
        this.destAddress = destAddress;
        this.shortMessage = shortMessage;
    }

    public void setProtocolId(byte protocolId) {
        this.protocolId = protocolId;
    }

    public void setPriorityFlag(byte priorityFlag) {
        this.priorityFlag = priorityFlag;
    }

    public void setScheduleDeliveryTime(String scheduleDeliveryTime) {
        this.scheduleDeliveryTime = scheduleDeliveryTime;
    }

    public void setValidityPeriod(String validityPeriod) {
        this.validityPeriod = validityPeriod;
    }

    public void setRegisteredDelivery(byte registeredDelivery) {
        this.registeredDelivery = registeredDelivery;
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

        ByteUtils.putCStr(buffer, serviceType);

        buffer.put(srcAddress.getTon())
                .put(srcAddress.getNpi());
        ByteUtils.putCStr(buffer, srcAddress.getAddr());

        buffer.put(destAddress.getTon())
                .put(destAddress.getNpi());
        ByteUtils.putCStr(buffer, destAddress.getAddr());

        buffer.put((byte) 0x00) // esm_class
                .put(protocolId)
                .put(priorityFlag);

        ByteUtils.putCStr(buffer, scheduleDeliveryTime);
        ByteUtils.putCStr(buffer, validityPeriod);

        buffer.put(registeredDelivery);
        buffer.put((byte) 0x00); // replace_if_present_flag
        buffer.put((byte) 0x08); // data_coding (UCS2)
        buffer.put((byte) 0x00); // sm_default_msg_id

        buffer.put((byte) smLen);
        buffer.put(shortMessageBytes, 0, smLen);

        return buffer;
    }

    private int calcLen(int smLen) {
        int len = 28; // sum of lens of fixed-sized fields
        len += ByteUtils.cStrLen(serviceType);
        len += ByteUtils.cStrLen(srcAddress.getAddr());
        len += ByteUtils.cStrLen(destAddress.getAddr());
        len += ByteUtils.cStrLen(scheduleDeliveryTime);
        len += ByteUtils.cStrLen(validityPeriod);
        len += smLen;
        return len;
    }
}
