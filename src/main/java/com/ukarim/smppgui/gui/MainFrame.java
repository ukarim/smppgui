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
import java.awt.Taskbar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class MainFrame extends JFrame {

    private static final int WIDTH = 1200;
    private static final int HEIGHT = 700;
    private static final Image ICON = Resources.loadImage("/icon.png");

    public MainFrame() {
        setSize(WIDTH, HEIGHT);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("SMPP GUI");

        setIconImage(ICON);
        Taskbar.getTaskbar().setIconImage(ICON);

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
            var textArea = new JTextArea();
            textArea.setEditable(false);
            textArea.setOpaque(false);
            textArea.setText(Resources.loadStr("/about.txt"));

            var scrollPane = new JScrollPane(textArea);
            scrollPane.setOpaque(false);
            scrollPane.getViewport().setOpaque(false);
            scrollPane.setBorder(null);
            JOptionPane.showMessageDialog(this, scrollPane, "", JOptionPane.INFORMATION_MESSAGE, new ImageIcon(ICON));
        });

        JMenuItem licenseItem = new JMenuItem("License");
        helpMenu.add(licenseItem);
        licenseItem.addActionListener(e -> {
            var textArea = new JTextArea();
            textArea.setEditable(false);
            textArea.setOpaque(false);
            textArea.setText(Resources.loadStr("/META-INF/LICENSE.txt"));

            var scrollPane = new JScrollPane(textArea);
            scrollPane.setOpaque(false);
            scrollPane.getViewport().setOpaque(false);
            scrollPane.setBorder(null);
            JOptionPane.showMessageDialog(this, scrollPane, "", JOptionPane.PLAIN_MESSAGE);
        });

        var toolsMenu = new JMenu("Tools");
        JMenuItem stringToHexItem = new JMenuItem("Encode text as hex string");
        toolsMenu.add(stringToHexItem);
        stringToHexItem.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, new HexToolPanel(true), "Text encoder", JOptionPane.PLAIN_MESSAGE);
        });

        JMenuItem hexToStringItem = new JMenuItem("Decode hex string");
        toolsMenu.add(hexToStringItem);
        hexToStringItem.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, new HexToolPanel(false), "Text decoder", JOptionPane.PLAIN_MESSAGE);
        });

        var menuBar = new JMenuBar();
        menuBar.add(toolsMenu);
        menuBar.add(helpMenu);
        return menuBar;
    }
}
