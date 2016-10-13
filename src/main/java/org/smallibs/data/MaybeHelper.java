/*
 * HPAS
 * https://github.com/d-plaindoux/hpas
 *
 * Copyright (c) 2016 Didier Plaindoux
 * Licensed under the LGPL2 license.
 */

package org.smallibs.data;

import org.smallibs.control.Monad;
import org.smallibs.exception.NoValueException;
import org.smallibs.type.TApp;

import java.util.function.Function;

public class MaybeHelper {

    private MaybeHelper() {
    }

    public static <T> Monad<Maybe, T, Maybe<T>> monad(Maybe<T> maybe) {
        return new MaybeHelper.Monadic<T>(maybe);
    }

    public static <T> Try<T> toTry(Maybe<T> maybe) {
        return maybe.map(Try::success).orElse(Try.failure(new NoValueException()));
    }

    @SuppressWarnings("unchecked")
    private static <B, Self extends TApp<Maybe, B, Self>> TApp<Maybe, B, Maybe<B>> specialize(TApp<Maybe, B, Self> app) {
        return (TApp<Maybe, B, Maybe<B>>) app;
    }

    @SuppressWarnings("unchecked")
    private static <B, Self extends TApp<Maybe, B, Self>> TApp<Maybe, B, Self> generalize(TApp<Maybe, B, Maybe<B>> app) {
        return (TApp<Maybe, B, Self>) app;
    }

    /**
     * @param <T>
     */
    final static class Monadic<T> implements Monad<Maybe, T, Maybe<T>> {
        private final Maybe<T> aMaybe;

        private Monadic(Maybe<T> aMaybe) {
            this.aMaybe = aMaybe;
        }

        @Override
        public <B, NSelf extends TApp<Maybe, B, NSelf>> TApp<Maybe, B, NSelf> map(Function<? super T, B> function) {
            return generalize(new Monadic<>(aMaybe.map(function)));
        }

        @Override
        public <B, NSelf extends TApp<Maybe, B, NSelf>> TApp<Maybe, B, NSelf> flatmap(Function<? super T, TApp<Maybe, B, NSelf>> function) {
            final Function<T, Maybe<B>> tMaybeFunction = t -> {
                final TApp<Maybe, B, NSelf> apply = function.apply(t);
                return specialize(apply).self();
            };

            return generalize(new Monadic<>(aMaybe.flatmap(tMaybeFunction)));
        }

        @Override
        public <T1> T1 accept(Function<TApp<Maybe, T, Maybe<T>>, T1> f) {
            return aMaybe.accept(f);
        }

        @Override
        public Maybe<T> self() {
            return aMaybe;
        }
    }
}
