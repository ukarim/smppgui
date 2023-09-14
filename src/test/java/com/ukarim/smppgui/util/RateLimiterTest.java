package com.ukarim.smppgui.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

class RateLimiterTest {

    @Test
    void checkRateLimiter() throws Exception {
        final int maxAllowed = 7; // control value
        final long windowSec = 60;
        AtomicInteger counter = new AtomicInteger(0);
        final var rateLimiter = new RateLimiter(maxAllowed, Duration.ofSeconds(windowSec));
        Thread worker = new Thread(() -> {
            for (int i = 0; i < 1000; i++) {
                rateLimiter.maybeBlock();
                counter.incrementAndGet();
            }
        });
        worker.start();
        Thread.sleep((windowSec/20) * 1000); // give time for worker thread to start and make first actions
        worker.interrupt(); // interrupt worker thread

        // check that only allowed number of slots was received
        Assertions.assertEquals(maxAllowed, counter.intValue());
    }
}