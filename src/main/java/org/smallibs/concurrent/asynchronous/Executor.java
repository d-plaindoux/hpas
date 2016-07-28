/*
 * HPAS
 * https://github.com/d-plaindoux/hpas
 *
 * Copyright (c) 2016 Didier Plaindoux
 * Licensed under the LGPL2 license.
 */

package org.smallibs.concurrent.asynchronous;

import org.smallibs.concurrent.promise.Promise;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public interface Executor {

    /**
     * Async method
     *
     * @param task the task to be asynchronously executed
     * @return a promise
     */
    <T> Promise<T> async(Callable<T> task);

    /**
     * Await method
     *
     * @param promise The promise to await for
     * @return a result or a runtime exception
     */
    <T> T await(Promise<T> promise);

    /**
     * Await method for a given duration
     *
     * @param promise The promise to await for
     * @return a result or a runtime exception
     * @throws TimeoutException raised when no result is available after a given delay
     */
    <T> T await(Promise<T> promise, long duration, TimeUnit timeUnit) throws TimeoutException;
}
