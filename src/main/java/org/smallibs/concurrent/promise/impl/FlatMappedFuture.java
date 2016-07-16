package org.smallibs.concurrent.promise.impl;

import org.smallibs.concurrent.promise.Promise;
import org.smallibs.data.TApp;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;

import static org.smallibs.concurrent.promise.Promise.specialize;

class FlatMappedFuture<T, R, Self extends TApp<Promise, R, Self>> implements Future<R> {

    private final Future<T> future;
    private final Function<? super T, TApp<Promise, R, Self>> function;

    FlatMappedFuture(Future<T> future, Function<? super T, TApp<Promise, R, Self>> function) {
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
        return specialize(this.function.apply(this.future.get())).self().getFuture().get();
    }

    @Override
    public R get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return specialize(this.function.apply(this.future.get(timeout, unit))).self().getFuture().get(timeout, unit);
    }
}
