/*
 * HPAS
 * https://github.com/d-plaindoux/hpas
 *
 * Copyright (c) 2016 Didier Plaindoux
 * Licensed under the LGPL2 license.
 */

package org.smallibs.concurrent.promise;

import org.smallibs.concurrent.promise.impl.SolvedPromise;
import org.smallibs.control.Functor;
import org.smallibs.control.Monad;
import org.smallibs.data.Try;
import org.smallibs.type.HoType;
import org.smallibs.util.FunctionsWithError;

import java.util.function.Function;

public enum PromiseHelper {
    ;

    public static <T> Monad<Promise, T, Promise<T>> monad(Promise<T> promise) {
        return new Monadic<>(promise);
    }

    public static <T> Promise<T> success(T t) {
        return new SolvedPromise<T>(Try.success(t));
    }

    public static <T> Promise<T> failure(Throwable t) {
        return new SolvedPromise<T>(Try.failure(t));
    }

    @SuppressWarnings("unchecked")
    private static <B, Self extends HoType<Promise, B, Self>> HoType<Promise, B, Promise<B>> specialize(HoType<Promise, B, Self> app) {
        return (HoType<Promise, B, Promise<B>>) app;
    }

    @SuppressWarnings("unchecked")
    private static <B, Self extends HoType<Promise, B, Self>> HoType<Promise, B, Self> generalize(HoType<Promise, B, Promise<B>> app) {
        return (HoType<Promise, B, Self>) app;
    }

    /**
     * @param <T>
     */
    private final static class Monadic<T> implements Monad<Promise, T, Promise<T>> {
        private final Promise<T> promise;

        Monadic(Promise<T> promise) {
            this.promise = promise;
        }

        @Override
        public <B, NSelf extends HoType<Promise, B, NSelf>> HoType<Promise, B, NSelf> map(Function<? super T, B> function) {
            return generalize(new Monadic<>(promise.map(FunctionsWithError.fromFunction(function))));
        }

        @Override
        public <B, NSelf extends HoType<Promise, B, NSelf>> HoType<Promise, B, NSelf> flatmap(Function<? super T, HoType<Promise, B, NSelf>> function) {
            final Function<T, Promise<B>> tPromiseFunction = t -> {
                final HoType<Promise, B, NSelf> apply = function.apply(t);
                return PromiseHelper.specialize(apply).self();
            };
            return generalize(new Monadic<>(promise.flatmap(tPromiseFunction)));
        }

        @Override
        public <T1> T1 accept(Function<HoType<Promise, T, Promise<T>>, T1> f) {
            return promise.accept(f);
        }


        @Override
        public <B, NSelf extends HoType<Promise, B, NSelf>> HoType<Promise, B, NSelf> apply(Functor<Promise, Function<? super T, ? extends B>, ?> functor) {
            return generalize(new Monadic<>(promise.flatmap(a -> {
                final HoType<Promise, B, NSelf> map = functor.map(bFunction -> bFunction.apply(a));
                return specialize(map).self();
            })));
        }

        @Override
        public Promise<T> self() {
            return promise;
        }
    }

}
