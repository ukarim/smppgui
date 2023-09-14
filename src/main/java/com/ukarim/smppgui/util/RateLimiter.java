package com.ukarim.smppgui.util;

import java.time.Duration;
import java.util.LinkedList;
import java.util.Queue;

public final class RateLimiter {

    private final Object lock = new Object();

    private final int allowedNum;

    private final long windowMs;

    private final Queue<Long> bucket = new LinkedList<>();

    public RateLimiter(int allowedNum, Duration window) {
        if (allowedNum < 1) {
            throw new IllegalArgumentException("allowedNum cannot be less than 1");
        }
        this.allowedNum = allowedNum;
        this.windowMs = window.getSeconds() * 1000;
    }

    public void maybeBlock() {
        long sleepMs = 0;
        synchronized (lock) {
            final long now = System.currentTimeMillis();

            // remove expired elements
            Long t = bucket.peek();
            while (t != null) {
                if (t < now) {
                    bucket.poll(); // remove expired element
                    t = bucket.peek();
                } else {
                    // optimization
                    break;
                }
            }
            if (bucket.isEmpty() || bucket.size() < allowedNum) {
                bucket.add(now + windowMs);
                return;
            }
            sleepMs = bucket.peek() - now;
        }
        if (sleepMs > 0) {
            try {
                Thread.sleep(sleepMs);
            } catch (InterruptedException e) {
                throw new RuntimeException("rate limited thread was interrupted", e);
            }
        }
    }
}
