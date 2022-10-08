package com.ukarim.smppgui.protocol;

import com.ukarim.smppgui.protocol.pdu.Pdu;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.List;

class PduParser {

    private PduParser() {}

    public static List<Pdu> parsePdu(ByteBuffer buffer) throws SmppException {
        return Collections.emptyList();
    }
}
