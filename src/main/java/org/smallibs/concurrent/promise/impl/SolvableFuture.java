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

    private enum Status {
        WAITING, SOLVED, CANCELLED
    }

    private final AtomicReference<Try<T>> responseReference;
    private final Consumer<Try<T>> callbackOnComplete;
    private final AtomicReference<Status> status;

    public SolvableFuture(Consumer<Try<T>> callbackOnComplete) {
        this.callbackOnComplete = callbackOnComplete;
        this.responseReference = new AtomicReference<>();
        this.status = new AtomicReference<>(Status.WAITING);
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        synchronized (this.responseReference) {
            final boolean isCancelled = this.status.compareAndSet(Status.WAITING, Status.CANCELLED);

            if (isCancelled) {
                this.responseReference.set(Try.failure(new CancellationException()));
            }

            return isCancelled;
        }
    }

    @Override
    public boolean isCancelled() {
        return this.status.get() == Status.CANCELLED;
    }

    @Override
    public boolean isDone() {
        return this.status.get() == Status.SOLVED;
    }

    @Override
    public T get() throws InterruptedException, ExecutionException {
        synchronized (responseReference) {
            if (this.status.get() == Status.WAITING) {
                this.responseReference.wait();
            }
        }

        return getNow();
    }

    @Override
    public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        synchronized (responseReference) {
            if (this.status.get() == Status.WAITING) {
                this.responseReference.wait(unit.toMillis(timeout));
            }

            if (this.status.get() == Status.WAITING) {
                throw new TimeoutException();
            }
        }

        return getNow();
    }

    public void solve(final Try<T> response) {
        synchronized (this.responseReference) {
            final boolean isSolved = this.status.compareAndSet(Status.WAITING, Status.SOLVED);

            if (isSolved) {
                this.responseReference.set(response);
            }
        }

        this.callbackOnComplete.accept(response);

        synchronized (this.responseReference) {
            this.responseReference.notifyAll();
        }
    }

    //
    // Private behaviors
    //

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
