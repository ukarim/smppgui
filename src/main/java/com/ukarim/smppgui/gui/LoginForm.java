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

    private final LoggingPane loggingPane;

    private final JTextField hostField;
    private final JTextField portField;
    private final JTextField systemIdField;
    private final JPasswordField passwordField;
    private final JTextField serviceTypeField;
    private final JButton button;

    LoginForm(LoggingPane loggingPane) {
        this.loggingPane = loggingPane;
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

        var serviceTypeLabel = new JLabel("Service type: ");
        serviceTypeField = new JTextField();

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
                        .addComponent(serviceTypeField)
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
                        .addComponent(serviceTypeField)
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
            loggingPane.printMsg("Host not provided");
            return;
        }

        String port = portField.getText();
        if (port == null || port.isBlank()) {
            loggingPane.printMsg("Port not provided");
            return;
        }
        port = port.trim();
        if (port.startsWith("-")) { // negative values
            loggingPane.printMsg("Invalid port value provided");
            return;
        }

        int portNum;
        try {
            portNum = Integer.parseInt(port);
        } catch (Exception e) {
            loggingPane.printMsg("Invalid port value provided");
            return;
        }

        String systemId = systemIdField.getText();
        if (systemId == null || systemId.isBlank()) {
            loggingPane.printMsg("System ID not provided");
            return;
        }

        char[] password = passwordField.getPassword();
        if (password == null || password.length == 0) {
            loggingPane.printMsg("Password not provided");
            return;
        }

        // TODO smpp bind request
    }
}
