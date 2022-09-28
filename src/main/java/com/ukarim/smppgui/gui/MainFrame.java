package com.ukarim.smppgui.gui;

import java.awt.Toolkit;
import javax.swing.JFrame;

public class MainFrame extends JFrame {

    public MainFrame() {
        setSize(400, 500);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("SMPP GUI");

        setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/icon.png")));
    }
}
