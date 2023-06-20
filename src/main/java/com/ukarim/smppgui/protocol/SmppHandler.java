package com.ukarim.smppgui.protocol;

import com.ukarim.smppgui.protocol.pdu.Pdu;
import com.ukarim.smppgui.gui.SubmitModel;
import com.ukarim.smppgui.gui.LoginModel;

public interface SmppHandler {

    Pdu handlePdu(Pdu pdu, Throwable ex);
	
	void login(LoginModel loginModel);
	
	void disconnect();
	
	void submitMessage(SubmitModel submitModel);
}
