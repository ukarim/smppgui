package com.ukarim.smppgui.gui;

import com.ukarim.smppgui.util.CharsetWrapper;
import com.ukarim.smppgui.util.GsmCharset;
import com.ukarim.smppgui.util.StringUtils;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerListModel;

class HexToolPanel extends JPanel {

  HexToolPanel() {
    var textArea = new JTextArea();
    textArea.setLineWrap(true);
    textArea.setRows(5);
    textArea.setColumns(30);
    var scrollPane = new JScrollPane(textArea);
    scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

    Charset[] charsets = {
        GsmCharset.INSTANCE_8BIT,
        GsmCharset.INSTANCE_7BIT,
        new CharsetWrapper("IA5:ASCII", StandardCharsets.US_ASCII),
        new CharsetWrapper("UCS2", StandardCharsets.UTF_16BE),
        new CharsetWrapper("LATIN-1:ISO-8859-1", StandardCharsets.ISO_8859_1),
    };

    var spinnerModel = new SpinnerListModel(charsets);
    spinnerModel.setValue(GsmCharset.INSTANCE_8BIT); // set default value
    var dataCodingSpinner = new JSpinner(spinnerModel);
    var spinnerEditor = new JSpinner.DefaultEditor(dataCodingSpinner);
    spinnerEditor.getTextField().setEditable(false);
    spinnerEditor.getTextField().setHorizontalAlignment(JTextField.LEFT);
    dataCodingSpinner.setEditor(spinnerEditor);

    var convertButton = new JButton("Convert");
    convertButton.addActionListener(e -> {
      String text = textArea.getText();
      if (text == null || text.isEmpty()) {
        return;
      }
      Charset charset = (Charset) spinnerModel.getValue();
      textArea.setText(StringUtils.toHexString(text.getBytes(charset)));
    });

    var copyButton = new JButton("Copy");
    copyButton.addActionListener(e -> {
      Clipboard clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
      clpbrd.setContents(new StringSelection(textArea.getText()), null);
    });

    var sidePanel = new JPanel(new GridBagLayout());
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.weightx = 1;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.gridwidth = GridBagConstraints.REMAINDER;

    sidePanel.add(dataCodingSpinner, gbc);
    sidePanel.add(convertButton, gbc);
    sidePanel.add(copyButton, gbc);

    add(scrollPane);
    add(sidePanel);
  }
}
