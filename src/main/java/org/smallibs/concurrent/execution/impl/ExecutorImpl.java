/*
 * HPAS
 * https://github.com/d-plaindoux/hpas
 *
 * Copyright (c) 2016-2025 Didier Plaindoux
 * Licensed under the LGPL2 license.
 */

package org.smallibs.concurrent.execution.impl;

import org.smallibs.concurrent.execution.Executor;
import org.smallibs.concurrent.promise.Promise;
import org.smallibs.concurrent.promise.impl.RunnablePromise;
import org.smallibs.data.Unit;

import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

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

        this.executorService.execute(runnablePromise);
        return runnablePromise;
    }

    @Override
    public Promise<Unit> async(RunnableWithError task) {
        Objects.requireNonNull(task);

        final RunnablePromise<Unit> runnablePromise = new RunnablePromise<>(() -> {
            task.run();
            return Unit.unit;
        });

        this.executorService.execute(runnablePromise);
        return runnablePromise;
    }
}
