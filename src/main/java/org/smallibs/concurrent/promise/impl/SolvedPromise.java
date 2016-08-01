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

import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

public final class SolvedPromise<T> extends AbstractPromise<T> {

    private final Try<T> value;

    private SolvedPromise(Try<T> value) {
        this.value = value;
    }

    public static <T> Promise<T> success(T t) {
        Objects.requireNonNull(t);
        return new SolvedPromise<T>(Try.success(t));
    }

    public static <T> Promise<T> failure(Throwable t) {
        Objects.requireNonNull(t);
        return new SolvedPromise<T>(Try.failure(t));
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
                if (value.isSuccess()) {
                    return value.success();
                }

                throw new ExecutionException(value.failure());
            }

            @Override
            public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
                return null;
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
