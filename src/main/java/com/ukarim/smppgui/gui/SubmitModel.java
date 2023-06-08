package com.ukarim.smppgui.gui;

import com.ukarim.smppgui.protocol.SmppConstants;
import com.ukarim.smppgui.protocol.pdu.Address;

public class SubmitModel {

    public enum DataCoding {

        DEFAULT(SmppConstants.DATA_CODING_DEFAULT, "Default data coding"),
        IA5(SmppConstants.DATA_CODING_IA5, "IA5 (ASCII)"),
        LATIN1(SmppConstants.DATA_CODING_LATIN1, "LATIN-1 (ISO-8859-1)"),
        UCS2(SmppConstants.DATA_CODING_UCS2, "UCS2");

        private final byte value;
        private final String desc;

        DataCoding(byte value, String desc) {
            this.value = value;
            this.desc = desc;
        }

        public byte getValue() {
            return value;
        }

        @Override
        public String toString() {
            return desc;
        }
    }

    private final Address srcAddress;
    private final Address destAddress;
    private final String shortMessage;
    private String serviceType;
    private byte registeredDelivery;
    private byte protocolId;
    private byte priorityFlag;
    private String schedDeliverTime;
    private String validityPeriod;
    private final DataCoding dataCoding;

    public SubmitModel(Address srcAddress, Address destAddress, String shortMessage, DataCoding dataCoding) {
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

    public String getShortMessage() {
        return shortMessage;
    }

    public DataCoding getDataCoding() {
        return dataCoding;
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
