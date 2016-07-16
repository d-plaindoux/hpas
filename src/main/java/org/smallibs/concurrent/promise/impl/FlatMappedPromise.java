package org.smallibs.concurrent.promise.impl;

import org.smallibs.concurrent.promise.Promise;
import org.smallibs.data.TApp;
import org.smallibs.data.Try;

import java.util.concurrent.Future;
import java.util.function.Consumer;
import java.util.function.Function;

import static org.smallibs.concurrent.promise.Promise.specialize;
import static org.smallibs.data.Try.specialize;

final class FlatMappedPromise<T, R, Self extends TApp<Promise, R, Self>> extends AbstractPromise<R> {

    private final Promise<T> promise;
    private final Function<? super T, TApp<Promise, R, Self>> transform;

    FlatMappedPromise(Promise<T> promise, Function<? super T, TApp<Promise, R, Self>> transform) {
        super();

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
            specialize(transform.apply(t).self().<Promise<R>>self()).self().onSuccess(consumer);
        });
    }

    @Override
    public void onFailure(final Consumer<Throwable> consumer) {
        promise.onFailure(consumer);
    }

    @Override
    public void onComplete(Consumer<Try<R>> consumer) {
        promise.onComplete(value -> {
            specialize(value.map(transform)).self().
                    onSuccess(o -> specialize(o).self().onComplete(consumer)).
                    onFailure(t -> consumer.accept(Try.failure(t)));
        });
    }
}
