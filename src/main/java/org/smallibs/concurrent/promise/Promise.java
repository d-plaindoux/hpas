/*
 * HPAS
 * https://github.com/d-plaindoux/hpas
 *
 * Copyright (c) 2016-2017 Didier Plaindoux
 * Licensed under the LGPL2 license.
 */

package org.smallibs.concurrent.promise;

import org.smallibs.control.Filter;
import org.smallibs.data.Try;
import org.smallibs.type.HK;
import org.smallibs.util.FunctionWithError;

import java.util.concurrent.Future;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * A promise is a component denoting an asynchronous computation. Such component can be mapped in order to chain
 * transformations.
 */

public interface Promise<T> extends Filter<Promise, T, Promise<T>>, HK<Promise, T, Promise<T>> {

    /**
     * Constructor
     *
     * @param <T>
     * @param value The captured value
     * @return a solved promise
     */
    static <T> Promise<T> pure(T value) {
        return PromiseHelper.success(value);
    }

    /**
     * Provides the underlying future able to capture and returns the result or the error for a given execution
     *
     * @return a future
     */
    Future<T> getFuture();

    /**
     * Method called when the computation succeed
     *
     * @param consumer The callback to be activated on success
     * @return the current promise
     */
    Promise<T> onSuccess(Consumer<T> consumer);

    /**
     * Method called when the computation fails
     *
     * @param consumer The callback to be activated on error
     * @return the current promise
     */
    Promise<T> onFailure(Consumer<Throwable> consumer);

    /**
     * Callback called when the computation terminates
     *
     * @param consumer The callback to be activated on completion
     * @return the current promise
     */
    Promise<T> onComplete(Consumer<Try<T>> consumer);

    /**
     * Method used to map a function. This mapping is done when the operation is a success. The result of this mapping
     * is a new promise component.
     *
     * @param <R>      the promised value type
     * @param function The function to applied on success which can raise an error
     * @return a new promise
     */
    <R> Promise<R> map(FunctionWithError<? super T, ? extends R> function);

    /**
     * Method used when a new computation must be done when the current one succeed. The current one and the chained one
     * are done sequentially in the same context.
     *
     * @param <R>      the promised value type
     * @param function The function to applied on success
     * @return a new promise
     */
    default <R> Promise<R> and(FunctionWithError<? super T, R> function) {
        return this.map(function);
    }

    /**
     * Method used to flatmap a function. This mapping is done when the operation is a success. The result of this mapping
     * is a new promise component.
     *
     * @param <R>      the promised value type
     * @param function The function to applied on success
     * @return a new promise
     */
    <R> Promise<R> flatmap(Function<? super T, Promise<R>> function);

    /**
     * Method used when a new asynchronous computation must be done when the current one succeed. The current one and the
     * chained one are not done sequentially in the same context.
     *
     * @param <R>      the promised value type
     * @param function The function to applied on success
     * @return a new promise
     */
    default <R> Promise<R> then(Function<? super T, Promise<R>> function) {
        return this.flatmap(function);
    }

}
