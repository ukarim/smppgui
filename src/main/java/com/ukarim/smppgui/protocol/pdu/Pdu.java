package com.ukarim.smppgui.protocol.pdu;

import com.ukarim.smppgui.protocol.SmppCmd;
import com.ukarim.smppgui.protocol.SmppStatus;
import java.nio.ByteBuffer;

public interface Pdu {

    SmppCmd getCmd();

    SmppStatus getSts();

    int getSeqNum();

    ByteBuffer toByteBuffer();
}
