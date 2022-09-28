package com.ukarim.smppgui;

import com.ukarim.smppgui.gui.MainFrame;
import javax.swing.SwingUtilities;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            var mainFrame = new MainFrame();

            // make it appear at the center of screen
            mainFrame.setLocationRelativeTo(null);

            mainFrame.setVisible(true);
        });
    }
}
