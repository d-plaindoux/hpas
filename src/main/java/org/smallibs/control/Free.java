/*
 * HPAS
 * https://github.com/d-plaindoux/hpas
 *
 * Copyright (c) 2016 Didier Plaindoux
 * Licensed under the LGPL2 license.
 */

package org.smallibs.control;

import org.smallibs.type.TApp;

/**
 * Free Monad
 *
 * @param <M>
 * @param <A>
 */
public abstract class Free<M, A> {

    private Free() {
    }

    public static <M, A> Free<M, A> pure(A a) {
        return new Pure<>(a);
    }

    public static <M, A, Self extends TApp<M, Free<M, A>, Self>> Free<M, A> impure(TApp<M, Free<M, A>, Self> s) {
        return new Impure<>(s);
    }

    private final static class Pure<M, A> extends Free<M, A> {
        private final A a;

        private Pure(A a) {
            this.a = a;
        }
    }

    private final static class Impure<M, A, Self extends TApp<M, Free<M, A>, Self>> extends Free<M, A> {
        private final TApp<M, Free<M, A>, Self> s;

        private Impure(TApp<M, Free<M, A>, Self> s) {
            this.s = s;
        }
    }
}
