/*
 * HPAS
 * https://github.com/d-plaindoux/hpas
 *
 * Copyright (c) 2016 Didier Plaindoux
 * Licensed under the LGPL2 license.
 */

package org.smallibs.concurrent.promise.impl;

import org.smallibs.concurrent.promise.Promise;
import org.smallibs.data.Maybe;
import org.smallibs.data.Try;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public class SolvablePromise<T> extends AbstractPromise<T> {

    private final SolvableFuture<T> future;
    private final List<Consumer<T>> onSuccess;
    private final List<Consumer<Throwable>> onError;

    public SolvablePromise() {
        this.future = createFuture(this::notifyResponse);

        this.onSuccess = new ArrayList<>();
        this.onError = new ArrayList<>();
    }

    @Override
    public Future<T> getFuture() {
        return future;
    }

    @Override
    public Promise<T> onSuccess(Consumer<T> consumer) {
        Objects.requireNonNull(consumer);

        final AtomicReference<T> success = new AtomicReference<>();

        synchronized (this.future) {
            if (future.isDone() || future.isCancelled()) {
                try {
                    success.set(this.future.get());
                } catch (InterruptedException | ExecutionException consume) {
                    // Ignore
                }
            } else {
                this.onSuccess.add(consumer);
            }
        }

        Maybe.some(success.get()).onSome(consumer);
        return this;
    }

    @Override
    public Promise<T> onFailure(Consumer<Throwable> consumer) {
        Objects.requireNonNull(consumer);

        final AtomicReference<Throwable> failure = new AtomicReference<>();

        synchronized (this.future) {
            if (future.isDone() || future.isCancelled()) {
                try {
                    this.future.get();
                } catch (ExecutionException e) {
                    failure.set(e.getCause());
                } catch (InterruptedException e) {
                    failure.set(e);
                }
            } else {
                this.onError.add(consumer);
            }
        }

        Maybe.some(failure.get()).onSome(consumer);

        return this;
    }

    @Override
    public Promise<T> onComplete(final Consumer<Try<T>> consumer) {
        Objects.requireNonNull(consumer);

        this.onSuccess(t -> consumer.accept(Try.success(t)));
        this.onFailure(throwable -> consumer.accept(Try.failure(throwable)));

        return this;
    }

    public void solve(final Try<T> response) {
        synchronized (this.future) {
            this.future.solve(response);
        }

        this.notifyResponse(response);
    }

    //
    // Protected behaviors
    //

    protected SolvableFuture<T> createFuture(Consumer<Try<T>> callbackOnComplete) {
        return new SolvableFuture<>(callbackOnComplete);
    }

    //
    // Private behaviors
    //

    private void notifyResponse(Try<T> response) {
        response.onSuccess(s -> {
            onSuccess.forEach(c -> c.accept(s));
            onSuccess.clear();
        }).onFailure(t -> {
            onError.forEach(c -> c.accept(t));
            onError.clear();
        });
    }
}
