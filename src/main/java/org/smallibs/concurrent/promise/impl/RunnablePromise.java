/*
 * HPAS
 * https://github.com/d-plaindoux/hpas
 *
 * Copyright (c) 2016-2017 Didier Plaindoux
 * Licensed under the LGPL2 license.
 */

package org.smallibs.concurrent.promise.impl;

import org.smallibs.data.Maybe;
import org.smallibs.data.Try;

import java.lang.ref.WeakReference;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public final class RunnablePromise<T> extends AbstractPromise<T> implements RunnableFuture<T> {

    private final Callable<T> callable;
    private final AtomicReference<Try<T>> responseReference;

    private WeakReference<Thread> currentExecutor;
    private volatile boolean canceled;

    private Consumer<T> onSuccess;
    private Consumer<Throwable> onError;

    public RunnablePromise(Callable<T> callable) {
        Objects.requireNonNull(callable);

        this.callable = callable;
        this.responseReference = new AtomicReference<>();

        this.currentExecutor = new WeakReference<>(null);
        this.canceled = false;

        this.onSuccess = s -> {
        };
        this.onError = e -> {
        };
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
                this.onSuccess = consumer;
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
                this.onError = consumer;
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

            if (mayInterruptIfRunning) {
                Maybe.some(this.currentExecutor.get()).onSome(Thread::interrupt);
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

    @Override
    public void run() {
        this.currentExecutor = new WeakReference<>(Thread.currentThread());

        try {
            manageResponse(Try.success(this.callable.call()));
        } catch (final Throwable exception) {
            manageResponse(Try.failure(exception));
        }

        this.currentExecutor.clear();
    }

    //
    // Private behaviors
    //

    private boolean isPerformed() {
        return this.isDone() || this.isCancelled();
    }

    private void manageResponse(final Try<T> response) {
        synchronized (this.responseReference) {
            if (this.isCancelled()) {
                return;
            }

            this.responseReference.set(response);
        }

        response.onSuccess(s -> onSuccess.accept(s)).
                onFailure(t -> onError.accept(t));

        synchronized (this.responseReference) {
            this.responseReference.notifyAll();
        }
    }

    private T getNow() throws ExecutionException {
        return responseReference.get().orElseThrow(t -> new ExecutionException(t));
    }
}
