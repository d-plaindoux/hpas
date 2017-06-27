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
import java.util.concurrent.RunnableFuture;

public final class RunnablePromise<T> extends PassivePromise<T> implements RunnableFuture<T> {

    private final Callable<T> callable;

    private WeakReference<Thread> currentExecutor;

    public RunnablePromise(Callable<T> callable) {
        Objects.requireNonNull(callable);
        this.currentExecutor = new WeakReference<>(null);
        this.callable = callable;
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        if (mayInterruptIfRunning) {
            Maybe.some(this.currentExecutor.get()).onSome(Thread::interrupt);
        }

        return super.cancel(mayInterruptIfRunning);
    }

    @Override
    public void run() {
        this.currentExecutor = new WeakReference<>(Thread.currentThread());

        try {
            response(Try.success(this.callable.call()));
        } catch (final Throwable exception) {
            response(Try.failure(exception));
        }

        this.currentExecutor.clear();
    }
}
