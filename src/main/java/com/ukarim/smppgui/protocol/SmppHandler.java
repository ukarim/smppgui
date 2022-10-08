package com.ukarim.smppgui.protocol;

import com.ukarim.smppgui.protocol.pdu.Pdu;

public interface SmppHandler {

    Pdu handlePdu(Pdu pdu, Throwable ex);
}
