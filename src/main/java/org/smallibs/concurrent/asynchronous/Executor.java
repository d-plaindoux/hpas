package org.smallibs.concurrent.asynchronous;

import org.smallibs.concurrent.promise.Promise;
import org.smallibs.concurrent.promise.impl.RunnablePromise;

import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;

/**
 * Asynchronous execution media
 */
public final class Executor {

    /**
     * Underlying executor service
     */
    private final ExecutorService executorService;

    //
    // Class definition
    //

    /**
     * Constructor
     *
     * @param executorService The executor service used for asynchronous operation
     */
    private Executor(ExecutorService executorService) {
        this.executorService = executorService;
    }

    public static Executor create(ExecutorService executorService) {
        return new Executor(executorService);
    }

    /**
     * Async method
     *
     * @param task the task to be asynchronously executed
     * @return a promise
     */
    public <T> Promise<T> async(Callable<T> task) {
        Objects.requireNonNull(task);

        final RunnablePromise<T> runnablePromise = new RunnablePromise<>(task);
        executorService.execute(runnablePromise);
        return runnablePromise;
    }

    /**
     * Await method
     *
     * @param promise The promise to await for
     * @return a result or a runtime exception
     */
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
}
