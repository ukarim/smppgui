package com.ukarim.smppgui.protocol;

import com.ukarim.smppgui.util.FmtUtils;
import java.util.HashMap;
import java.util.Map;

public enum SmppStatus {

    ESME_ROK(0x00000000, "No Error"),
    ESME_RINVMSGLEN(0x00000001, "Message Length is invalid"),
    ESME_RINVCMDLEN(0x00000002, "Command Length is invalid"),
    ESME_RINVCMDID(0x00000003, "Invalid Command ID"),
    ESME_RINVBNDSTS(0x00000004, "Incorrect BIND Status for given command"),
    ESME_RALYBND(0x00000005, "ESME Already in Bound State"),
    ESME_RINVPRTFLG(0x00000006, "Invalid Priority Flag"),
    ESME_RINVREGDLVFLG(0x00000007, "Invalid Registered Delivery Flag"),
    ESME_RSYSERR(0x00000008, "System Error"),
    ESME_RINVSRCADR(0x0000000A, "Invalid Source Address"),
    ESME_RINVDSTADR(0x0000000B, "Invalid Dest Addr"),
    ESME_RINVMSGID(0x0000000C, "Message ID is invalid"),
    ESME_RBINDFAIL(0x0000000D, "Bind Failed"),
    ESME_RINVPASWD(0x0000000E, "Invalid Password"),
    ESME_RINVSYSID(0x0000000F, "Invalid System ID"),
    ESME_RCANCELFAIL(0x00000011, "Cancel SM Failed"),
    ESME_RREPLACEFAIL(0x00000013, "Replace SM Failed"),
    ESME_RMSGQFUL(0x00000014, "Message Queue Full"),
    ESME_RINVSERTYP(0x00000015, "Invalid Service Type"),
    ESME_RINVNUMDESTS(0x00000033, "Invalid number of destinations"),
    ESME_RINVDLNAME(0x00000034, "Invalid Distribution List name"),
    ESME_RINVDESTFLAG(0x00000040, "Destination flag (submit_multi)"),
    ESME_RINVSUBREP(0x00000042, "Invalid ‘submit with replace’ request (i.e. submit_sm with replace_if_present_flag set)"),
    ESME_RINVESMCLASS(0x00000043, "Invalid esm_class field data"),
    ESME_RCNTSUBDL(0x00000044, "Cannot Submit to Distribution List"),
    ESME_RSUBMITFAIL(0x00000045, "submit_sm or submit_multi failed"),
    ESME_RINVSRCTON(0x00000048, "Invalid Source address TON"),
    ESME_RINVSRCNPI(0x00000049, "Invalid Source address NPI"),
    ESME_RINVDSTTON(0x00000050, "Invalid Destination address TON"),
    ESME_RINVDSTNPI(0x00000051, "Invalid Destination address NPI"),
    ESME_RINVSYSTYP(0x00000053, "Invalid system_type field"),
    ESME_RINVREPFLAG(0x00000054, "Invalid replace_if_present flag"),
    ESME_RINVNUMMSGS(0x00000055, "Invalid number of messages"),
    ESME_RTHROTTLED(0x00000058, "Throttling error (ESME has exceeded allowed message limits)"),
    ESME_RINVSCHED(0x00000061, "Invalid Scheduled Delivery Time"),
    ESME_RINVEXPIRY(0x00000062, "Invalid message (Expiry time)"),
    ESME_RINVDFTMSGID(0x00000063, "Predefined Message Invalid or Not Found"),
    ESME_RX_T_APPN(0x00000064, "ESME Receiver Temporary App Error Code"),
    ESME_RX_P_APPN(0x00000065, "ESME Receiver Permanent App Error Code"),
    ESME_RX_R_APPN(0x00000066, "ESME Receiver Reject Message Error Code"),
    ESME_RQUERYFAIL(0x00000067, "query_sm request failed"),
    ESME_RINVOPTPARSTREAM(0x000000C0, "Error in the optional part of the PDU Body"),
    ESME_ROPTPARNOTALLWD(0x000000C1, "Optional Parameter not allowed"),
    ESME_RINVPARLEN(0x000000C2, "Invalid Parameter Length"),
    ESME_RMISSINGOPTPARAM(0x000000C3, "Expected Optional Parameter missing"),
    ESME_RINVOPTPARAMVAL(0x000000C4, "Invalid Optional Parameter Value"),
    ESME_RDELIVERYFAILURE(0x000000FE, "Delivery Failure (used period for data_sm_resp)"),
    ESME_RUNKNOWNERR(0x000000FF, "Unknown Error");

    private final int statusId;

    private final String statusDesc;

    SmppStatus(int statusId, String statusDesc) {
        this.statusId = statusId;
        this.statusDesc = statusDesc;
    }

    public int getStatusId() {
        return statusId;
    }

    public String getStatusDesc() {
        return statusDesc;
    }

    private final static Map<Integer, SmppStatus> STATUS_MAP = new HashMap<>();

    static {
        for (var value : SmppStatus.values()) {
            STATUS_MAP.put(value.statusId, value);
        }
    }

    static SmppStatus fromStatusId(int statusId) throws SmppException {
        var smppStatus = STATUS_MAP.get(statusId);
        if (smppStatus == null) {
            throw new SmppException("Unknown smpp status with id %s", FmtUtils.fmtInt(statusId));
        }
        return smppStatus;
    }
}
