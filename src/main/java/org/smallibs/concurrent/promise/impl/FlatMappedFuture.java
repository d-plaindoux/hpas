/*
 * HPAS
 * https://github.com/d-plaindoux/hpas
 *
 * Copyright (c) 2016-2017 Didier Plaindoux
 * Licensed under the LGPL2 license.
 */

package org.smallibs.concurrent.promise.impl;

import org.smallibs.concurrent.promise.Promise;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;

class FlatMappedFuture<T, R> implements Future<R> {

    private final Future<T> future;
    private final Function<? super T, Promise<R>> function;

    FlatMappedFuture(Future<T> future, Function<? super T, Promise<R>> function) {
        this.future = future;
        this.function = function;
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return this.future.cancel(mayInterruptIfRunning);
    }

    @Override
    public boolean isCancelled() {
        return this.future.isCancelled();
    }

    @Override
    public boolean isDone() {
        return this.future.isDone();
    }

    @Override
    public R get() throws InterruptedException, ExecutionException {
        return this.function.apply(this.future.get()).getFuture().get();
    }

    @Override
    public R get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return this.function.apply(this.future.get(timeout, unit)).getFuture().get(timeout, unit);
    }
}
