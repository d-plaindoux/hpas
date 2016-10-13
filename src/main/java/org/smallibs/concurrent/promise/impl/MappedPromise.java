/*
 * HPAS
 * https://github.com/d-plaindoux/hpas
 *
 * Copyright (c) 2016 Didier Plaindoux
 * Licensed under the LGPL2 license.
 */

package org.smallibs.concurrent.promise.impl;

import org.smallibs.concurrent.promise.Promise;
import org.smallibs.data.Try;
import org.smallibs.util.FunctionWithError;
import org.smallibs.util.FunctionsWithError;

import java.util.concurrent.Future;
import java.util.function.Consumer;
import java.util.function.Function;

final class MappedPromise<T, R> extends AbstractPromise<R> {

    private final Promise<T> promise;
    private final Function<? super T, Try<R>> transform;

    MappedPromise(Promise<T> promise, FunctionWithError<? super T, R> transform) {
        this.promise = promise;
        this.transform = FunctionsWithError.toFunction(transform);
    }

    @Override
    public Future<R> getFuture() {
        return new MappedFuture<>(promise.getFuture(), transform);
    }

    @Override
    public void onSuccess(final Consumer<R> consumer) {
        promise.onSuccess(t -> transform.apply(t).onSuccess(consumer));
    }

    @Override
    public void onFailure(final Consumer<Throwable> consumer) {
        promise.onFailure(consumer);
    }

    @Override
    public void onComplete(Consumer<Try<R>> consumer) {
        promise.onComplete(value -> consumer.accept(value.flatmap(transform).self()));
    }
}
