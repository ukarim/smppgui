package com.ukarim.smppgui.gui;

import java.awt.CardLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

class FormsPane extends JScrollPane {

    private static final String LOGIN_FORM = "loginForm";
    private static final String SUBMIT_FORM = "submitForm";

    private final JPanel container = new JPanel();
    private final CardLayout containerLayout = new CardLayout();

    FormsPane(EventDispatcher eventDispatcher) {
        super();
        var loginForm = new LoginForm(eventDispatcher);
        var submitForm = new SubmitForm(eventDispatcher);

        setBorder(new EmptyBorder(0, 0, 0, 0));
        setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        setViewportView(container);

        container.setBorder(new EmptyBorder(0, 0, 0, 0));
        container.setLayout(containerLayout);
        container.add(loginForm, LOGIN_FORM);
        container.add(submitForm, SUBMIT_FORM);
    }

    void showSubmitForm() {
        containerLayout.show(container, SUBMIT_FORM);
    }

    void showLoginForm() {
        containerLayout.show(container, LOGIN_FORM);
    }
}
