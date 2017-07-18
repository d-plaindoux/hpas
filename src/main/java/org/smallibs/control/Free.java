/*
 * HPAS
 * https://github.com/d-plaindoux/hpas
 *
 * Copyright (c) 2016-2017 Didier Plaindoux
 * Licensed under the LGPL2 license.
 */

package org.smallibs.control;

public interface Free<M extends Functor<MT, A, M>, A, MT>  {
/*
    static <M extends Functor<MT, A, M>, A, MT> Free<M, A, MT> pure(A a) {
        return new Pure<>(a);
    }

    static <M, A, Self extends HK<M, Free<M, A, Self>, Self>> Free<M, A, Self> impure(HK<M, Free<NSelf, A>, NSelf> s) {
        return new Impure<>(s);
    }

    <B, NM extends Functor<MT, B, NM>> Free<NM, B, MT> map(Functor<M, A, ? extends B> function);

    // <B, NMT extends Functor<M, B, NMT>> Free<M, B, NMT> map(Function<? super A, ? extends B> function);

    class Pure<M extends Functor<MT, A, M>, A, MT> implements Free<M, A, MT> {
        private final A a;

        private Pure(A a) {
            this.a = a;
        }

        @Override
        public <B, NM extends Functor<MT, B, NM>> Free<NM, B, MT> map(Function<? super A, ? extends B> function) {
            return new Pure<>(function.apply(a));
        }
    }

    class Impure<M extends Functor<MT, A, M>, A, MT> implements Free<M, A, MT> {
        private final HK<M,  s;

        private Impure(HK<M, Free<Self, A>, Self> s) {
            this.s = s;
        }


        @Override
        public <B, NM extends Functor<MT, B, NM>> Free<NM, B, MT> map(Functor<? super A, ? extends B> function) {
            return S
        }
    }
 */
}
