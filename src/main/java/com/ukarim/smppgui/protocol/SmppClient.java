package com.ukarim.smppgui.protocol;

public final class SmppClient {

    private final SmppHandler handler;

    public SmppClient(SmppHandler handler) {
        this.handler = handler;
    }

    public void connect(String host, int port, String systemId, char[] password) {

    }

    public void disconnect() {

    }
}
