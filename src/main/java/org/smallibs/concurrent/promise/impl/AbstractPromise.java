/*
 * HPAS
 * https://github.com/d-plaindoux/hpas
 *
 * Copyright (c) 2016-2017 Didier Plaindoux
 * Licensed under the LGPL2 license.
 */

package org.smallibs.concurrent.promise.impl;

import org.smallibs.concurrent.promise.Promise;
import org.smallibs.concurrent.promise.PromiseHelper;
import org.smallibs.exception.FilterException;
import org.smallibs.type.HK;
import org.smallibs.util.FunctionWithError;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

abstract class AbstractPromise<T> implements Promise<T> {

    AbstractPromise() {
    }

    @Override
    public <R> R apply(Function<HK<Promise, T, Promise<T>>, R> f) {
        return f.apply(this);
    }

    @Override
    public final <R> Promise<R> map(FunctionWithError<? super T, ? extends R> function) {
        Objects.requireNonNull(function);

        return new MappedPromise<>(this, function);
    }

    @Override
    public final <R> Promise<R> flatmap(Function<? super T, Promise<R>> function) {
        Objects.requireNonNull(function);

        return new FlatMappedPromise<>(this, function);
    }

    @Override
    public HK<Promise, T, Promise<T>> filter(Predicate<? super T> predicate) {
        return this.flatmap(t -> {
            if (predicate.test(t)) {
                return PromiseHelper.success(t);
            } else {
                return PromiseHelper.failure(new FilterException());
            }
        });
    }

    @Override
    final public Promise<T> self() {
        return this;
    }

}
