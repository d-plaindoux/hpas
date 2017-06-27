/*
 * HPAS
 * https://github.com/d-plaindoux/hpas
 *
 * Copyright (c) 2016 Didier Plaindoux
 * Licensed under the LGPL2 license.
 */

package org.smallibs.concurrent.promise.impl;

import org.smallibs.data.Maybe;
import org.smallibs.data.Try;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public class PassivePromise<T> extends AbstractPromise<T> implements Future<T> {

    private final AtomicReference<Try<T>> responseReference;
    private final List<Consumer<T>> onSuccess;
    private final List<Consumer<Throwable>> onError;

    private volatile boolean canceled;

    public PassivePromise() {
        this.responseReference = new AtomicReference<>();

        this.canceled = false;

        this.onSuccess = new ArrayList<>();
        this.onError = new ArrayList<>();
    }

    @Override
    public Future<T> getFuture() {
        return this;
    }

    @Override
    public void onSuccess(Consumer<T> consumer) {
        Objects.requireNonNull(consumer);

        final AtomicReference<T> success = new AtomicReference<>();

        synchronized (this.responseReference) {
            if (isPerformed()) {
                this.responseReference.get().onSuccess(success::set);
            } else {
                this.onSuccess.add(consumer);
            }
        }

        Maybe.some(success.get()).onSome(consumer);
    }

    @Override
    public void onFailure(Consumer<Throwable> consumer) {
        Objects.requireNonNull(consumer);

        final AtomicReference<Throwable> failure = new AtomicReference<>();

        synchronized (this.responseReference) {
            if (isPerformed()) {
                this.responseReference.get().onFailure(failure::set);
            } else {
                this.onError.add(consumer);
            }
        }

        Maybe.some(failure.get()).onSome(consumer);
    }

    @Override
    public void onComplete(final Consumer<Try<T>> consumer) {
        Objects.requireNonNull(consumer);

        this.onSuccess(t -> consumer.accept(Try.success(t)));
        this.onFailure(throwable -> consumer.accept(Try.failure(throwable)));
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        synchronized (this.responseReference) {
            if (isPerformed()) {
                return false;
            }

            this.responseReference.set(Try.failure(new CancellationException()));
            this.canceled = true;

            return true;
        }
    }

    @Override
    public boolean isCancelled() {
        return this.canceled;
    }

    @Override
    public boolean isDone() {
        return this.responseReference.get() != null;
    }

    @Override
    public T get() throws InterruptedException, ExecutionException {
        synchronized (responseReference) {
            if (responseReference.get() == null) {
                responseReference.wait();
            }
        }

        return getNow();
    }

    @Override
    public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        synchronized (responseReference) {
            if (responseReference.get() == null) {
                responseReference.wait(unit.toMillis(timeout));
            }

            if (responseReference.get() == null) {
                throw new TimeoutException();
            }
        }

        return getNow();
    }

    public void response(final Try<T> response) {
        synchronized (this.responseReference) {
            if (this.isCancelled()) {
                return;
            }

            this.responseReference.set(response);
        }

        response.onSuccess(s -> {
            onSuccess.forEach(c -> c.accept(s));
            onSuccess.clear();
        }).onFailure(t -> {
            onError.forEach(c -> c.accept(t));
            onError.clear();
        });

        synchronized (this.responseReference) {
            this.responseReference.notifyAll();
        }
    }

    //
    // Private behaviors
    //

    private boolean isPerformed() {
        return this.isDone() || this.isCancelled();
    }

    private T getNow() throws ExecutionException {
        return responseReference.get().orElseThrow(t -> new ExecutionException(t));
    }
}
