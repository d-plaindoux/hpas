/*
 * HPAS
 * https://github.com/d-plaindoux/hpas
 *
 * Copyright (c) 2016-2025 Didier Plaindoux
 * Licensed under the LGPL2 license.
 */

package org.smallibs.concurrent.promise.impl;

import org.smallibs.concurrent.promise.Promise;
import org.smallibs.data.Try;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

public class SolvablePromise<T> extends AbstractPromise<T> {

    private final SolvableFuture<T> future;

    private CountDownLatch awaiting;
    private List<Consumer<T>> onSuccess;
    private List<Consumer<Throwable>> onError;

    public SolvablePromise() {
        this.future = createFuture(this::notifyResponse);

        this.onSuccess = null;
        this.onError = null;
        this.awaiting = null;
    }

    @Override
    public Future<T> getFuture() {
        return future;
    }

    @Override
    public T await() throws Exception {
        return await(MAX_AWAIT_DURATION);
    }

    @Override
    public T await(Duration duration) throws Exception {
        var shouldAwait = false;

        synchronized (future) {
            shouldAwait = !isCompleted();

            if (shouldAwait && awaiting == null) {
                awaiting = new CountDownLatch(1);
            }
        }

        if (shouldAwait) {
            try {
                var awaited = awaiting.await(duration.toMillis(), TimeUnit.MILLISECONDS);

                if (!isCompleted() || !awaited) {
                    throw new TimeoutException();
                }
            } catch (InterruptedException consumed) {
                // Ignored
            }
        }

        return future.get();
    }

    @Override
    public Promise<T> onSuccess(Consumer<T> consumer) {
        Objects.requireNonNull(consumer);

        final T value;

        synchronized (future) {
            if (isCompleted()) {
                try {
                    value = future.get();
                } catch (InterruptedException | ExecutionException e) {
                    return this;
                }
            } else {
                if (this.onSuccess == null) {
                    this.onSuccess = new ArrayList<>();
                }
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

        synchronized (future) {
            if (isCompleted()) {
                try {
                    this.future.get();
                    return this;
                } catch (InterruptedException e) {
                    value = e;
                } catch (ExecutionException e) {
                    value = e.getCause();
                }
            } else {
                if (this.onError == null) {
                    this.onError = new ArrayList<>();
                }
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
        synchronized (future) {
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

    private boolean isCompleted() {
        return future.isDone() || future.isCancelled();
    }

    private void notifyResponse(Try<T> response) {
        if (awaiting != null) {
            awaiting.countDown();
        }

        response.onSuccess(s -> {
            if (onSuccess != null) {
                onSuccess.forEach(c -> c.accept(s));
                onSuccess.clear();
            }
        }).onFailure(t -> {
            if (onError != null) {
                onError.forEach(c -> c.accept(t));
                onError.clear();
            }
        });
    }
}
