/*
 * HPAS
 * https://github.com/d-plaindoux/hpas
 *
 * Copyright (c) 2016-2025 Didier Plaindoux
 * Licensed under the LGPL2 license.
 */

package org.smallibs.concurrent.execution;

import org.smallibs.concurrent.execution.impl.ExecutorImpl;
import org.smallibs.concurrent.promise.Promise;
import org.smallibs.data.Try;

import java.time.Duration;
import java.util.concurrent.ExecutorService;

/**
 * Asynchronous execution builder
 */
public enum ExecutorHelper {
    ;

    /**
     * Factory
     *
     * @param executorService The underlying executor service
     * @return a new executor
     */
    public static Executor create(ExecutorService executorService) {
        return new ExecutorImpl(executorService);
    }

    /**
     * Await method
     *
     * @param <T>      The promised value type
     * @param promise  The promise to await for
     * @param duration The wait duration before timeout
     * @return a result or a failure
     */
    public static <T> Try<T> await(Promise<T> promise, Duration duration) {
        return Try.handle(() -> promise.await(duration));
    }
}
