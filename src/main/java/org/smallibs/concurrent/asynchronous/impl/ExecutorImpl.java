package org.smallibs.concurrent.asynchronous.impl;

import org.smallibs.concurrent.asynchronous.Executor;
import org.smallibs.concurrent.promise.Promise;
import org.smallibs.concurrent.promise.impl.RunnablePromise;

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
    public <T> T await(Promise<T> promise) {
        Objects.requireNonNull(promise);

        try {
            return promise.getFuture().get();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e.getCause());
        }
    }

    @Override
    public <T> T await(Promise<T> promise, long duration, TimeUnit timeUnit) throws TimeoutException {
        Objects.requireNonNull(promise);

        try {
            return promise.getFuture().get(duration, timeUnit);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e.getCause());
        }
    }
}
