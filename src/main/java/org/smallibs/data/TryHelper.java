/*
 * HPAS
 * https://github.com/d-plaindoux/hpas
 *
 * Copyright (c) 2016 Didier Plaindoux
 * Licensed under the LGPL2 license.
 */

package org.smallibs.data;

import org.smallibs.control.Functor;
import org.smallibs.control.Monad;
import org.smallibs.type.HoType;

import java.util.function.Function;

public enum  TryHelper {
    ;

    public static <T> Monad<Try, T, Try<T>> monad(Try<T> aTry) {
        return new TryHelper.Monadic<>(aTry);
    }

    public static <T> Maybe<T> toMaybe(Try<T> aTry) {
        return aTry.map(Maybe::some).recoverWith(Maybe.none());
    }

    @SuppressWarnings("unchecked")
    private static <B, Self extends HoType<Try, B, Self>> HoType<Try, B, Try<B>> specialize(HoType<Try, B, Self> app) {
        return (HoType<Try, B, Try<B>>) app;
    }

    @SuppressWarnings("unchecked")
    private static <B, Self extends HoType<Try, B, Self>> HoType<Try, B, Self> generalize(HoType<Try, B, Try<B>> app) {
        return (HoType<Try, B, Self>) app;
    }

    /**
     * @param <T>
     */
    final static class Monadic<T> implements Monad<Try, T, Try<T>> {
        private final Try<T> aTry;

        private Monadic(Try<T> aTry) {
            this.aTry = aTry;
        }

        @Override
        public <B, NSelf extends HoType<Try, B, NSelf>> HoType<Try, B, NSelf> map(Function<? super T, B> function) {
            return generalize(new Monadic<>(aTry.map(function)));
        }

        @Override
        public <B, NSelf extends HoType<Try, B, NSelf>> HoType<Try, B, NSelf> flatmap(Function<? super T, HoType<Try, B, NSelf>> function) {
            final Function<T, Try<B>> tTryFunction = t -> {
                final HoType<Try, B, NSelf> apply = function.apply(t);
                return specialize(apply).self();
            };

            return generalize(new Monadic<>(aTry.flatmap(tTryFunction)));
        }

        @Override
        public <T1> T1 accept(Function<HoType<Try, T, Try<T>>, T1> f) {
            return aTry.accept(f);
        }

        @Override
        public <B, NSelf extends HoType<Try, B, NSelf>> HoType<Try, B, NSelf> apply(Functor<Try, Function<? super T, ? extends B>, ?> functor) {
            return generalize(new Monadic<>(aTry.flatmap(a -> {
                final HoType<Try, B, NSelf> map = functor.map(bFunction -> bFunction.apply(a));
                return specialize(map).self();
            })));
        }

        @Override
        public Try<T> self() {
            return aTry;
        }
    }
}
