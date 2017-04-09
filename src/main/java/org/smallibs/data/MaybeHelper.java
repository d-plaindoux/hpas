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
import org.smallibs.exception.NoValueException;
import org.smallibs.type.Kind;

import java.util.function.Function;

public enum MaybeHelper {
    ;

    public static <T> Monad<Maybe, T, Maybe<T>> monad(Maybe<T> maybe) {
        return new MaybeHelper.Monadic<T>(maybe);
    }

    public static <T> Try<T> toTry(Maybe<T> maybe) {
        return maybe.map(Try::success).orElse(Try.failure(new NoValueException()));
    }

    @SuppressWarnings("unchecked")
    private static <B, Self extends Kind<Maybe, B, Self>> Kind<Maybe, B, Maybe<B>> specialize(Kind<Maybe, B, Self> app) {
        return (Kind<Maybe, B, Maybe<B>>) app;
    }

    @SuppressWarnings("unchecked")
    private static <B, Self extends Kind<Maybe, B, Self>> Kind<Maybe, B, Self> generalize(Kind<Maybe, B, Maybe<B>> app) {
        return (Kind<Maybe, B, Self>) app;
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
        public <B, NSelf extends Kind<Maybe, B, NSelf>> Kind<Maybe, B, NSelf> map(Function<? super T, B> function) {
            return generalize(new Monadic<>(aMaybe.map(function)));
        }

        @Override
        public <B, NSelf extends Kind<Maybe, B, NSelf>> Kind<Maybe, B, NSelf> flatmap(Function<? super T, Kind<Maybe, B, NSelf>> function) {
            final Function<T, Maybe<B>> tMaybeFunction = t -> {
                final Kind<Maybe, B, NSelf> apply = function.apply(t);
                return specialize(apply).self();
            };

            return generalize(new Monadic<>(aMaybe.flatmap(tMaybeFunction)));
        }

        @Override
        public <T1> T1 accept(Function<Kind<Maybe, T, Maybe<T>>, T1> f) {
            return aMaybe.accept(f);
        }

        @Override
        public <B, NSelf extends Kind<Maybe, B, NSelf>> Kind<Maybe, B, NSelf> apply(Functor<Maybe, Function<? super T, ? extends B>, ?> functor) {
            return generalize(new Monadic<>(aMaybe.flatmap(a -> {
                final Kind<Maybe, B, NSelf> map = functor.map(bFunction -> bFunction.apply(a));
                return specialize(map).self();
            })));
        }

        @Override
        public Maybe<T> self() {
            return aMaybe;
        }
    }
}
