/*
 * HPAS
 * https://github.com/d-plaindoux/hpas
 *
 * Copyright (c) 2016 Didier Plaindoux
 * Licensed under the LGPL2 license.
 */

package org.smallibs.concurrent.asynchronous.impl;

import org.smallibs.concurrent.asynchronous.Executor;
import org.smallibs.concurrent.promise.Promise;
import org.smallibs.concurrent.promise.impl.RunnablePromise;
import org.smallibs.data.Try;

import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Asynchronous execution media
 */
public final class ExecutorImpl implements Executor {

    /**
     * Underlying executor service
     */
    private final ExecutorService executorService;

    /**
     * Constructor
     *
     * @param executorService The executor service used for asynchronous operation
     */
    public ExecutorImpl(ExecutorService executorService) {
        this.executorService = executorService;
    }

    @Override
    public <T> Promise<T> async(Callable<T> task) {
        Objects.requireNonNull(task);

        final RunnablePromise<T> runnablePromise = new RunnablePromise<>(task);
        executorService.execute(runnablePromise);
        return runnablePromise;
    }

    @Override
    public <T> Try<T> await(Promise<T> promise) {
        Objects.requireNonNull(promise);

        try {
            return Try.success(promise.getFuture().get());
        } catch (InterruptedException e) {
            return Try.failure(e);
        } catch (ExecutionException e) {
            return Try.failure(e.getCause());
        }
    }

    @Override
    public <T> Try<T> await(Promise<T> promise, long duration, TimeUnit timeUnit) throws TimeoutException {
        Objects.requireNonNull(promise);

        try {
            return Try.success(promise.getFuture().get(duration, timeUnit));
        } catch (InterruptedException e) {
            return Try.failure(e);
        } catch (ExecutionException e) {
            return Try.failure(e.getCause());
        }
    }
}
