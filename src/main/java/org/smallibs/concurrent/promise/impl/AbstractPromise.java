/*
 * HPAS
 * https://github.com/d-plaindoux/hpas
 *
 * Copyright (c) 2016 Didier Plaindoux
 * Licensed under the LGPL2 license.
 */

package org.smallibs.concurrent.promise.impl;

import org.smallibs.concurrent.promise.Promise;
import org.smallibs.exception.FilterException;
import org.smallibs.type.TApp;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

abstract class AbstractPromise<T> implements Promise<T> {

    AbstractPromise() {
    }

    @Override
    public <R> R accept(Function<TApp<Promise, T, Promise<T>>, R> f) {
        return f.apply(this);
    }

    @Override
    public final <R> Promise<R> map(Function<? super T, R> function) {
        Objects.requireNonNull(function);

        return new MappedPromise<>(this, function);
    }

    @Override
    public final <R> Promise<R> flatmap(Function<? super T, Promise<R>> function) {
        Objects.requireNonNull(function);

        return new FlatMappedPromise<>(this, function);
    }

    @Override
    public TApp<Promise, T, Promise<T>> filter(Predicate<? super T> predicate) {
        return this.flatmap(t -> {
            if (predicate.test(t)) {
                return SolvedPromise.success(t);
            } else {
                return SolvedPromise.failure(new FilterException());
            }
        });
    }

    @Override
    final public Promise<T> self() {
        return this;
    }

}
