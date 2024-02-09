package com.ukarim.smppgui.gui;

import com.ukarim.smppgui.core.SmppHandlerImpl;
import com.ukarim.smppgui.util.CharsetWrapper;
import com.ukarim.smppgui.util.GsmCharset;
import com.ukarim.smppgui.util.Resources;
import java.awt.*;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import javax.swing.*;

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

        var toolsMenu = new JMenu("Tools");
        JMenuItem stringToHexItem = new JMenuItem("Text to Hex");
        toolsMenu.add(stringToHexItem);
        stringToHexItem.addActionListener(e -> {
            performConversionFromStringToHex();
        });
        var menuBar = new JMenuBar();
        menuBar.add(toolsMenu);
        menuBar.add(helpMenu);
        return menuBar;
    }

    private void performConversionFromStringToHex() {
        JTextArea stringValue = new JTextArea();
        stringValue.setPreferredSize(new Dimension(500,30));
        Charset[] charsets = {
                GsmCharset.INSTANCE_8BIT,
                GsmCharset.INSTANCE_7BIT,
                new CharsetWrapper("IA5:ASCII", StandardCharsets.US_ASCII),
                new CharsetWrapper("UCS2", StandardCharsets.UTF_16BE),
                new CharsetWrapper("LATIN-1:ISO-8859-1", StandardCharsets.ISO_8859_1),
        };
        var spinnerModel = new SpinnerListModel(charsets);
        spinnerModel.setValue(GsmCharset.INSTANCE_8BIT); // set default value
        JSpinner dataCodingSpinner = new JSpinner(spinnerModel);
        dataCodingSpinner.setPreferredSize(new Dimension(500,30));

        var spinnerEditor = new JSpinner.DefaultEditor(dataCodingSpinner);
        spinnerEditor.getTextField().setEditable(false);
        spinnerEditor.getTextField().setHorizontalAlignment(JTextField.LEFT);
        dataCodingSpinner.setEditor(spinnerEditor);
        Object[] message = {
                "Text : ", stringValue,
                "Charset : ", dataCodingSpinner
        };
        int option = JOptionPane.showConfirmDialog(this,
                message,"Text to Hex convertor",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                new ImageIcon(ICON));
        if(option == JOptionPane.OK_OPTION) {
            if(!stringValue.getText().isEmpty() && !(dataCodingSpinner.getModel().getValue()).toString().isEmpty()) {
                   String convertedHexValue =  new BigInteger(stringValue.getText().getBytes(getCharset((Charset) dataCodingSpinner.getModel().getValue()))).toString(16);
                   JTextField convertedHexField =  new JTextField(convertedHexValue);
                   convertedHexField.setPreferredSize(new Dimension(500,30));
                   convertedHexField.setEditable(false);
                   Object[] convertedMessage = {
                           "Hex value for text " +stringValue.getText()+" is " , convertedHexField
                   };
                   JOptionPane.showMessageDialog(this,convertedMessage, "Converted value", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,"Either Text or Charset value is empty", "Failed", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    public Charset getCharset(Charset dataCoding) {
        Charset charset = null;
        switch (dataCoding.toString()) {
            case "GSM-8bit":
                charset = GsmCharset.INSTANCE_8BIT;
                break;
            case "GSM-7bit":
                charset = GsmCharset.INSTANCE_7BIT;
                break;
            case "IA5:ASCII":
                charset = StandardCharsets.US_ASCII;
                break;
            case "UCS2":
                charset = StandardCharsets.UTF_16BE;
                break;
            case "LATIN-1:ISO-8859-1":
                charset = StandardCharsets.ISO_8859_1;
                break;
            default:
                // should not happen
                throw new IllegalArgumentException("Unknown data coding " + dataCoding);
        }
        return charset;
    }
}
