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
import org.smallibs.control.Applicative;
import org.smallibs.control.Functor;
import org.smallibs.control.Monad;
import org.smallibs.data.Try;
import org.smallibs.type.HK;
import org.smallibs.util.FunctionsHelper;

import java.util.List;
import java.util.function.Function;

public enum PromiseHelper {
    ;

    public static <T> Functor<Promise, T, Promise<T>> functor(Promise<T> promise) {
        return (Functor4Promise<T>) () -> promise;
    }

    public static <T> Applicative<Promise, T, Promise<T>> applicative(Promise<T> promise) {
        return (Applicative4Promise<T>) () -> promise;
    }

    public static <T> Monad<Promise, T, Promise<T>> monad(Promise<T> promise) {
        return (Monad4Promise<T>) () -> promise;
    }

    public static <T> Promise<T> success(T t) {
        return new SolvedPromise<>(Try.success(t));
    }

    public static <T> Promise<T> failure(Throwable t) {
        return new SolvedPromise<>(Try.failure(t));
    }

    @SafeVarargs
    public static <T> Promise<List<T>> join(Promise<T>... promises) {
        return new PromisesSet<>(PromisesSet.Strategy.NO_STOP, promises);
    }

    @SafeVarargs
    public static <T> Promise<List<T>> forall(Promise<T>... promises) {
        return new PromisesSet<>(PromisesSet.Strategy.STOP_ON_ERROR, promises);
    }

    @SafeVarargs
    public static <T> Promise<List<T>> exists(Promise<T>... promises) {
        return new PromisesSet<>(PromisesSet.Strategy.STOP_ON_SUCCESS, promises);
    }

    @SuppressWarnings("unchecked")
    private static <B, Self extends HK<Promise, B, Self>> HK<Promise, B, Promise<B>> specialize(HK<Promise, B, Self> app) {
        return (HK<Promise, B, Promise<B>>) app;
    }

    @SuppressWarnings("unchecked")
    private static <B, Self extends HK<Promise, B, Self>> HK<Promise, B, Self> generalize(HK<Promise, B, Promise<B>> app) {
        return (HK<Promise, B, Self>) app;
    }

    //
    // Internal classes
    //

    @FunctionalInterface
    private interface Functor4Promise<T> extends Functor<Promise, T, Promise<T>> {

        @Override
        default <B, NSelf extends HK<Promise, B, NSelf>> HK<Promise, B, NSelf> map(Function<? super T, ? extends B> function) {
            return generalize((Functor4Promise<B>) () -> self().map(FunctionsHelper.fromFunction(function)));
        }

        @Override
        default <T1> T1 accept(Function<HK<Promise, T, Promise<T>>, T1> f) {
            return self().accept(f);
        }

    }

    @FunctionalInterface
    private interface Applicative4Promise<T> extends Functor4Promise<T>, Applicative<Promise, T, Promise<T>> {

        @Override
        default <B, NSelf extends HK<Promise, B, NSelf>> HK<Promise, B, NSelf> apply(Functor<Promise, Function<? super T, ? extends B>, ?> functor) {
            return generalize((Applicative4Promise<B>) () -> self().flatmap(a -> {
                final HK<Promise, B, NSelf> map = functor.map(bFunction -> bFunction.apply(a));
                return specialize(map).self();
            }));
        }
    }

    /**
     * @param <T>
     */
    private interface Monad4Promise<T> extends Applicative4Promise<T>, Monad<Promise, T, Promise<T>> {

        @Override
        default <B, NSelf extends HK<Promise, B, NSelf>> HK<Promise, B, NSelf> flatmap(Function<? super T, HK<Promise, B, NSelf>> function) {
            final Function<T, Promise<B>> tPromiseFunction = t -> {
                final HK<Promise, B, NSelf> apply = function.apply(t);
                return specialize(apply).self();
            };

            return generalize((Monad4Promise<B>) () -> self().flatmap(tPromiseFunction));
        }
    }
}
