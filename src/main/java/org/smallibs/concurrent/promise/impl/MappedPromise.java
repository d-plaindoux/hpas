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
import org.smallibs.util.FunctionWithError;

import java.util.function.Function;

final class MappedPromise<T, R> extends SolvablePromise<R> {

    MappedPromise(Promise<T> promise, FunctionWithError<? super T, ? extends R> transform) {
        super();

        promise.onComplete(c ->
                solve(c.flatmap(v -> {
                    try {
                        return Try.success(transform.apply(v));
                    } catch (Throwable throwable) {
                        return Try.failure(throwable);
                    }
                }))
        );
    }
}
