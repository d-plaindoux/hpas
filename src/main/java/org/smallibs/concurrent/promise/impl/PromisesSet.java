/*
 * HPAS
 * https://github.com/d-plaindoux/hpas
 *
 * Copyright (c) 2016-2017 Didier Plaindoux
 * Licensed under the LGPL2 license.
 */

package org.smallibs.concurrent.promise.impl;

import org.smallibs.concurrent.promise.Promise;
import org.smallibs.data.Try;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class PromisesSet<T> extends SolvablePromise<List<T>> {

    private final Strategy strategy;
    private final Promise<T>[] promises;
    private final AtomicInteger activePromises;
    private final List<T> collectedResult;

    public PromisesSet(Strategy strategy, Promise<T>... promises) {
        this.strategy = strategy;
        this.promises = promises;
        this.activePromises = new AtomicInteger(this.promises.length);
        this.collectedResult = new ArrayList<>();

        if (this.promises.length == 0) {
            solve(Try.success(this.collectedResult));
            return;
        }

        Arrays.asList(this.promises).forEach(promise -> {
            promise.onSuccess(t -> {
                activePromises.decrementAndGet();
                manageSuccess(t);
            });
            promise.onFailure(e -> {
                activePromises.decrementAndGet();
                manageError(e);
            });
        });

    }

    private final void manageSuccess(T t) {
        this.collectedResult.add(t);

        switch (strategy) {
            case STOP_ON_SUCCESS:
                solve(Try.success(this.collectedResult));

                Arrays.asList(promises).forEach(promise -> {
                    promise.getFuture().cancel(true);
                });
                break;
            default:
                if (activePromises.get() == 0) {
                    solve(Try.success(this.collectedResult));
                }
                break;
        }
    }

    private final void manageError(Throwable t) {
        switch (strategy) {
            case STOP_ON_ERROR:
                solve(Try.failure(t));

                Arrays.asList(promises).forEach(promise -> {
                    promise.getFuture().cancel(true);
                });
                break;
            case STOP_ON_SUCCESS:
                if (activePromises.get() == 0) {
                    solve(Try.failure(t));
                }
                break;
            default:
                if (activePromises.get() == 0) {
                    solve(Try.success(this.collectedResult));
                }
                break;
        }
    }

    public enum Strategy {
        NO_STOP,
        STOP_ON_ERROR,
        STOP_ON_SUCCESS
    }
}
