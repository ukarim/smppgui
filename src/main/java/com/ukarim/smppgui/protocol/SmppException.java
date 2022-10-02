package com.ukarim.smppgui.protocol;

public class SmppException extends Exception {

    public SmppException(String message) {
        super(message);
    }

    public SmppException(String fmt, Object... args) {
        super(String.format(fmt, args));
    }

    public SmppException(Throwable cause, String message) {
        super(message, cause);
    }

    public SmppException(Throwable cause, String fmt, Object... args) {
        super(String.format(fmt, args), cause);
    }
}
