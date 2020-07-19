/*
 * HPAS
 * https://github.com/d-plaindoux/hpas
 *
 * Copyright (c) 2016-2017 Didier Plaindoux
 * Licensed under the LGPL2 license.
 */

package org.smallibs.concurrent.execution;

import org.smallibs.concurrent.promise.Promise;
import org.smallibs.data.Unit;

import java.util.concurrent.Callable;

public interface Executor {

    @FunctionalInterface
    interface RunnableWithError {
        void run() throws Exception;
    }

    /**
     * Async method
     *
     * @param <T>  the promised value type
     * @param task the task to be asynchronously executed
     * @return a promise
     */
    <T> Promise<T> async(Callable<T> task);

    /**
     * Async method
     *
     * @param task the task to be asynchronously executed
     * @return a promise
     */
    Promise<Unit> async(RunnableWithError task);

}
