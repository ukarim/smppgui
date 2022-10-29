package com.ukarim.smppgui.gui;

import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

class SubmitForm extends JPanel {

    private final EventDispatcher eventDispatcher;

    SubmitForm(EventDispatcher eventDispatcher) {
        this.eventDispatcher = eventDispatcher;
        setBorder(new EmptyBorder(10, 10, 10, 10));

    }
}
