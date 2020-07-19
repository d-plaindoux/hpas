/*
 * HPAS
 * https://github.com/d-plaindoux/hpas
 *
 * Copyright (c) 2016-2017 Didier Plaindoux
 * Licensed under the LGPL2 license.
 */

package org.smallibs.data;

import org.smallibs.control.Functor;
import org.smallibs.control.Monad;
import org.smallibs.exception.NoValueException;
import org.smallibs.type.HK;

import java.util.Objects;
import java.util.function.Function;

public enum MaybeHelper {
    ;

    public static <T> Monad<Maybe, T, Maybe<T>> monad(Maybe<T> maybe) {
        return new Monadic<T>(maybe);
    }

    public static <T> Try<T> toTry(Maybe<T> maybe) {
        return maybe.map(Try::success).orElse(Try.failure(new NoValueException()));
    }

    @SuppressWarnings("unchecked")
    private static <B, S extends HK<Maybe, B, S>> HK<Maybe, B, Maybe<B>> specialize(HK<Maybe, B, S> app) {
        return (HK<Maybe, B, Maybe<B>>) app;
    }

    @SuppressWarnings("unchecked")
    private static <B, S extends HK<Maybe, B, S>> HK<Maybe, B, S> generalize(HK<Maybe, B, Maybe<B>> app) {
        return (HK<Maybe, B, S>) app;
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
        public <B, NS extends HK<Maybe, B, NS>> HK<Maybe, B, NS> map(Function<? super T, ? extends B> function) {
            return generalize(new Monadic<>(aMaybe.map(function)));
        }

        @Override
        public <B, NS extends HK<Maybe, B, NS>> HK<Maybe, B, NS> flatmap(Function<? super T, HK<Maybe, B, NS>> function) {
            final Function<T, Maybe<B>> tMaybeFunction = t -> {
                final HK<Maybe, B, NS> applied = function.apply(t);
                return specialize(applied).self();
            };

            return generalize(new Monadic<>(aMaybe.flatmap(tMaybeFunction)));
        }

        @Override
        public <B, NS extends HK<Maybe, B, NS>> HK<Maybe, B, NS> apply(Functor<Maybe, Function<? super T, ? extends B>, ?> functor) {
            return generalize(new Monadic<>(aMaybe.flatmap(a -> {
                final HK<Maybe, B, NS> map = functor.map(bFunction -> bFunction.apply(a));
                return specialize(map).self();
            })));
        }

        @Override
        public Maybe<T> self() {
            return aMaybe;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Monadic<?> monadic = (Monadic<?>) o;
            return Objects.equals(aMaybe, monadic.aMaybe);
        }

        @Override
        public int hashCode() {
            return Objects.hash(aMaybe);
        }
    }
}
