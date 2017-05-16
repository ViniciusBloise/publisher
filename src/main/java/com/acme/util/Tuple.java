package com.acme.util;

//Changed Pair into Tuple in order to use the index
public class Tuple<F,S,I> {

    final Object f;
    final Object s;
    final Object i;

    public Tuple(final F f, final S s, final I i) {
        this.f = f;
        this.s = s;
        this.i = i;
    }

    public F first() { return (F) f; }
    public S second() { return (S) s; }
    public I third() { return (I) i; }
}
