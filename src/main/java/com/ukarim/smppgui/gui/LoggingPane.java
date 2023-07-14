package com.ukarim.smppgui.gui;

import java.util.LinkedList;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.border.EmptyBorder;

class LoggingPane extends JScrollPane {

    private static final int MAX_MSG_COUNT = 50; // preserve only last 50 messages
    private static final String LOGS_HEADER = String.format("The last %s log messages will be here", MAX_MSG_COUNT);

    private final LinkedList<String> messages = new LinkedList<>();

    private final JTextPane textPane = new JTextPane();

    LoggingPane() {
        super();
        setBorder(new EmptyBorder(0, 0, 0, 0));
        setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        setViewportView(textPane);
        textPane.setEditable(false);
        textPane.setText(LOGS_HEADER);
        messages.add(LOGS_HEADER);
    }

    void printMsg(String msg) {
        if (messages.size() >= MAX_MSG_COUNT+1) { // plus 1 for header msg
            messages.removeFirst();
        }
        messages.addLast(msg);

        String text = String.join("\n---------------\n", messages);
        textPane.setText(text);
        textPane.repaint();
    }

    void clearLogs() {
        messages.clear();
        printMsg(LOGS_HEADER);
    }
}
