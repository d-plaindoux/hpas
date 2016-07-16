package org.smallibs.concurrent.promise.impl;

import org.smallibs.concurrent.promise.Promise;
import org.smallibs.data.Monad;

import java.util.Objects;
import java.util.function.Function;

abstract class AbstractPromise<T> implements Promise<T> {

    protected AbstractPromise() {
    }

    @Override
    final public <R> Promise<R> map(Function<? super T, R> function) {
        Objects.requireNonNull(function);

        return new MappedPromise<>(this, function).concretize();
    }

    @Override
    final public <R> Promise<R> flatmap(Function<? super T, Monad<Promise, R>> function) {
        Objects.requireNonNull(function);

        return new FlatMappedPromise<>(this, function).concretize();
    }

    @Override
    @SuppressWarnings("unchecked")
    final public Promise<T> concretize() {
        return this;
    }

}
