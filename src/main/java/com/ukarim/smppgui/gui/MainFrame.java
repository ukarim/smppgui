package com.ukarim.smppgui.gui;

import com.ukarim.smppgui.core.SmppHandlerImpl;
import com.ukarim.smppgui.util.Resources;
import java.awt.GridLayout;
import java.awt.Image;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

public class MainFrame extends JFrame {

    private static final int WIDTH = 1200;
    private static final int HEIGHT = 700;
    private static final Image ICON = Resources.loadImage("/icon.png");
    private static final String ABOUT_INFO = Resources.loadStr("/about.txt");

    public MainFrame() {
        setSize(WIDTH, HEIGHT);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("SMPP GUI");

        setIconImage(ICON);

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

        JMenuItem aboutMenuItem = new JMenuItem("About");
        helpMenu.add(aboutMenuItem);
        aboutMenuItem.addActionListener(e -> {
            JOptionPane.showMessageDialog(
                this,
                ABOUT_INFO,
                "",
                JOptionPane.INFORMATION_MESSAGE,
                new ImageIcon(ICON)
            );
        });

        var menuBar = new JMenuBar();
        menuBar.add(helpMenu);
        return menuBar;
    }
}
