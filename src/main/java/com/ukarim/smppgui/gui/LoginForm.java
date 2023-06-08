package com.ukarim.smppgui.gui;

import com.ukarim.smppgui.gui.LoginModel.SessionType;
import com.ukarim.smppgui.util.CharsetWrapper;
import com.ukarim.smppgui.util.GsmCharset;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerListModel;
import javax.swing.border.EmptyBorder;

class LoginForm extends JPanel implements ActionListener {

    private final EventDispatcher eventDispatcher;

    private final JTextField hostField;
    private final JTextField portField;
    private final JTextField systemIdField;
    private final JPasswordField passwordField;
    private final JTextField systemTypeField;
    private final JButton button;
    private final JSpinner sessionTypeSpinner;
    private final JSpinner charsetSpinner;

    LoginForm(EventDispatcher eventDispatcher) {
        this.eventDispatcher = eventDispatcher;
        setBorder(new EmptyBorder(10, 10, 10, 10));
        var layout = new GroupLayout(this);
        setLayout(layout);

        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        var hostLabel = new JLabel("Host");
        hostField = new JTextField();

        var portLabel = new JLabel("Port");
        portField = new JTextField();

        var systemIdLabel = new JLabel("System ID");
        systemIdField = new JTextField();

        var passwordLabel = new JLabel("Password");
        passwordField = new JPasswordField();

        var sessionTypeLabel = new JLabel("Session type");
        sessionTypeSpinner = createSpinner(SessionType.values(), SessionType.TRANSMITTER);

        var serviceTypeLabel = new JLabel("System type");
        systemTypeField = new JTextField();

        var dataCodingLabel = new JLabel("Default data coding");
        Charset[] charsets = {
                GsmCharset.INSTANCE_8BIT,
                GsmCharset.INSTANCE_7BIT,
                new CharsetWrapper("IA5:ASCII", StandardCharsets.US_ASCII),
                new CharsetWrapper("UCS2", StandardCharsets.UTF_16BE),
                new CharsetWrapper("LATIN-1:ISO-8859-1", StandardCharsets.ISO_8859_1),
        };
        charsetSpinner = createSpinner(charsets, GsmCharset.INSTANCE_8BIT);

        button = new JButton("Connect");
        button.addActionListener(this);

        layout.setHorizontalGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(Alignment.TRAILING)
                        .addComponent(hostLabel)
                        .addComponent(portLabel)
                        .addComponent(systemIdLabel)
                        .addComponent(passwordLabel)
                        .addComponent(sessionTypeLabel)
                        .addComponent(serviceTypeLabel)
                        .addComponent(dataCodingLabel)
                )
                .addGroup(layout.createParallelGroup(Alignment.LEADING)
                        .addComponent(hostField)
                        .addComponent(portField)
                        .addComponent(systemIdField)
                        .addComponent(passwordField)
                        .addComponent(sessionTypeSpinner)
                        .addComponent(systemTypeField)
                        .addComponent(charsetSpinner)
                        .addComponent(button)
                )
        );

        layout.setVerticalGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(Alignment.BASELINE)
                        .addComponent(hostLabel)
                        .addComponent(hostField)
                )
                .addGroup(layout.createParallelGroup(Alignment.BASELINE)
                        .addComponent(portLabel)
                        .addComponent(portField)
                )
                .addGroup(layout.createParallelGroup(Alignment.BASELINE)
                        .addComponent(systemIdLabel)
                        .addComponent(systemIdField)
                )
                .addGroup(layout.createParallelGroup(Alignment.BASELINE)
                        .addComponent(passwordLabel)
                        .addComponent(passwordField)
                )
                .addGroup(layout.createParallelGroup(Alignment.BASELINE)
                        .addComponent(sessionTypeLabel)
                        .addComponent(sessionTypeSpinner)
                )
                .addGroup(layout.createParallelGroup(Alignment.BASELINE)
                        .addComponent(serviceTypeLabel)
                        .addComponent(systemTypeField)
                )
                .addGroup(layout.createParallelGroup(Alignment.BASELINE)
                        .addComponent(dataCodingLabel)
                        .addComponent(charsetSpinner)
                )
                .addGroup(layout.createParallelGroup(Alignment.BASELINE)
                        .addComponent(button)
                )
        );
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        String host = hostField.getText();
        if (host == null || host.isBlank()) {
            showError("Host not provided");
            return;
        }

        String port = portField.getText();
        if (port == null || port.isBlank()) {
            showError("Port not provided");
            return;
        }
        port = port.trim();
        if (port.startsWith("-")) { // negative values
            showError("Invalid port value provided");
            return;
        }

        int portNum;
        try {
            portNum = Integer.parseInt(port);
        } catch (Exception e) {
            showError("Invalid port value provided");
            return;
        }

        String systemId = systemIdField.getText();
        if (systemId == null || systemId.isBlank()) {
            showError("System ID not provided");
            return;
        }

        char[] password = passwordField.getPassword();
        if (password == null || password.length == 0) {
            showError("Password not provided");
            return;
        }

        var sessionType = (LoginModel.SessionType) sessionTypeSpinner.getModel().getValue();
        var charset = (Charset) charsetSpinner.getModel().getValue();

        String systemType = systemTypeField.getText();

        eventDispatcher.dispatch(EventType.DO_LOGIN,
                new LoginModel(host, portNum, systemId, password, sessionType, systemType, charset));

        // cleanup pwd
        passwordField.setText("");
    }

    private void showError(String msg) {
        eventDispatcher.dispatch(EventType.SHOW_ERROR, msg);
    }

    private JSpinner createSpinner(Object[] values, Object initialValue) {
        var spinnerModel = new SpinnerListModel(values);
        spinnerModel.setValue(initialValue); // set initial value
        var spinner = new JSpinner(spinnerModel);

        // make spinner not editable
        var spinnerEditor = new JSpinner.DefaultEditor(spinner);
        spinnerEditor.getTextField().setEditable(false);
        spinner.setEditor(spinnerEditor);
        return spinner;
    }
}
