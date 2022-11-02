package com.ukarim.smppgui.gui;

import com.ukarim.smppgui.core.SmppHandlerImpl;
import java.awt.GridLayout;
import java.awt.Toolkit;
import javax.swing.JFrame;

public class MainFrame extends JFrame {

    public static final int WIDTH = 800;
    public static final int HEIGHT = 600;

    public MainFrame() {
        setSize(WIDTH, HEIGHT);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("SMPP GUI");

        setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/icon.png")));

        var eventDispatcher = new EventDispatcher();

        var loggingPane = new LoggingPane();
        var formsPane = new FormsPane(eventDispatcher);
        var smppHandler = new SmppHandlerImpl(eventDispatcher);

        eventDispatcher.setLoggingPane(loggingPane);
        eventDispatcher.setFormsPane(formsPane);
        eventDispatcher.setSmppHandler(smppHandler);

        setLayout(new GridLayout(1, 2));

        add(formsPane);
        add(loggingPane);
    }
}
