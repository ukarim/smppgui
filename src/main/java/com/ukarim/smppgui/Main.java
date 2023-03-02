package com.ukarim.smppgui;

import com.ukarim.smppgui.gui.MainFrame;
import java.awt.Color;
import java.util.Enumeration;
import javax.swing.SwingUtilities;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.FontUIResource;

public class Main {

    public static void main(String[] args) {
        setupUI();
        SwingUtilities.invokeLater(() -> {
            var mainFrame = new MainFrame();

            // make it appear at the center of screen
            mainFrame.setLocationRelativeTo(null);

            mainFrame.setVisible(true);
        });
    }

    private static void setupUI() {
        // Ensure anti-aliasing is enabled
        System.setProperty("swing.aatext", "true");

        UIDefaults defaults = UIManager.getLookAndFeelDefaults();

        // Ensure that font size is greater than 16
        Enumeration<Object> keys = defaults.keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            if ((key instanceof String) && (((String) key).endsWith(".font"))) {
                FontUIResource font = (FontUIResource) UIManager.get(key);
                int size = Math.max(font.getSize(), 16);
                defaults.put(key, new FontUIResource(font.getFontName(), font.getStyle(), size));
            }
        }

        // Disable focus borders for buttons
        defaults.put("Button.focus", new ColorUIResource(new Color(0, 0, 0, 0)));
    }
}
