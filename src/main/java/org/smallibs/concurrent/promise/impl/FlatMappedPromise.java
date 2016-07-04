package org.smallibs.concurrent.promise.impl;

import org.smallibs.concurrent.promise.Promise;
import org.smallibs.util.Try;

import java.util.concurrent.Future;
import java.util.function.Consumer;
import java.util.function.Function;

class FlatMappedPromise<T, R> implements Promise<R> {

    private final Promise<T> promise;
    private final Function<? super T, ? extends Promise<R>> transform;

    FlatMappedPromise(Promise<T> promise, Function<? super T, ? extends Promise<R>> transform) {
        this.promise = promise;
        this.transform = transform;
    }

    @Override
    public Future<R> getFuture() {
        return new FlatMappedFuture<>(promise.getFuture(), transform);
    }

    @Override
    public void onSuccess(final Consumer<R> consumer) {
        promise.onSuccess(t -> {
            transform.apply(t).map(r -> {
                consumer.accept(r);
                return r;
            });
        });
    }

    @Override
    public void onFailure(final Consumer<Throwable> consumer) {
        promise.onFailure(consumer);
    }

    @Override
    public void onComplete(Consumer<Try<R>> consumer) {
        promise.flatmap(transform).onComplete(consumer);
    }

    @Override
    public <S> Promise<S> map(Function<? super R, S> function) {
        return new MappedPromise<>(this, function);
    }

    @Override
    public <S> Promise<S> flatmap(Function<? super R, ? extends Promise<S>> function) {
        return new FlatMappedPromise<>(this, function);
    }
}
