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
import org.smallibs.data.Unit;
import org.smallibs.exception.NoValueException;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

public class PromisesSet extends SolvablePromise<Unit> {

    private final Strategy strategy;
    private final Promise<?>[] promises;
    private final AtomicInteger activePromises;

    public PromisesSet(Strategy strategy, Promise... promises) {

        this.strategy = strategy;
        this.promises = promises;
        this.activePromises = new AtomicInteger(this.promises.length);

        if (this.promises.length == 0) {
            switch (strategy) {
                case NO_STOP:
                case STOP_ON_ERROR:
                    solve(Try.success(Unit.unit));
                    break;
                case STOP_ON_SUCCESS:
                    solve(Try.failure(new NoValueException()));
                    break;
                default:
                    throw new IllegalArgumentException();
            }
            return;
        }

        Arrays.asList(this.promises).forEach(promise -> {
            promise.onSuccess(t -> {
                activePromises.decrementAndGet();
                manageSuccess();
            });
            promise.onFailure(e -> {
                activePromises.decrementAndGet();
                manageError(e);
            });
        });

    }

    private void manageSuccess() {
        if (strategy == Strategy.STOP_ON_SUCCESS) {
            solve(Try.success(Unit.unit));

            Arrays.asList(promises).forEach(promise -> {
                promise.getFuture().cancel(true);
            });
        } else {
            if (activePromises.get() == 0) {
                solve(Try.success(Unit.unit));
            }
        }
    }

    private void manageError(Throwable t) {
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
                    solve(Try.success(Unit.unit));
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
