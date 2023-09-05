package com.ukarim.smppgui.gui;

import com.ukarim.smppgui.core.SmppHandlerImpl;
import com.ukarim.smppgui.util.Resources;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class EventDispatcher {

    private static final ImageIcon INFO_ICON = Resources.loadIcon("/info.png");
    private static final ImageIcon ERROR_ICON = Resources.loadIcon("/error.png");

    private final ExecutorService workerThread = Executors.newSingleThreadExecutor();
    private LoggingPane loggingPane;
    private FormsPane formsPane;
    private SmppHandlerImpl smppHandler;

    void setLoggingPane(LoggingPane loggingPane) {
        this.loggingPane = loggingPane;
    }

    void setFormsPane(FormsPane formsPane) {
        this.formsPane = formsPane;
    }

    void setSmppHandler(SmppHandlerImpl smppHandler) {
        this.smppHandler = smppHandler;
    }

    public void dispatch(EventType eventType) {
        dispatch(eventType, null);
    }

    public void dispatch(EventType eventType, Object eventAttach) {
        SwingUtilities.invokeLater(() -> dispatchInternal(eventType, eventAttach));
    }

    private void dispatchInternal(EventType eventType, Object eventAttach) {
        switch (eventType) {
            case PRINT_MSG: {
                loggingPane.printMsg((String) eventAttach);
                break;
            }
            case SHOW_ERROR: {
                var msg = (String) eventAttach;
                JOptionPane.showMessageDialog(null, msg, null, JOptionPane.ERROR_MESSAGE, ERROR_ICON);
                break;
            }
            case SHOW_INFO: {
                var msg = (String) eventAttach;
                JOptionPane.showMessageDialog(null, msg, null, JOptionPane.INFORMATION_MESSAGE, INFO_ICON);
                break;
            }
            case DO_LOGIN: {
                // Do login in background thread (blocking task)
                workerThread.execute(() -> {
                    LoginModel loginModel = (LoginModel) eventAttach;
                    smppHandler.login(loginModel);
                });
                break;
            }
            case DISCONNECT: {
                // Do disconnect in background thread (blocking task)
                workerThread.execute(() -> smppHandler.disconnect());
                break;
            }
            case SHOW_SUBMIT_FORM: {
                formsPane.showSubmitForm();
                break;
            }
            case SHOW_LOGIN_FORM: {
                formsPane.showLoginForm();
                break;
            }
            case DO_SUBMIT: {
                // Do submit in background thread (blocking task)
                workerThread.execute(() -> smppHandler.submitMessage((SubmitModel) eventAttach));
                break;
            }
            case CLEAR_LOGS:
                loggingPane.clearLogs();
                break;
            case TOGGLE_ENQ_LINK_LOGS:
                smppHandler.toggleEnqLinkLogs();
                break;
            default: {
                // NOOP
            }
        }
    }
}
