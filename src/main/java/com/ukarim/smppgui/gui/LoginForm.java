package com.ukarim.smppgui.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

class LoginForm extends JPanel implements ActionListener {

    private final EventDispatcher eventDispatcher;

    private final JTextField hostField;
    private final JTextField portField;
    private final JTextField systemIdField;
    private final JPasswordField passwordField;
    private final JTextField systemTypeField;
    private final JButton button;

    LoginForm(EventDispatcher eventDispatcher) {
        this.eventDispatcher = eventDispatcher;
        setBorder(new EmptyBorder(10, 10, 10, 10));
        var layout = new GroupLayout(this);
        setLayout(layout);

        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        var hostLabel = new JLabel("Host: ");
        hostField = new JTextField();

        var portLabel = new JLabel("Port: ");
        portField = new JTextField();

        var systemIdLabel = new JLabel("System ID: ");
        systemIdField = new JTextField();

        var passwordLabel = new JLabel("Password: ");
        passwordField = new JPasswordField();

        var serviceTypeLabel = new JLabel("System type: ");
        systemTypeField = new JTextField();

        button = new JButton("Connect");
        button.addActionListener(this);

        layout.setHorizontalGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(Alignment.TRAILING)
                        .addComponent(hostLabel)
                        .addComponent(portLabel)
                        .addComponent(systemIdLabel)
                        .addComponent(passwordLabel)
                        .addComponent(serviceTypeLabel)
                )
                .addGroup(layout.createParallelGroup(Alignment.LEADING)
                        .addComponent(hostField)
                        .addComponent(portField)
                        .addComponent(systemIdField)
                        .addComponent(passwordField)
                        .addComponent(systemTypeField)
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
                        .addComponent(serviceTypeLabel)
                        .addComponent(systemTypeField)
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

        String systemType = systemTypeField.getText();

        eventDispatcher.dispatch(EventType.DO_LOGIN,
                new LoginModel(host, portNum, systemId, password, systemType));
    }

    private void showError(String msg) {
        eventDispatcher.dispatch(EventType.SHOW_ERROR, msg);
    }
}
