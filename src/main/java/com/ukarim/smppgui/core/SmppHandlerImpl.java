package com.ukarim.smppgui.core;

import com.ukarim.smppgui.gui.EventDispatcher;
import com.ukarim.smppgui.gui.EventType;
import com.ukarim.smppgui.gui.LoginModel;
import com.ukarim.smppgui.gui.SubmitModel;
import com.ukarim.smppgui.protocol.SmppClient;
import com.ukarim.smppgui.protocol.SmppCmd;
import com.ukarim.smppgui.protocol.SmppConstants;
import com.ukarim.smppgui.protocol.SmppHandler;
import com.ukarim.smppgui.protocol.SmppStatus;
import com.ukarim.smppgui.protocol.pdu.BindPdu;
import com.ukarim.smppgui.protocol.pdu.BindRespPdu;
import com.ukarim.smppgui.protocol.pdu.DeliverSmRespPdu;
import com.ukarim.smppgui.protocol.pdu.HeaderPdu;
import com.ukarim.smppgui.protocol.pdu.Pdu;
import com.ukarim.smppgui.protocol.pdu.SubmitSmPdu;
import com.ukarim.smppgui.util.FmtUtils;
import com.ukarim.smppgui.util.SmppUtils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

public class SmppHandlerImpl implements SmppHandler {

    private static final int MAX_SHORT_MSG_LEN = 70;

    private final long TIMEOUT_MILLIS = TimeUnit.SECONDS.toMillis(10);

    private final AtomicInteger seqNumGenerator = new AtomicInteger();

    private final EventDispatcher eventDispatcher;
    private final SmppClient smppClient;

    public SmppHandlerImpl(EventDispatcher eventDispatcher) {
        this.eventDispatcher = eventDispatcher;
        this.smppClient = new SmppClient(this);
    }

    @Override
    public Pdu handlePdu(Pdu pdu, Throwable e) {
        if (e != null) {
            if (e instanceof IOException) {
                smppClient.disconnect();
                showErrorDialog("Network error: %s", e.getMessage());
                eventDispatcher.dispatch(EventType.SHOW_LOGIN_FORM);
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
        if (SmppCmd.DELIVER_SM.equals(cmd)) {
            return new DeliverSmRespPdu(SmppStatus.ESME_ROK, seqNum);
        }
        return null;
    }

    public void login(LoginModel loginModel) {
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
                eventDispatcher.dispatch(EventType.SHOW_SUBMIT_FORM);
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

    public void disconnect() {
        try {
            var unbind = new HeaderPdu(SmppCmd.UNBIND, nextSeqNum());
            printMsg("Pdu sent:\n%s", FmtUtils.fmtPdu(unbind));
            Pdu unbindResp = smppClient.sendPduSync(unbind, TIMEOUT_MILLIS);
            printMsg("Pdu received:\n%s", FmtUtils.fmtPdu(unbindResp));
        } catch (Exception e) {
            showErrorDialog("Disconnect error: %s", e.getMessage());
        } finally {
            smppClient.disconnect();
            eventDispatcher.dispatch(EventType.SHOW_LOGIN_FORM);
        }
    }

    public void submitMessage(SubmitModel submitModel) {
        try {
            String shortMessage = submitModel.getShortMessage();
            List<SubmitSmPdu> pduList = new ArrayList<>();
            if (shortMessage.length() > MAX_SHORT_MSG_LEN) {
                List<byte[]> udhParts = SmppUtils.toUdhPartsInUcs2(shortMessage);
                for (byte[] p : udhParts) {
                    var submitSmPdu = new SubmitSmPdu(
                            nextSeqNum(),
                            submitModel.getSrcAddress(),
                            submitModel.getDestAddress(),
                            p,
                            SmppConstants.DATA_CODING_UCS2
                    );
                    setSubmitSmOptionalFields(submitSmPdu, submitModel);
                    submitSmPdu.setEsmClass(SmppConstants.ESM_UDH_MASK);
                    pduList.add(submitSmPdu);
                }
            } else {
                var submitSmPdu = new SubmitSmPdu(
                        nextSeqNum(),
                        submitModel.getSrcAddress(),
                        submitModel.getDestAddress(),
                        SmppUtils.toUcs2Bytes(shortMessage),
                        SmppConstants.DATA_CODING_UCS2
                );
                setSubmitSmOptionalFields(submitSmPdu, submitModel);
                submitSmPdu.setEsmClass(SmppConstants.ESM_DEFAULT);
                pduList.add(submitSmPdu);
            }

            for (var submitSmPdu : pduList) {
                smppClient.sendPdu(submitSmPdu);
                printMsg("Pdu sent:\n%s", FmtUtils.fmtPdu(submitSmPdu));
            }
            showInfoDialog("Short message was sent");
        } catch (Exception e) {
            showErrorDialog("Submit error: %s", e.getMessage());
        }
    }

    private void setSubmitSmOptionalFields(SubmitSmPdu submitSmPdu, SubmitModel submitModel) {
        submitSmPdu.setServiceType(submitModel.getServiceType());
        submitSmPdu.setRegisteredDelivery(submitModel.getRegisteredDelivery());
        submitSmPdu.setProtocolId(submitModel.getProtocolId());
        submitSmPdu.setPriorityFlag(submitModel.getPriorityFlag());
        submitSmPdu.setScheduleDeliveryTime(submitModel.getSchedDeliverTime());
        submitSmPdu.setValidityPeriod(submitModel.getValidityPeriod());
    }

    private void printMsg(String fmt, Object... args) {
        eventDispatcher.dispatch(EventType.PRINT_MSG, String.format(fmt, args));
    }

    private void showErrorDialog(String fmt, Object... args) {
        eventDispatcher.dispatch(EventType.SHOW_ERROR, String.format(fmt, args));
    }

    private void showInfoDialog(String fmt, Object... args) {
        eventDispatcher.dispatch(EventType.SHOW_INFO, String.format(fmt, args));
    }

    private int nextSeqNum() {
        return seqNumGenerator.incrementAndGet();
    }
}
