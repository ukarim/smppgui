package com.ukarim.smppgui.util;

public final class Tuple2<F, S> {

    private final F first;
    private final S second;

    private Tuple2(F first, S second) {
        this.first = first;
        this.second = second;
    }

    public static <F, S> Tuple2<F, S> of(F first, S second) {
        return new Tuple2<>(first, second);
    }

    public F getFirst() {
        return first;
    }

    public S getSecond() {
        return second;
    }
}
