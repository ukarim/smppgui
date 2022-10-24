package com.ukarim.smppgui.gui;

import java.awt.GridLayout;
import java.awt.Toolkit;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

public class MainFrame extends JFrame {

    private final JPanel container;

    private final LoggingPane loggingPane;

    private final LoginForm loginForm;

    public MainFrame() {
        setSize(700, 600);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("SMPP GUI");

        setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/icon.png")));

        this.container = new JPanel();
        this.loggingPane = new LoggingPane();
        this.loginForm = new LoginForm(loggingPane);

        initGui();
    }

    private void initGui() {
        var scrollPane = new JScrollPane(container);
        scrollPane.setBorder(new EmptyBorder(0, 0, 0, 0));
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        getContentPane().add(scrollPane);

        container.setLayout(new GridLayout(1, 2));

        container.add(loginForm);
        container.add(loggingPane);
    }
}
