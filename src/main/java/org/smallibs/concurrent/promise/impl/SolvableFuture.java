/*
 * HPAS
 * https://github.com/d-plaindoux/hpas
 *
 * Copyright (c) 2016-2017 Didier Plaindoux
 * Licensed under the LGPL2 license.
 */

package org.smallibs.concurrent.promise.impl;

import org.smallibs.data.Try;

import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public class SolvableFuture<T> implements Future<T> {

    private final AtomicReference<Try<T>> responseReference;
    private final Consumer<Try<T>> callbackOnComplete;
    private volatile boolean canceled;

    public SolvableFuture(Consumer<Try<T>> callbackOnComplete) {
        this.callbackOnComplete = callbackOnComplete;
        this.responseReference = new AtomicReference<>();

        this.canceled = false;
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

    public void solve(final Try<T> response) {
        synchronized (this.responseReference) {
            if (this.isPerformed()) {
                return;
            }

            this.responseReference.set(response);
        }

        callbackOnComplete.accept(response);

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
        return responseReference.get().orElseThrow(t -> {
            if (t instanceof ExecutionException) {
                return (ExecutionException) t;
            } else {
                return new ExecutionException(t);
            }
        });
    }
}
