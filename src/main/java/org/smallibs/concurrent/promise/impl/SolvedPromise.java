/*
 * HPAS
 * https://github.com/d-plaindoux/hpas
 *
 * Copyright (c) 2016-2017 Didier Plaindoux
 * Licensed under the LGPL2 license.
 */

package org.smallibs.concurrent.promise.impl;

import org.smallibs.data.Try;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

public final class SolvedPromise<T> extends AbstractPromise<T> {

    private final Try<T> value;

    public SolvedPromise(Try<T> value) {
        this.value = value;
    }

    @Override
    public Future<T> getFuture() {
        return new Future<T>() {
            @Override
            public boolean cancel(boolean mayInterruptIfRunning) {
                return false;
            }

            @Override
            public boolean isCancelled() {
                return false;
            }

            @Override
            public boolean isDone() {
                return true;
            }

            @Override
            public T get() throws InterruptedException, ExecutionException {
                return value.orElseThrow(t -> new ExecutionException(t));
            }

            @Override
            public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
                return get();
            }
        };
    }

    @Override
    public void onSuccess(Consumer<T> consumer) {
        value.onSuccess(consumer);
    }

    @Override
    public void onFailure(Consumer<Throwable> consumer) {
        value.onFailure(consumer);
    }

    @Override
    public void onComplete(Consumer<Try<T>> consumer) {
        consumer.accept(value);
    }
}
