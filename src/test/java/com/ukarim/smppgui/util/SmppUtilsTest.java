package com.ukarim.smppgui.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class SmppUtilsTest {

    @Test
    void testValidSmppTime() {
        String[] s = new String[] {
                "000000001000000R", // after 10min
                "221105141623024+", // 14:16 UTC+6
                "221105141623024-"  // 14:16 UTC-6
        };

        for (String time : s) {
            Assertions.assertTrue(SmppUtils.isValidSmppTime(time));
        }
    }
}