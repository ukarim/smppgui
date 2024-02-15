package com.ukarim.smppgui.gui;

import com.ukarim.smppgui.core.SmppHandlerImpl;
import com.ukarim.smppgui.util.CharsetWrapper;
import com.ukarim.smppgui.util.GsmCharset;
import com.ukarim.smppgui.util.Resources;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerListModel;
import javax.swing.border.BevelBorder;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Panel;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

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
        // Define the Text input with scroll bar
        JTextArea jTextAreaText = new JTextArea();
        jTextAreaText.setLineWrap(true);
        jTextAreaText.setRows(5);
        jTextAreaText.setSize(500, 1);
        JScrollPane jScrollPaneText = new JScrollPane(jTextAreaText);
        jScrollPaneText.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        //Define the value dropdown for charset
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
        var spinnerEditor = new JSpinner.DefaultEditor(dataCodingSpinner);
        spinnerEditor.getTextField().setEditable(false);
        spinnerEditor.getTextField().setHorizontalAlignment(JTextField.LEFT);
        dataCodingSpinner.setEditor(spinnerEditor);
        dataCodingSpinner.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
        dataCodingSpinner.setPreferredSize(new Dimension(200, 30));

        //Adding the fields to the panel
        Panel inputPanel = new Panel();
        inputPanel.add(jScrollPaneText);
        inputPanel.add(dataCodingSpinner);

        int option = JOptionPane.showConfirmDialog(this,
                inputPanel,
                "Text to Hex convertor",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);
        if (option == JOptionPane.OK_OPTION) {
            if (!jTextAreaText.getText().isEmpty() && !(dataCodingSpinner.getModel().getValue()).toString().isEmpty()) {
                String convertedHexValue = new BigInteger(jTextAreaText.getText().getBytes(getCharset((Charset) dataCodingSpinner.getModel().getValue()))).toString(16);

                //Setting the string value to the TextArea with a scroll bar
                JTextArea jTextAreaConvertedHexValue = new JTextArea(convertedHexValue);
                jTextAreaConvertedHexValue.setEditable(false);
                jTextAreaConvertedHexValue.setRows(5);
                jTextAreaConvertedHexValue.setLineWrap(true);
                jTextAreaConvertedHexValue.setSize(500, 1);
                JScrollPane scroll = new JScrollPane(jTextAreaConvertedHexValue);
                scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

                //Define the copy button
                JButton button = new JButton();
                button.setText("Copy");
                button.addActionListener(e -> {
                    StringSelection stringSelection = new StringSelection(convertedHexValue);
                    Clipboard clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
                    clpbrd.setContents(stringSelection, null);
                });

                //Define the output panel
                Panel outputPanel = new Panel();
                outputPanel.add(scroll);
                outputPanel.add(button);

                JOptionPane.showMessageDialog(this, outputPanel, "Converted value", JOptionPane.PLAIN_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Either Text or Charset value is empty", "Failed", JOptionPane.ERROR_MESSAGE);
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
