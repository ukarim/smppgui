package com.ukarim.smppgui.gui;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

class EventDispatcher {

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

    void dispatch(EventType eventType, Object eventAttach) {
        SwingUtilities.invokeLater(() -> dispatchInternal(eventType, eventAttach));
    }

    void dispatchInternal(EventType eventType, Object eventAttach) {
        switch (eventType) {
            case PRINT_MSG: {
                loggingPane.printMsg((String) eventAttach);
                break;
            }
            case SHOW_ERROR: {
                var msg = (String) eventAttach;
                JOptionPane.showMessageDialog(null, msg, null, JOptionPane.ERROR_MESSAGE);
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
            case SHOW_SUBMIT_FORM: {
                formsPane.showSubmitForm();
                break;
            }
            case SHOW_LOGIN_FORM: {
                formsPane.showLoginForm();
                break;
            }
            default: {
                // NOOP
            }
        }
    }
}
