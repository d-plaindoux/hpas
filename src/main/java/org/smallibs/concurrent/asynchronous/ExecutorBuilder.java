package org.smallibs.concurrent.asynchronous;

import org.smallibs.concurrent.asynchronous.impl.ExecutorImpl;

import java.util.concurrent.ExecutorService;

/**
 * Asynchronous execution builder
 */
public enum ExecutorBuilder {
    $;

    /**
     * Factory
     *
     * @param executorService The underlying executr service
     * @return a new executor
     */
    public static Executor create(ExecutorService executorService) {
        return new ExecutorImpl(executorService);
    }

}
