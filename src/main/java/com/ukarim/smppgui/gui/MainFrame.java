package com.ukarim.smppgui.gui;

import com.ukarim.smppgui.core.Config;
import com.ukarim.smppgui.core.SmppHandlerImpl;
import com.ukarim.smppgui.util.Resources;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import java.awt.GridLayout;
import java.awt.Image;

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

        var config = Config.loadConfig();
        var eventDispatcher = new EventDispatcher(config);

        var loggingPane = new LoggingPane();
        var formsPane = new FormsPane(eventDispatcher, config);
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
            JOptionPane.showMessageDialog(this, ABOUT_INFO, "", JOptionPane.INFORMATION_MESSAGE, new ImageIcon(ICON));
        });

        var toolsMenu = new JMenu("Tools");
        JMenuItem stringToHexItem = new JMenuItem("Text to Hex");
        toolsMenu.add(stringToHexItem);
        stringToHexItem.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, new HexToolPanel(), "Text to Hex convertor", JOptionPane.PLAIN_MESSAGE);
        });
        var menuBar = new JMenuBar();
        menuBar.add(toolsMenu);
        menuBar.add(helpMenu);
        return menuBar;
    }
}
