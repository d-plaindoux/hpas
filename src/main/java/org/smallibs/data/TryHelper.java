/*
 * HPAS
 * https://github.com/d-plaindoux/hpas
 *
 * Copyright (c) 2016 Didier Plaindoux
 * Licensed under the LGPL2 license.
 */

package org.smallibs.data;

import org.smallibs.control.Monad;
import org.smallibs.type.TApp;

import java.util.function.Function;

public class TryHelper {

    public static <B, Self extends TApp<Try, B, Self>> TApp<Try, B, Try<B>> specialize(TApp<Try, B, Self> app) {
        //noinspection unchecked
        return (TApp<Try, B, Try<B>>) app;
    }

    public static <B, Self extends TApp<Try, B, Self>> TApp<Try, B, Self> generalize(TApp<Try, B, Try<B>> app) {
        //noinspection unchecked
        return (TApp<Try, B, Self>) app;
    }

    public static <T> Monad<Try, T, Try<T>> monad(Try<T> aTry) {
        return new TryHelper.Monadic<>(aTry);
    }

    public static <T> Maybe<T> toMaybe(Try<T> aTry) {
        final TApp<Try, Maybe<T>, ? extends Try<Maybe<T>>> map = aTry.map(Maybe::some);
        return map.self().recoverWith(Maybe.none());
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
        public <B, NSelf extends TApp<Try, B, NSelf>> TApp<Try, B, NSelf> map(Function<? super T, B> function) {
            return generalize(new Monadic<>(aTry.map(function)));
        }

        @Override
        public <B, NSelf extends TApp<Try, B, NSelf>> TApp<Try, B, NSelf> flatmap(Function<? super T, TApp<Try, B, NSelf>> function) {
            final Function<T, Try<B>> tTryFunction = t -> {
                final TApp<Try, B, NSelf> apply = function.apply(t);
                return specialize(apply).self();
            };

            return generalize(new Monadic<>(aTry.flatmap(tTryFunction)));
        }

        @Override
        public <T1> T1 accept(Function<TApp<Try, T, Try<T>>, T1> f) {
            return aTry.accept(f);
        }

        @Override
        public Try<T> self() {
            return aTry;
        }
    }
}
