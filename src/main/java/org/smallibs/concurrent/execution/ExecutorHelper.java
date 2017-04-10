/*
 * HPAS
 * https://github.com/d-plaindoux/hpas
 *
 * Copyright (c) 2016-2017 Didier Plaindoux
 * Licensed under the LGPL2 license.
 */

package org.smallibs.concurrent.execution;

import org.smallibs.concurrent.execution.impl.ExecutorImpl;
import org.smallibs.concurrent.promise.Promise;
import org.smallibs.data.Try;

import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Asynchronous execution builder
 */
public enum ExecutorHelper {
    ;

    /**
     * Factory
     *
     * @param executorService The underlying executr service
     * @return a new executor
     */
    public static Executor create(ExecutorService executorService) {
        return new ExecutorImpl(executorService);
    }

    /**
     * Await method
     *
     * @param promise The promise to await for
     * @return a result or a failure
     */
    public static <T> Try<T> await(Promise<T> promise) {
        Objects.requireNonNull(promise);

        try {
            return Try.success(promise.getFuture().get());
        } catch (InterruptedException e) {
            return Try.failure(e);
        } catch (ExecutionException e) {
            return Try.failure(e.getCause());
        }
    }

    /**
     * Await method for a given duration
     *
     * @param promise The promise to await for
     * @return a result or a runtime exception
     * @throws TimeoutException raised when no result is available after a given delay
     */
    public static <T> Try<T> await(Promise<T> promise, long duration, TimeUnit timeUnit) throws TimeoutException {
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
