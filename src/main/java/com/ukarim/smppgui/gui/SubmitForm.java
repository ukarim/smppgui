package com.ukarim.smppgui.gui;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

class SubmitForm extends JPanel {

    private final EventDispatcher eventDispatcher;

    SubmitForm(EventDispatcher eventDispatcher) {
        this.eventDispatcher = eventDispatcher;
        setBorder(new EmptyBorder(10, 10, 10, 10));

        var layout = new GroupLayout(this);
        setLayout(layout);

        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        var disconnectButton = new JButton("Disconnect");
        disconnectButton.addActionListener((e) -> eventDispatcher.dispatch(EventType.DISCONNECT));

        layout.setHorizontalGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup()
                        .addComponent(disconnectButton)
                )
        );

        layout.setVerticalGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup()
                        .addComponent(disconnectButton)
                )
        );
    }
}
