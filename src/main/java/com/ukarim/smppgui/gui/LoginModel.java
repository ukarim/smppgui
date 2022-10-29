package com.ukarim.smppgui.gui;

class LoginModel {

    private final String host;

    private final int port;

    private final String systemId;

    private final char[] password;

    private final String systemType;

    LoginModel(String host, int port, String systemId, char[] password, String systemType) {
        this.host = host;
        this.port = port;
        this.systemId = systemId;
        this.password = password;
        this.systemType = systemType;
    }

    String getHost() {
        return host;
    }

    int getPort() {
        return port;
    }

    String getSystemId() {
        return systemId;
    }

    char[] getPassword() {
        return password;
    }

    String getSystemType() {
        return systemType;
    }
}
