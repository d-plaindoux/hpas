/*
 * HPAS
 * https://github.com/d-plaindoux/hpas
 *
 * Copyright (c) 2016 Didier Plaindoux
 * Licensed under the LGPL2 license.
 */

package org.smallibs.control;

import org.smallibs.type.Kind;

/**
 * Free *
 *
 * @param <M>
 * @param <A>
 */
public interface Free<M, A> {

    static <M, A> Free<M, A> pure(A a) {
        return new Pure<>(a);
    }

    static <M, A, Self extends Kind<M, Free<M, A>, Self>> Free<M, A> impure(Kind<M, Free<M, A>, Self> s) {
        return new Impure<>(s);
    }

    class Pure<M, A> implements Free<M, A> {
        private final A a;

        private Pure(A a) {
            this.a = a;
        }
    }

    class Impure<M, A, Self extends Kind<M, Free<M, A>, Self>> implements Free<M, A> {
        private final Kind<M, Free<M, A>, Self> s;

        private Impure(Kind<M, Free<M, A>, Self> s) {
            this.s = s;
        }
    }
}
