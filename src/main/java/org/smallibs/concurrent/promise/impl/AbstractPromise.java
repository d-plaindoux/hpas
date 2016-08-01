package org.smallibs.concurrent.promise.impl;

import org.smallibs.concurrent.promise.Promise;
import org.smallibs.type.TApp;

import java.util.Objects;
import java.util.function.Function;

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
    final public Promise<T> self() {
        return this;
    }

}
