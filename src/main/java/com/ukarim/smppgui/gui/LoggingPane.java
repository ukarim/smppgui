package com.ukarim.smppgui.gui;

import java.util.LinkedList;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.border.EmptyBorder;

class LoggingPane extends JScrollPane {

    private static final int MAX_MSG_COUNT = 20; // preserve only last 20 messages

    private final LinkedList<String> messages = new LinkedList<>();

    private final JTextPane textPane = new JTextPane();

    LoggingPane() {
        super();
        setBorder(new EmptyBorder(0, 0, 0, 0));
        setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        setViewportView(textPane);
        textPane.setEditable(false);
    }

    // Print in reverse order (new messages appear at the top)
    void printMsg(String msg) {
        if (messages.size() >= MAX_MSG_COUNT) {
            messages.removeLast();
        }
        messages.addFirst(msg);

        String text = String.join("\n-----\n", messages);
        textPane.setText(text);
        textPane.setCaretPosition(0);
        textPane.repaint();
    }
}
