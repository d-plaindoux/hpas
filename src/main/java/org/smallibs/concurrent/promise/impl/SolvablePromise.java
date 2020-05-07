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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
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

        final T value;

        synchronized (this.future) {
            if (future.isDone() || future.isCancelled()) {
                try {
                    value = future.get();
                } catch (InterruptedException | ExecutionException e) {
                    return this;
                }
            } else {
                this.onSuccess.add(consumer);
                return this;
            }
        }

        consumer.accept(value);

        return this;
    }

    @Override
    public Promise<T> onFailure(Consumer<Throwable> consumer) {
        Objects.requireNonNull(consumer);

        Throwable value;

        synchronized (this.future) {
            if (future.isDone() || future.isCancelled()) {
                try {
                    this.future.get();
                    return this;
                } catch (InterruptedException e) {
                    value = e;
                } catch (ExecutionException e) {
                    value = e.getCause();
                }
            } else {
                this.onError.add(consumer);
                return this;
            }
        }

        consumer.accept(value);

        return this;
    }

    @Override
    public Promise<T> onComplete(final Consumer<Try<T>> consumer) {
        Objects.requireNonNull(consumer);

        return this.onSuccess(t -> consumer.accept(Try.success(t)))
                .onFailure(throwable -> consumer.accept(Try.failure(throwable)));
    }

    public boolean solve(final Try<T> response) {
        synchronized (this.future) {
            return this.future.solve(response);
        }
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
