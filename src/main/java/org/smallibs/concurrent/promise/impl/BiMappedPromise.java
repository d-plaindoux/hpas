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

final class BiMappedPromise<T, R> extends SolvablePromise<R> {

    BiMappedPromise(Promise<T> promise, FunctionWithError<? super T, ? extends R> onSuccess, FunctionWithError<? super Throwable, ? extends R> onError) {
        super();

        promise.onComplete(c -> solve(c.fold(
                        v -> {
                            try {
                                return Try.success(onSuccess.apply(v));
                            } catch (Throwable throwable) {
                                return Try.failure(throwable);
                            }
                        },
                        v -> {
                            try {
                                return Try.success(onError.apply(v));
                            } catch (Throwable throwable) {
                                return Try.failure(throwable);
                            }
                        })
                )
        );
    }
}
