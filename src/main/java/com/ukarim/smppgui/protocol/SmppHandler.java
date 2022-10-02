package com.ukarim.smppgui.protocol;

public interface SmppHandler {

    Pdu handlePdu(Pdu pdu, Throwable ex);
}
