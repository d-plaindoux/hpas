package org.smallibs.concurrent.promise;

import org.smallibs.data.Monad;
import org.smallibs.data.Try;

import java.util.concurrent.Future;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * A promise is a component denoting an asynchronous computation. Such component can be mapped in order to chain
 * transformations.
 */

public interface Promise<T> extends Monad<Promise, T> {

    @Override
    @SuppressWarnings("unchecked")
    Promise<T> concretize();

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
     * @param function The function to applied on success
     * @return a new promise
     */
    <R> Promise<R> map(Function<? super T, R> function);

    /**
     * Method use to flatmap a function. This mapping is done when the operation is a success. The result of this mapping
     * is a new promise component.
     *
     * @param function The function to applied on success
     * @return a new promise
     */
    <R> Promise<R> flatmap(Function<? super T, Monad<Promise, R>> function);

}
