package com.ukarim.smppgui.protocol;

import com.ukarim.smppgui.util.FmtUtils;
import java.util.HashMap;
import java.util.Map;

public enum SmppCmd {

    GENERIC_NACK(0X80000000),
    BIND_RECEIVER(0X00000001),
    BIND_RECEIVER_RESP(0X80000001),
    BIND_TRANSMITTER(0X00000002),
    BIND_TRANSMITTER_RESP(0X80000002),
    QUERY_SM(0X00000003),
    QUERY_SM_RESP(0X80000003),
    SUBMIT_SM(0X00000004),
    SUBMIT_SM_RESP(0X80000004),
    DELIVER_SM(0X00000005),
    DELIVER_SM_RESP(0X80000005),
    UNBIND(0X00000006),
    UNBIND_RESP(0X80000006),
    REPLACE_SM(0X00000007),
    REPLACE_SM_RESP(0X80000007),
    CANCEL_SM(0X00000008),
    CANCEL_SM_RESP(0X80000008),
    BIND_TRANSCEIVER(0X00000009),
    BIND_TRANSCEIVER_RESP(0X80000009),
    OUTBIND(0X0000000B),
    ENQUIRE_LINK(0X00000015),
    ENQUIRE_LINK_RESP(0X80000015),
    SUBMIT_MULTI(0X00000021),
    SUBMIT_MULTI_RESP(0X80000021),
    ALERT_NOTIFICATION(0X00000102),
    DATA_SM(0X00000103),
    DATA_SM_RESP(0X80000103);

    private final int cmdId;

    SmppCmd(int cmdId) {
        this.cmdId = cmdId;
    }

    public int getCmdId() {
        return cmdId;
    }

    private static final Map<Integer, SmppCmd> CMD_MAP = new HashMap<>();

    static {
        for (var value : SmppCmd.values()) {
            CMD_MAP.put(value.cmdId, value);
        }
    }

    static SmppCmd fromCmdId(int cmdId) throws SmppException {
        var smppCmd = CMD_MAP.get(cmdId);
        if (smppCmd == null) {
            throw new SmppException("Unknown smpp command with id %s", FmtUtils.fmtInt(cmdId));
        }
        return smppCmd;
    }
}
