/*
 * HPAS
 * https://github.com/d-plaindoux/hpas
 *
 * Copyright (c) 2016-2017 Didier Plaindoux
 * Licensed under the LGPL2 license.
 */

package org.smallibs.concurrent.promise;

import org.smallibs.concurrent.promise.impl.PromisesSet;
import org.smallibs.concurrent.promise.impl.SolvedPromise;
import org.smallibs.control.Functor;
import org.smallibs.control.Monad;
import org.smallibs.data.Try;
import org.smallibs.data.Unit;
import org.smallibs.type.HK;
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

    public static Promise<Unit> join(Promise<?>... promises) {
        return new PromisesSet(PromisesSet.Strategy.NO_STOP, promises);
    }

    public static Promise<Unit> forall(Promise<?>... promises) {
        return new PromisesSet(PromisesSet.Strategy.STOP_ON_ERROR, promises);
    }

    public static Promise<Unit> exists(Promise<?>... promises) {
        return new PromisesSet(PromisesSet.Strategy.STOP_ON_SUCCESS, promises);
    }

    @SuppressWarnings("unchecked")
    private static <B, Self extends HK<Promise, B, Self>> HK<Promise, B, Promise<B>> specialize(HK<Promise, B, Self> app) {
        return (HK<Promise, B, Promise<B>>) app;
    }

    @SuppressWarnings("unchecked")
    private static <B, Self extends HK<Promise, B, Self>> HK<Promise, B, Self> generalize(HK<Promise, B, Promise<B>> app) {
        return (HK<Promise, B, Self>) app;
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
        public <B, NSelf extends HK<Promise, B, NSelf>> HK<Promise, B, NSelf> map(Function<? super T, B> function) {
            return generalize(new Monadic<>(promise.map(FunctionsWithError.fromFunction(function))));
        }

        @Override
        public <B, NSelf extends HK<Promise, B, NSelf>> HK<Promise, B, NSelf> flatmap(Function<? super T, HK<Promise, B, NSelf>> function) {
            final Function<T, Promise<B>> tPromiseFunction = t -> {
                final HK<Promise, B, NSelf> apply = function.apply(t);
                return PromiseHelper.specialize(apply).self();
            };
            return generalize(new Monadic<>(promise.flatmap(tPromiseFunction)));
        }

        @Override
        public <T1> T1 accept(Function<HK<Promise, T, Promise<T>>, T1> f) {
            return promise.accept(f);
        }


        @Override
        public <B, NSelf extends HK<Promise, B, NSelf>> HK<Promise, B, NSelf> apply(Functor<Promise, Function<? super T, ? extends B>, ?> functor) {
            return generalize(new Monadic<>(promise.flatmap(a -> {
                final HK<Promise, B, NSelf> map = functor.map(bFunction -> bFunction.apply(a));
                return specialize(map).self();
            })));
        }

        @Override
        public Promise<T> self() {
            return promise;
        }
    }

}
