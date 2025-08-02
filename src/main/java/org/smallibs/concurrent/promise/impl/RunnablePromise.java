/*
 * HPAS
 * https://github.com/d-plaindoux/hpas
 *
 * Copyright (c) 2016-2025 Didier Plaindoux
 * Licensed under the LGPL2 license.
 */

package org.smallibs.concurrent.promise.impl;

import org.smallibs.data.Maybe;
import org.smallibs.data.Try;

import java.lang.ref.WeakReference;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.function.Consumer;

public final class RunnablePromise<T> extends SolvablePromise<T> implements Runnable {

    private final Callable<T> callable;

    private WeakReference<Thread> currentExecutor;

    public RunnablePromise(Callable<T> callable) {
        Objects.requireNonNull(callable);
        this.currentExecutor = new WeakReference<>(null);
        this.callable = callable;
    }

    @Override
    public void run() {
        this.currentExecutor = new WeakReference<>(Thread.currentThread());

        try {
            solve(Try.success(this.callable.call()));
        } catch (final Throwable exception) {
            solve(Try.failure(exception));
        }

        this.currentExecutor.clear();
    }

    //
    // Protected behaviors
    //

    @Override
    protected SolvableFuture<T> createFuture(Consumer<Try<T>> callbackOnComplete) {
        return new SolvableFuture<>(callbackOnComplete) {
            @Override
            public boolean cancel(boolean mayInterruptIfRunning) {
                if (mayInterruptIfRunning) {
                    Maybe.some(currentExecutor.get()).onSome(Thread::interrupt);
                }

                return super.cancel(mayInterruptIfRunning);
            }
        };
    }
}
