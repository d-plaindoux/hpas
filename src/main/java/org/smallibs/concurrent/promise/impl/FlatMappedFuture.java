package org.smallibs.concurrent.promise.impl;

import org.smallibs.concurrent.promise.Promise;
import org.smallibs.data.Monad;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;

class FlatMappedFuture<T, R> implements Future<R> {

    private final Future<T> future;
    private final Function<? super T, ? extends Monad<Promise, R>> function;

    FlatMappedFuture(Future<T> future, Function<? super T, ? extends Monad<Promise, R>> function) {
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
        final Promise<R> concretized = this.function.apply(this.future.get()).concretize();
        return concretized.getFuture().get();
    }

    @Override
    public R get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        final Promise<R> concretized = this.function.apply(this.future.get(timeout, unit)).concretize();
        return concretized.getFuture().get(timeout, unit);
    }
}
