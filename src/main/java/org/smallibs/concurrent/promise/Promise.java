/*
 * HPAS
 * https://github.com/d-plaindoux/hpas
 *
 * Copyright (c) 2016 Didier Plaindoux
 * Licensed under the LGPL2 license.
 */

package org.smallibs.concurrent.promise;

import org.smallibs.control.Filter;
import org.smallibs.data.Try;
import org.smallibs.type.TApp;
import org.smallibs.util.FunctionWithError;

import java.util.concurrent.Future;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * A promise is a component denoting an asynchronous computation. Such component can be mapped in order to chain
 * transformations.
 */

public interface Promise<T> extends Filter<Promise, T, Promise<T>>, TApp<Promise, T, Promise<T>> {

    /**
     * Provides the underlying future able to capture and returns the result or the error for a given execution
     *
     * @return a future
     */
    Future<T> getFuture();

    /**
     * Callback called when the computation succeed
     *
     * @param consumer The callback to be activated on success
     */
    void onSuccess(Consumer<T> consumer);

    /**
     * Callback called when the computation fails
     *
     * @param consumer The callback to be activated on error
     */
    void onFailure(Consumer<Throwable> consumer);

    /**
     * Callback called when the computation terminates
     *
     * @param consumer The callback to be activated on completion
     */
    void onComplete(Consumer<Try<T>> consumer);

    /**
     * Method use to map a function. This mapping is done when the operation is a success. The result of this mapping
     * is a new promise component.
     *
     * @param function The function to applied on success which can raise an error
     * @return a new promise
     */
    <R> Promise<R> map(FunctionWithError<? super T, R> function);

    /**
     * Method use when a new computation must be done when the current one succeed. The current one and the chained one
     * are done sequentially in the same context.
     *
     * @param function The function to applied on success
     * @return a new promise
     */
    default <R> Promise<R> and(FunctionWithError<? super T, R> function) {
        return this.map(function);
    }

    /**
     * Method use to flatmap a function. This mapping is done when the operation is a success. The result of this mapping
     * is a new promise component.
     *
     * @param function The function to applied on success
     * @return a new promise
     */
    <R> Promise<R> flatmap(Function<? super T, Promise<R>> function);

    /**
     * Method use when a new asynchronous computation must be done when the current one succeed. The current one and the
     * chained one are not done sequentially in the same context.
     *
     * @param function The function to applied on success
     * @return a new promise
     */
    default <R> Promise<R> then(Function<? super T, Promise<R>> function) {
        return this.flatmap(function);
    }
}
