/*
 * HPAS
 * https://github.com/d-plaindoux/hpas
 *
 * Copyright (c) 2016 Didier Plaindoux
 * Licensed under the LGPL2 license.
 */

package org.smallibs.concurrent.promise.impl;

import org.smallibs.concurrent.promise.Promise;
import org.smallibs.data.TApp;
import org.smallibs.data.Try;

import java.util.concurrent.Future;
import java.util.function.Consumer;
import java.util.function.Function;

final class MappedPromise<T, R> extends AbstractPromise<R> {

    private final Promise<T> promise;
    private final Function<? super T, R> transform;

    MappedPromise(Promise<T> promise, Function<? super T, R> transform) {
        this.promise = promise;
        this.transform = transform;
    }

    @Override
    public Future<R> getFuture() {
        return new MappedFuture<>(promise.getFuture(), transform);
    }

    @Override
    public void onSuccess(final Consumer<R> consumer) {
        promise.onSuccess(t -> consumer.accept(transform.apply(t)));
    }

    @Override
    public void onFailure(final Consumer<Throwable> consumer) {
        promise.onFailure(consumer);
    }

    @Override
    public void onComplete(Consumer<Try<R>> consumer) {
        promise.onComplete(value -> {
            final TApp<Try, R, ? extends Try<R>> map = value.map(transform);
            consumer.accept(map.self());
        });
    }
}
