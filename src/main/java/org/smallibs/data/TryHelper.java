/*
 * HPAS
 * https://github.com/d-plaindoux/hpas
 *
 * Copyright (c) 2016-2025 Didier Plaindoux
 * Licensed under the LGPL2 license.
 */

package org.smallibs.data;

import org.smallibs.control.Functor;
import org.smallibs.control.Monad;
import org.smallibs.type.HK;

import java.util.function.Function;

@SuppressWarnings("rawtypes")
public enum TryHelper {
    ;

    public static <T> Monad<Try, T, Try<T>> monad(Try<T> aTry) {
        return new Monadic<>(aTry);
    }

    public static <T> Maybe<T> toMaybe(Try<T> aTry) {
        return aTry.map(Maybe::some).recoverWith(Maybe.none());
    }

    @SuppressWarnings("unchecked")
    private static <B, S extends HK<Try, B, S>> HK<Try, B, Try<B>> specialize(HK<Try, B, S> app) {
        return (HK<Try, B, Try<B>>) app;
    }

    @SuppressWarnings("unchecked")
    private static <B, S extends HK<Try, B, S>> HK<Try, B, S> generalize(HK<Try, B, Try<B>> app) {
        return (HK<Try, B, S>) app;
    }

    /**
     * @param <T>
     */
    record Monadic<T>(Try<T> aTry) implements Monad<Try, T, Try<T>> {
        @Override
        public <B, NS extends HK<Try, B, NS>> HK<Try, B, NS> map(Function<? super T, ? extends B> function) {
            return generalize(new Monadic<>(aTry.map(function)));
        }

        @Override
        public <B, NS extends HK<Try, B, NS>> HK<Try, B, NS> flatmap(Function<? super T, HK<Try, B, NS>> function) {
            final Function<T, Try<B>> tTryFunction = t -> {
                final HK<Try, B, NS> apply = function.apply(t);
                return specialize(apply).self();
            };

            return generalize(new Monadic<>(aTry.flatmap(tTryFunction)));
        }

        @Override
        public <B, NS extends HK<Try, B, NS>> HK<Try, B, NS> apply(Functor<Try, Function<? super T, ? extends B>, ?> functor) {
            return generalize(new Monadic<>(aTry.flatmap(a -> {
                final HK<Try, B, NS> map = functor.map(bFunction -> bFunction.apply(a));
                return specialize(map).self();
            })));
        }

        @Override
        public Try<T> self() {
            return aTry;
        }
    }
}
