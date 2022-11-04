package com.ukarim.smppgui.gui;

import com.ukarim.smppgui.core.SmppHandlerImpl;
import java.awt.Desktop;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.net.URI;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

public class MainFrame extends JFrame {

    private static final String SMPP_ORG_URL = "https://smpp.org/";
    private static final String SMPPGUI_REPO_URL = "https://github.com/ukarim/smppgui";

    private static final int WIDTH = 1200;
    private static final int HEIGHT = 600;

    public MainFrame() {
        setSize(WIDTH, HEIGHT);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("SMPP GUI");

        setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/icon.png")));

        setJMenuBar(buildMenuBar());

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

    private JMenuBar buildMenuBar() {
        var helpMenu = new JMenu("Help");

        JMenuItem smppWebsiteMenuItem = new JMenuItem("smpp.org website");
        helpMenu.add(smppWebsiteMenuItem);
        smppWebsiteMenuItem.addActionListener(e -> openLink(SMPP_ORG_URL));

        helpMenu.addSeparator();

        JMenuItem sourceCodeMenuItem = new JMenuItem("smppgui source code");
        helpMenu.add(sourceCodeMenuItem);
        sourceCodeMenuItem.addActionListener(e -> openLink(SMPPGUI_REPO_URL));

        var menuBar = new JMenuBar();
        menuBar.add(helpMenu);
        return menuBar;
    }

    private void openLink(String link) {
        try {
            Desktop.getDesktop().browse(new URI(link));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), null, JOptionPane.ERROR_MESSAGE);
        }
    }
}
