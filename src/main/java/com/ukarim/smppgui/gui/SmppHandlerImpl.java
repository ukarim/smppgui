package com.ukarim.smppgui.gui;

import com.ukarim.smppgui.protocol.SmppClient;
import com.ukarim.smppgui.protocol.SmppCmd;
import com.ukarim.smppgui.protocol.SmppHandler;
import com.ukarim.smppgui.protocol.SmppStatus;
import com.ukarim.smppgui.protocol.pdu.BindPdu;
import com.ukarim.smppgui.protocol.pdu.BindRespPdu;
import com.ukarim.smppgui.protocol.pdu.HeaderPdu;
import com.ukarim.smppgui.protocol.pdu.Pdu;
import com.ukarim.smppgui.util.FmtUtils;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

// TODO maybe move to another package
class SmppHandlerImpl implements SmppHandler {

    private final long TIMEOUT_MILLIS = TimeUnit.SECONDS.toMillis(10);

    private final AtomicInteger seqNumGenerator = new AtomicInteger();

    private final EventDispatcher eventDispatcher;
    private final SmppClient smppClient;

    SmppHandlerImpl(EventDispatcher eventDispatcher) {
        this.eventDispatcher = eventDispatcher;
        this.smppClient = new SmppClient(this);
    }

    @Override
    public Pdu handlePdu(Pdu pdu, Throwable e) {
        if (e != null) {
            if (e instanceof IOException) {
                smppClient.disconnect();
                showErrorDialog("Network error: %s", e.getMessage());
                eventDispatcher.dispatchInternal(EventType.SHOW_LOGIN_FORM, null);
            }
            printMsg("Error occurred: %s", e.getMessage());
            return null;
        }
        printMsg("Pdu received:\n%s", FmtUtils.fmtPdu(pdu));

        Pdu respPdu = handlePduInternal(pdu);
        if (respPdu != null) {
            printMsg("Pdu sent:\n%s", FmtUtils.fmtPdu(respPdu));
        }
        return respPdu;
    }

    private Pdu handlePduInternal(Pdu pdu) {
        SmppCmd cmd = pdu.getCmd();
        SmppStatus sts = pdu.getSts();
        int seqNum = pdu.getSeqNum();
        if (SmppCmd.ENQUIRE_LINK.equals(cmd)) {
            // always respond to health check requests with OK status
            return new HeaderPdu(SmppCmd.ENQUIRE_LINK_RESP, SmppStatus.ESME_ROK, seqNum);
        }
        return null;
    }

    void login(LoginModel loginModel) {
        String host = loginModel.getHost();
        int port = loginModel.getPort();
        String systemId = loginModel.getSystemId();
        String password = new String(loginModel.getPassword());
        String systemType = loginModel.getSystemType();

        try {
            // establish tcp connection
            smppClient.connect(host, port);

            int seqNum = nextSeqNum();
            BindPdu bindReq = new BindPdu(SmppCmd.BIND_TRANSMITTER, seqNum, systemId, password);
            bindReq.setSystemType(systemType);

            // send bind request
            printMsg("Pdu sent:\n%s", FmtUtils.fmtPdu(bindReq));
            BindRespPdu bindResp = (BindRespPdu) smppClient.sendPduSync(bindReq, TIMEOUT_MILLIS);
            printMsg("Pdu received:\n%s", FmtUtils.fmtPdu(bindResp));
            SmppStatus respSts = bindResp.getSts();
            if (SmppStatus.ESME_ROK.equals(respSts)) {
                eventDispatcher.dispatch(EventType.SHOW_SUBMIT_FORM, null);
            } else {
                smppClient.disconnect();
                showErrorDialog("Connect failed: %s", respSts.getStatusDesc());
            }
        } catch (TimeoutException e) {
            smppClient.disconnect();
            showErrorDialog("Timeout: %s", e.getMessage());
        } catch (Exception e) {
            smppClient.disconnect();
            showErrorDialog("Error: %s", e.getMessage());
        }
    }

    private void printMsg(String fmt, Object... args) {
        eventDispatcher.dispatch(EventType.PRINT_MSG, String.format(fmt, args));
    }

    private void showErrorDialog(String fmt, Object args) {
        eventDispatcher.dispatch(EventType.SHOW_ERROR, String.format(fmt, args));
    }

    private int nextSeqNum() {
        return seqNumGenerator.incrementAndGet();
    }
}
