package org.smallibs.concurrent.promise.impl;

import org.smallibs.concurrent.promise.Promise;
import org.smallibs.data.Try;
import org.smallibs.data.Unit;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

public class PromisesSet extends PassivePromise<Unit> {

    private final Strategy strategy;
    private final Promise<?>[] promises;
    private final AtomicInteger activePromises;

    public PromisesSet(Strategy strategy, Promise<?>... promises) {
        this.strategy = strategy;
        this.promises = promises;
        this.activePromises = new AtomicInteger(this.promises.length);

        if (this.promises.length == 0) {
            response(Try.success(Unit.unit));
            return;
        }

        Arrays.asList(this.promises).forEach(promise -> {
            promise.onSuccess(__ -> {
                activePromises.decrementAndGet();
                manageSuccess();
            });
            promise.onFailure(e -> {
                activePromises.decrementAndGet();
                manageError(e);
            });
        });

    }

    private final void manageSuccess() {
        switch (strategy) {
            case STOP_ON_SUCCESS:
            response(Try.success(Unit.unit));

            Arrays.asList(promises).forEach(promise -> {
                promise.getFuture().cancel(true);
            });
            break;
            default:
                if (activePromises.get() == 0) {
                    response(Try.success(Unit.unit));
                }
                break;
        }
    }

    private final void manageError(Throwable t) {
        switch (strategy) {
            case STOP_ON_ERROR:
            response(Try.failure(t));

            Arrays.asList(promises).forEach(promise -> {
                promise.getFuture().cancel(true);
            });
            break;
            case STOP_ON_SUCCESS:
                if (activePromises.get() == 0) {
                    response(Try.failure(t));
                }
                break;
            default:
                if (activePromises.get() == 0) {
                    response(Try.success(Unit.unit));
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
