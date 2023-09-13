package com.ukarim.smppgui.util;

import com.ukarim.smppgui.protocol.pdu.SubmitSmPdu;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

class CsvPduParserTest {

    @Test
    void simpleParse() {
        String csv = "5.0.Kcell,1.1.7701211xxx2,Hello";
        var pdus = new ArrayList<SubmitSmPdu>();
        try (var stream = CsvPduParser.parseForSubmitSmPdus(csv)) {
            stream.forEach(pdus::add);
        }

        Assertions.assertEquals(1, pdus.size());

        // check pdu's content
        SubmitSmPdu pdu = pdus.get(0);
        Assertions.assertEquals(5, pdu.getSrcAddress().getTon());
        Assertions.assertEquals(0, pdu.getSrcAddress().getNpi());
        Assertions.assertEquals("Kcell", pdu.getSrcAddress().getAddr());
        Assertions.assertEquals(1, pdu.getDestAddress().getTon());
        Assertions.assertEquals(1, pdu.getDestAddress().getNpi());
        Assertions.assertEquals("7701211xxx2", pdu.getDestAddress().getAddr());
        Assertions.assertEquals("Hello", new String(pdu.getShortMessage(), StandardCharsets.UTF_16BE));
    }

    @Test
    void parseQuotedMessage1() {
        String csv = "5.0.Kcell,1.1.7701211xxx1,\"Hello, how are you?\"";

        var pdus = new ArrayList<SubmitSmPdu>();
        try (var stream = CsvPduParser.parseForSubmitSmPdus(csv)) {
            stream.forEach(pdus::add);
        }

        Assertions.assertEquals(1, pdus.size());

        // check pdu's content
        SubmitSmPdu pdu = pdus.get(0);
        Assertions.assertEquals(5, pdu.getSrcAddress().getTon());
        Assertions.assertEquals(0, pdu.getSrcAddress().getNpi());
        Assertions.assertEquals("Kcell", pdu.getSrcAddress().getAddr());
        Assertions.assertEquals(1, pdu.getDestAddress().getTon());
        Assertions.assertEquals(1, pdu.getDestAddress().getNpi());
        Assertions.assertEquals("7701211xxx1", pdu.getDestAddress().getAddr());
        Assertions.assertEquals("Hello, how are you?", new String(pdu.getShortMessage(), StandardCharsets.UTF_16BE));
    }

    @Test
    void parseQuotedMessage2() {
        String csv = "5.0.Kcell,1.1.7701211xxx1,'Hello, \"Almaty\"!'";

        var pdus = new ArrayList<SubmitSmPdu>();
        try (var stream = CsvPduParser.parseForSubmitSmPdus(csv)) {
            stream.forEach(pdus::add);
        }

        Assertions.assertEquals(1, pdus.size());

        // check pdu's content
        SubmitSmPdu pdu = pdus.get(0);
        Assertions.assertEquals(5, pdu.getSrcAddress().getTon());
        Assertions.assertEquals(0, pdu.getSrcAddress().getNpi());
        Assertions.assertEquals("Kcell", pdu.getSrcAddress().getAddr());
        Assertions.assertEquals(1, pdu.getDestAddress().getTon());
        Assertions.assertEquals(1, pdu.getDestAddress().getNpi());
        Assertions.assertEquals("7701211xxx1", pdu.getDestAddress().getAddr());
        Assertions.assertEquals("Hello, \"Almaty\"!", new String(pdu.getShortMessage(), StandardCharsets.UTF_16BE));
    }
}