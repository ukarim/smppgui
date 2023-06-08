package com.ukarim.smppgui.gui;

import java.nio.charset.Charset;

public class LoginModel {

    private final String host;

    private final int port;

    private final String systemId;

    private final char[] password;

    private final SessionType sessionType;

    private final String systemType;

    private final Charset defaultCharset;

    LoginModel(String host, int port, String systemId, char[] password, SessionType sessionType, String systemType,
               Charset defaultCharset) {
        this.host = host;
        this.port = port;
        this.systemId = systemId;
        this.password = password;
        this.sessionType = sessionType;
        this.systemType = systemType;
        this.defaultCharset = defaultCharset;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getSystemId() {
        return systemId;
    }

    public char[] getPassword() {
        return password;
    }

    public SessionType getSessionType() {
        return sessionType;
    }

    public String getSystemType() {
        return systemType;
    }

    public Charset getDefaultCharset() {
        return defaultCharset;
    }

    public enum SessionType {
        TRANSMITTER,
        TRANSCEIVER,
        RECEIVER,
    }
}
