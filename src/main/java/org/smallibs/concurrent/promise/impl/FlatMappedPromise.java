package org.smallibs.concurrent.promise.impl;

import org.smallibs.concurrent.promise.Promise;
import org.smallibs.data.Monad;
import org.smallibs.data.Try;

import java.util.concurrent.Future;
import java.util.function.Consumer;
import java.util.function.Function;

public class FlatMappedPromise<T, R> implements Promise<R> {

    private final Promise<T> promise;
    private final Function<? super T, Monad<Promise, R>> transform;

    public FlatMappedPromise(Monad<Promise, T> promise, Function<? super T, Monad<Promise, R>> transform) {
        this.promise = promise.concretize();
        this.transform = transform;
    }

    @Override
    public Future<R> getFuture() {
        final Promise<T> concretize = promise.concretize();
        return new FlatMappedFuture<>(concretize.getFuture(), transform);
    }

    @Override
    public void onSuccess(final Consumer<R> consumer) {
        promise.onSuccess(t -> {
            transform.apply(t).<Promise<R>>concretize().onSuccess(consumer);
        });
    }

    @Override
    public void onFailure(final Consumer<Throwable> consumer) {
        promise.onFailure(consumer);
    }

    @Override
    public void onComplete(Consumer<Try<R>> consumer) {
        promise.onComplete(value -> {
            final Try<Monad<Promise, R>> concretized = value.map(transform).concretize();
            concretized.
                    onSuccess(o -> o.<Promise<R>>concretize().onComplete(consumer)).
                    onFailure(t -> consumer.accept(Try.failure(t)));
        });
    }

    @Override
    @SuppressWarnings("unchecked")
    public FlatMappedPromise<T, R> concretize() {
        return this;
    }
}
