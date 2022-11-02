package com.ukarim.smppgui.gui;

import com.ukarim.smppgui.protocol.pdu.Address;

public class SubmitModel {

    private final Address srcAddress;
    private final Address destAddress;
    private final String shortMessage;
    private String serviceType;
    private byte registeredDelivery;
    private byte protocolId;
    private byte priorityFlag;
    private String schedDeliverTime;
    private String validityPeriod;

    public SubmitModel(Address srcAddress, Address destAddress, String shortMessage) {
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

    public byte getRegisteredDelivery() {
        return registeredDelivery;
    }

    public void setRegisteredDelivery(byte registeredDelivery) {
        this.registeredDelivery = registeredDelivery;
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

    public String getSchedDeliverTime() {
        return schedDeliverTime;
    }

    public void setSchedDeliverTime(String schedDeliverTime) {
        this.schedDeliverTime = schedDeliverTime;
    }

    public String getValidityPeriod() {
        return validityPeriod;
    }

    public void setValidityPeriod(String validityPeriod) {
        this.validityPeriod = validityPeriod;
    }
}
