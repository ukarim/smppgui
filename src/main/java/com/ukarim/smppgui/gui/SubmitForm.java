package com.ukarim.smppgui.gui;

import com.ukarim.smppgui.protocol.pdu.Address;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import static com.ukarim.smppgui.util.StringUtils.isEmpty;

class SubmitForm extends JPanel implements ActionListener {

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("yyMMddHHmmss");
    private static final int TIME_STR_LEN = 16;

    private final EventDispatcher eventDispatcher;

    private final JTextField serviceTypeField = new JTextField();

    private final JTextField srcAddrField = new JTextField();
    private final JTextField srcAddrTonField = new JTextField();
    private final JTextField srcAddrNpiField = new JTextField();

    private final JTextField destAddrField = new JTextField();
    private final JTextField destAddrTonField = new JTextField();
    private final JTextField destAddrNpiField = new JTextField();

    private final JTextField protocolIdField = new JTextField();
    private final JTextField priorityFlagField = new JTextField();

    private final JTextField schedDeliveryTimeField = new JTextField();
    private final JTextField validityPeriodField = new JTextField();

    private final JTextField registeredDeliveryField = new JTextField();

    private final JTextArea shortMessageTextArea = new JTextArea();

    SubmitForm(EventDispatcher eventDispatcher) {
        this.eventDispatcher = eventDispatcher;
        setBorder(new EmptyBorder(10, 10, 10, 10));

        var layout = new GroupLayout(this);
        setLayout(layout);

        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        var disconnectButton = new JButton("Disconnect");
        disconnectButton.addActionListener((e) -> eventDispatcher.dispatch(EventType.DISCONNECT));

        var submitButton = new JButton("Send");
        submitButton.addActionListener(this);

        shortMessageTextArea.setLineWrap(true);

        var components = Arrays.asList(
                new Pair(disconnectButton, null),

                // mandatory fields
                new Pair(new JLabel("source_addr"), srcAddrField),
                new Pair(new JLabel("source_addr_ton"), srcAddrTonField),
                new Pair(new JLabel("source_addr_npi"), srcAddrNpiField),
                new Pair(new JLabel("destination_addr"), destAddrField),
                new Pair(new JLabel("dest_addr_ton"), destAddrTonField),
                new Pair(new JLabel("dest_addr_npi"), destAddrNpiField),
                new Pair(new JLabel("short_message"), shortMessageTextArea),

                // optional fields
                new Pair(new JLabel("registered_delivery"), registeredDeliveryField),
                new Pair(new JLabel("service_type"), serviceTypeField),
                new Pair(new JLabel("protocol_id"), protocolIdField),
                new Pair(new JLabel("priority_flag"), priorityFlagField),
                new Pair(new JLabel("sched_deliver_time"), schedDeliveryTimeField),
                new Pair(new JLabel("validity_period"), validityPeriodField),
                new Pair(null, submitButton)
        );

        // Setup horizontal layout
        var horizontalGroup = layout.createSequentialGroup();

        var horizontalLabelGroup = layout.createParallelGroup(Alignment.TRAILING);
        var horizontalFieldsGroup = layout.createParallelGroup(Alignment.LEADING);

        components.forEach(pair -> {
            var component1 = pair.component1;
            if (component1 != null) {
                horizontalLabelGroup.addComponent(component1);
            }
            var component2 = pair.component2;
            if (component2 != null) {
                horizontalFieldsGroup.addComponent(component2);
            }
        });

        horizontalGroup.addGroup(horizontalLabelGroup);
        horizontalGroup.addGroup(horizontalFieldsGroup);
        layout.setHorizontalGroup(horizontalGroup);

        // Setup vertical layout

        var verticalGroup = layout.createSequentialGroup();
        components.forEach(pair -> {
            var component1 = pair.component1;
            var component2 = pair.component2;
            var pairGroup = layout.createParallelGroup(Alignment.BASELINE);
            if (component1 != null) {
                pairGroup.addComponent(component1);
            }
            if (component2 != null) {
                pairGroup.addComponent(component2);
            }
            verticalGroup.addGroup(pairGroup);
        });

        layout.setVerticalGroup(verticalGroup);
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        // TODO validate and sent submitSm

        // source address
        String srcAddr = srcAddrField.getText();
        if (isEmpty(srcAddr)) {
            showError("'source_addr' not provided");
            return;
        }
        byte srcAddrTon;
        try {
            srcAddrTon = Byte.parseByte(srcAddrTonField.getText());
        } catch (Exception e) {
            showError("Invalid 'source_addr_ton' provided");
            return;
        }

        byte srcAddrNpi;
        try {
            srcAddrNpi = Byte.parseByte(srcAddrNpiField.getText());
        } catch (Exception e) {
            showError("Invalid 'source_addr_npi' provided");
            return;
        }

        // destination address
        String destAddr = destAddrField.getText();
        if (isEmpty(srcAddr)) {
            showError("'destination_addr' not provided");
            return;
        }
        byte destAddrTon;
        try {
            destAddrTon = Byte.parseByte(destAddrTonField.getText());
        } catch (Exception e) {
            showError("Invalid 'dest_addr_ton' provided");
            return;
        }

        byte destAddrNpi;
        try {
            destAddrNpi = Byte.parseByte(destAddrNpiField.getText());
        } catch (Exception e) {
            showError("Invalid 'dest_addr_npi' provided");
            return;
        }

        String shortMessage = shortMessageTextArea.getText();
        if (isEmpty(shortMessage)) {
            showError("'short_message' not provided");
            return;
        }

        byte registeredDelivery = 0;
        if (!isEmpty(registeredDeliveryField.getText())) {
            try {
                registeredDelivery = Byte.parseByte(registeredDeliveryField.getText());
            } catch (Exception e) {
                showError("Invalid 'registered_delivery' provided");
                return;
            }
        }

        byte protocolId = 0;
        if (!isEmpty(protocolIdField.getText())) {
            try {
                protocolId = Byte.parseByte(protocolIdField.getText());
            } catch (Exception e) {
                showError("Invalid 'protocol_id' provided");
                return;
            }
        }

        byte priorityFlag = 0;
        if (!isEmpty(priorityFlagField.getText())) {
            try {
                priorityFlag = Byte.parseByte(priorityFlagField.getText());
            } catch (Exception e) {
                showError("Invalid 'priority_flag' provided");
                return;
            }
        }

        String schedDeliverTime = null;
        String schedDeliverTimeText = schedDeliveryTimeField.getText();
        if (!isEmpty(schedDeliverTimeText)) {
            if (!isValidSmppTime(schedDeliverTimeText)) {
                showError("Invalid 'sched_deliver_time' provided");
                return;
            }
            schedDeliverTime = schedDeliverTimeText;
        }

        String validityPeriod = null;
        String validityPeriodText = validityPeriodField.getText();
        if (!isEmpty(validityPeriodText)) {
            if (!isValidSmppTime(validityPeriodText)) {
                showError("Invalid 'validity_period' provided");
                return;
            }
            validityPeriod = validityPeriodText;
        }

        var submitModel = new SubmitModel(
                new Address(srcAddrTon, srcAddrNpi, srcAddr),
                new Address(destAddrTon, destAddrNpi, destAddr),
                shortMessage
        );
        submitModel.setRegisteredDelivery(registeredDelivery);
        submitModel.setProtocolId(protocolId);
        submitModel.setPriorityFlag(priorityFlag);
        submitModel.setServiceType(serviceTypeField.getText());
        submitModel.setSchedDeliverTime(schedDeliverTime);
        submitModel.setValidityPeriod(validityPeriod);

        eventDispatcher.dispatch(EventType.DO_SUBMIT, submitModel);
    }

    private void showError(String fmt, Object... args) {
        eventDispatcher.dispatch(EventType.SHOW_ERROR, String.format(fmt, args));
    }

    private boolean isValidSmppTime(String s) {
        // YYMMDDhhmmsstnnp
        try {
            if (s.length() != TIME_STR_LEN) {
                return false;
            }
            LocalDateTime.parse(s.substring(0, 12), TIME_FORMATTER);
            String tail = s.substring(12);
            return tail.matches("[0-9][0-4][0-9](\\+|\\-|R)");
        } catch (Exception e) {
            return false;
        }
    }

    private static class Pair {

        private final JComponent component1;

        private final JComponent component2;

        Pair(JComponent component1, JComponent component2) {
            this.component1 = component1;
            this.component2 = component2;
        }
    }
}
