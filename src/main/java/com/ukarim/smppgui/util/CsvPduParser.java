package com.ukarim.smppgui.util;

import com.ukarim.smppgui.protocol.SmppConstants;
import com.ukarim.smppgui.protocol.pdu.Address;
import com.ukarim.smppgui.protocol.pdu.SubmitSmPdu;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.stream.Stream;

public final class CsvPduParser {

    public static Stream<SubmitSmPdu> parseForSubmitSmPdus(Path path) throws IOException {
        return parseForSubmitSmPdus(Files.lines(path));
    }

    public static Stream<SubmitSmPdu> parseForSubmitSmPdus(String csv) {
        return parseForSubmitSmPdus(csv.lines());
    }

    private static Stream<SubmitSmPdu> parseForSubmitSmPdus(Stream<String> lines) {
        return lines
                .map(String::trim)
                .filter(l -> !l.isBlank()) // skip blank lines
                .filter(l -> !l.startsWith("#")) // ignore comment lines
                .map(CsvPduParser::parseSubmitSmPdu);
    }

    private static SubmitSmPdu parseSubmitSmPdu(String line) {
        final int length = line.length();
        final var buf = new StringBuilder();
        final var values = new ArrayList<String>();
        boolean insideQuote = false;
        char quote = 0;
        for (int i = 0; i < length; i++) {
            char ch = line.charAt(i);
            if (ch == ',') {
                if (insideQuote) {
                    buf.append(ch);
                } else {
                    // value was read completely
                    values.add(buf.toString());
                    buf.setLength(0); // clean buffer
                }
            } else if (ch == '"' || ch == '\'') {
                if (insideQuote) {
                    if (ch == quote) {
                        insideQuote = false;
                    } else {
                        buf.append(ch);
                    }
                } else {
                    insideQuote = true;
                    quote = ch;
                }
            } else {
                buf.append(ch);
            }
        }

        values.add(buf.toString()); // drain buffer

        Address src = parseAddress(values.get(0));
        Address dest = parseAddress(values.get(1));
        String shortMessage = values.get(2);
        return new SubmitSmPdu(
                0,
                src,
                dest,
                shortMessage.getBytes(StandardCharsets.UTF_16BE),
                SmppConstants.DATA_CODING_UCS2
        );
    }

    private static Address parseAddress(String str) {
        byte ton = 0;
        byte npi = 0;
        int mark = 0;
        int dotOccurrence = 0;
        for (int i = 0; i < str.length(); i++) {
            char ch = str.charAt(i);
            if (ch == '.') {
                if (dotOccurrence == 0) {
                    // parse ton
                    ton = Byte.parseByte(str.substring(mark, i));
                    mark = i + 1;
                    dotOccurrence++;
                } else if (dotOccurrence == 1) {
                    // parse npi
                    npi = Byte.parseByte(str.substring(mark, i));
                    mark = i + 1;
                    break;
                }
            }
        }
        return new Address(ton, npi, str.substring(mark));
    }
}
