package com.ukarim.smppgui.gui;

public class LoginModel {

    private final String host;

    private final int port;

    private final String systemId;

    private final char[] password;

    private final SessionType sessionType;

    private final String systemType;

    LoginModel(String host, int port, String systemId, char[] password, SessionType sessionType, String systemType) {
        this.host = host;
        this.port = port;
        this.systemId = systemId;
        this.password = password;
        this.sessionType = sessionType;
        this.systemType = systemType;
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

    public enum SessionType {
        TRANSMITTER,
        TRANSCEIVER,
        RECEIVER,
    }
}
