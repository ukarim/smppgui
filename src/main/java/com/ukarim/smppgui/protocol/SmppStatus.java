package com.ukarim.smppgui.protocol;

import com.ukarim.smppgui.util.FmtUtils;

import java.util.HashMap;
import java.util.Map;

public interface SmppStatus {

    SmppStatus ESME_ROK = new SmppStatusImpl(0x00000000);
    SmppStatus ESME_RINVCMDID = new SmppStatusImpl(0x00000003);
    SmppStatus ESME_RSUBMITFAIL = new SmppStatusImpl(0x00000045);

    int getStatusId();

    String getStatusDesc();

    static SmppStatus fromStatusId(int statusId) {
        return new SmppStatusImpl(statusId);
    }
}

class SmppStatusImpl implements SmppStatus {

    private final int statusId;
    private final String statusDesc;

    private static final Map<Integer, String> KNOWN_STATUSES = new HashMap<>();

    static {
        KNOWN_STATUSES.put(0x00000000, "No Error");
        KNOWN_STATUSES.put(0x00000001, "Message Length is invalid");
        KNOWN_STATUSES.put(0x00000002, "Command Length is invalid");
        KNOWN_STATUSES.put(0x00000003, "Invalid Command ID");
        KNOWN_STATUSES.put(0x00000004, "Incorrect BIND Status for given command");
        KNOWN_STATUSES.put(0x00000005, "ESME Already in Bound State");
        KNOWN_STATUSES.put(0x00000006, "Invalid Priority Flag");
        KNOWN_STATUSES.put(0x00000007, "Invalid Registered Delivery Flag");
        KNOWN_STATUSES.put(0x00000008, "System Error");
        KNOWN_STATUSES.put(0x0000000A, "Invalid Source Address");
        KNOWN_STATUSES.put(0x0000000B, "Invalid Dest Addr");
        KNOWN_STATUSES.put(0x0000000C, "Message ID is invalid");
        KNOWN_STATUSES.put(0x0000000D, "Bind Failed");
        KNOWN_STATUSES.put(0x0000000E, "Invalid Password");
        KNOWN_STATUSES.put(0x0000000F, "Invalid System ID");
        KNOWN_STATUSES.put(0x00000011, "Cancel SM Failed");
        KNOWN_STATUSES.put(0x00000013, "Replace SM Failed");
        KNOWN_STATUSES.put(0x00000014, "Message Queue Full");
        KNOWN_STATUSES.put(0x00000015, "Invalid Service Type");
        KNOWN_STATUSES.put(0x00000033, "Invalid number of destinations");
        KNOWN_STATUSES.put(0x00000034, "Invalid Distribution List name");
        KNOWN_STATUSES.put(0x00000040, "Destination flag (submit_multi)");
        KNOWN_STATUSES.put(0x00000042, "Invalid ‘submit with replace’ request (i.e. submit_sm with replace_if_present_flag set)");
        KNOWN_STATUSES.put(0x00000043, "Invalid esm_class field data");
        KNOWN_STATUSES.put(0x00000044, "Cannot Submit to Distribution List");
        KNOWN_STATUSES.put(0x00000045, "submit_sm or submit_multi failed");
        KNOWN_STATUSES.put(0x00000048, "Invalid Source address TON");
        KNOWN_STATUSES.put(0x00000049, "Invalid Source address NPI");
        KNOWN_STATUSES.put(0x00000050, "Invalid Destination address TON");
        KNOWN_STATUSES.put(0x00000051, "Invalid Destination address NPI");
        KNOWN_STATUSES.put(0x00000053, "Invalid system_type field");
        KNOWN_STATUSES.put(0x00000054, "Invalid replace_if_present flag");
        KNOWN_STATUSES.put(0x00000055, "Invalid number of messages");
        KNOWN_STATUSES.put(0x00000058, "Throttling error (ESME has exceeded allowed message limits)");
        KNOWN_STATUSES.put(0x00000061, "Invalid Scheduled Delivery Time");
        KNOWN_STATUSES.put(0x00000062, "Invalid message (Expiry time)");
        KNOWN_STATUSES.put(0x00000063, "Predefined Message Invalid or Not Found");
        KNOWN_STATUSES.put(0x00000064, "ESME Receiver Temporary App Error Code");
        KNOWN_STATUSES.put(0x00000065, "ESME Receiver Permanent App Error Code");
        KNOWN_STATUSES.put(0x00000066, "ESME Receiver Reject Message Error Code");
        KNOWN_STATUSES.put(0x00000067, "query_sm request failed");
        KNOWN_STATUSES.put(0x000000C0, "Error in the optional part of the PDU Body");
        KNOWN_STATUSES.put(0x000000C1, "Optional Parameter not allowed");
        KNOWN_STATUSES.put(0x000000C2, "Invalid Parameter Length");
        KNOWN_STATUSES.put(0x000000C3, "Expected Optional Parameter missing");
        KNOWN_STATUSES.put(0x000000C4, "Invalid Optional Parameter Value");
        KNOWN_STATUSES.put(0x000000FE, "Delivery Failure (used period for data_sm_resp)");
        KNOWN_STATUSES.put(0x000000FF, "Unknown Error");
    }

    SmppStatusImpl(int statusId) {
        this.statusId = statusId;
        String desc = KNOWN_STATUSES.get(statusId);
        if (desc != null) {
            this.statusDesc = desc;
        } else {
            this.statusDesc = String.format("Unknown status [%s]", FmtUtils.fmtInt(statusId));
        }
    }

    @Override
    public int getStatusId() {
        return statusId;
    }

    @Override
    public String getStatusDesc() {
        return statusDesc;
    }

    @Override
    public String toString() {
        return statusDesc;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof SmppStatus) {
            return this.statusId == ((SmppStatus) obj).getStatusId();
        }
        return false;
    }
}
