/*
 * HPAS
 * https://github.com/d-plaindoux/hpas
 *
 * Copyright (c) 2016-2025 Didier Plaindoux
 * Licensed under the LGPL2 license.
 */

package org.smallibs.concurrent.promise;

import org.smallibs.concurrent.promise.impl.PromisesSet;
import org.smallibs.concurrent.promise.impl.SolvablePromise;
import org.smallibs.concurrent.promise.impl.SolvedPromise;
import org.smallibs.control.Applicative;
import org.smallibs.control.Functor;
import org.smallibs.control.Monad;
import org.smallibs.data.Try;
import org.smallibs.data.Unit;
import org.smallibs.type.HK;
import org.smallibs.util.FunctionsHelper;

import java.util.ArrayList;
import java.util.Collections;
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

    public static Promise<Unit> join(Promise... promises) {
        return new PromisesSet(PromisesSet.Strategy.NO_STOP, promises);
    }

    public static <T> Promise<List<T>> sequence(List<Promise<T>> promises) {
        final List<T> result = Collections.synchronizedList(new ArrayList<>());
        final SolvablePromise<List<T>> solvablePromise = new SolvablePromise<>();
        final Promise[] promisesArray = promises.stream()
                .map(p -> p.onSuccess(result::add))
                .toArray(Promise[]::new);

        PromiseHelper
                .join(promisesArray)
                .onComplete(c -> solvablePromise.solve(Try.pure(result)));

        return solvablePromise;
    }

    public static Promise<Unit> forall(Promise... promises) {
        return new PromisesSet(PromisesSet.Strategy.STOP_ON_ERROR, promises);
    }

    public static Promise<Unit> exists(Promise... promises) {
        return new PromisesSet(PromisesSet.Strategy.STOP_ON_SUCCESS, promises);
    }

    @SuppressWarnings("unchecked")
    private static <B, S extends HK<Promise, B, S>> HK<Promise, B, Promise<B>> specialize(HK<Promise, B, S> app) {
        return (HK<Promise, B, Promise<B>>) app;
    }

    @SuppressWarnings("unchecked")
    private static <B, S extends HK<Promise, B, S>> HK<Promise, B, S> generalize(HK<Promise, B, Promise<B>> app) {
        return (HK<Promise, B, S>) app;
    }

    //
    // Internal classes
    //

    @FunctionalInterface
    private interface Functor4Promise<T> extends Functor<Promise, T, Promise<T>> {

        @Override
        default <B, NS extends HK<Promise, B, NS>> HK<Promise, B, NS> map(Function<? super T, ? extends B> function) {
            return generalize((Functor4Promise<B>) () -> self().map(FunctionsHelper.fromFunction(function)));
        }

    }

    @FunctionalInterface
    private interface Applicative4Promise<T> extends Functor4Promise<T>, Applicative<Promise, T, Promise<T>> {

        @Override
        default <B, NS extends HK<Promise, B, NS>> HK<Promise, B, NS> apply(Functor<Promise, Function<? super T, ? extends B>, ?> functor) {
            return generalize((Applicative4Promise<B>) () -> self().flatmap(a -> {
                final HK<Promise, B, NS> map = functor.map(bFunction -> bFunction.apply(a));
                return specialize(map).self();
            }));
        }
    }

    /**
     * @param <T>
     */
    private interface Monad4Promise<T> extends Applicative4Promise<T>, Monad<Promise, T, Promise<T>> {

        @Override
        default <B, NS extends HK<Promise, B, NS>> HK<Promise, B, NS> flatmap(Function<? super T, HK<Promise, B, NS>> function) {
            final Function<T, Promise<B>> tPromiseFunction = t -> {
                final HK<Promise, B, NS> apply = function.apply(t);
                return specialize(apply).self();
            };

            return generalize((Monad4Promise<B>) () -> self().flatmap(tPromiseFunction));
        }
    }
}
