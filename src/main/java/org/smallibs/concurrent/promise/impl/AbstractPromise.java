package org.smallibs.concurrent.promise.impl;

import org.smallibs.concurrent.promise.Promise;
import org.smallibs.data.TApp;

import java.util.Objects;
import java.util.function.Function;

import static org.smallibs.concurrent.promise.Promise.generalize;

abstract class AbstractPromise<T> implements Promise<T> {

    AbstractPromise() {
    }

    @Override
    public <R> R accept(Function<TApp<Promise, T, ? extends Promise>, R> f) {
        return f.apply(this);
    }

    @Override
    public final <B, Self extends TApp<Promise, B, Self>> TApp<Promise, B, Self> map(Function<? super T, B> function) {
        Objects.requireNonNull(function);

        return generalize(new MappedPromise<>(this, function));
    }

    @Override
    final public <B, Self extends TApp<Promise, B, Self>> TApp<Promise, B, Self> flatmap(Function<? super T, TApp<Promise, B, Self>> function) {
        Objects.requireNonNull(function);

        return generalize(new FlatMappedPromise<>(this, function));
    }

    @Override
    final public Promise<T> self() {
        return this;
    }

}
