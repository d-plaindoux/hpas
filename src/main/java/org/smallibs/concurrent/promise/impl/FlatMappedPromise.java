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

import java.util.function.Function;

final class FlatMappedPromise<T, R> extends SolvablePromise<R> {

    FlatMappedPromise(Promise<T> promise, Function<? super T, Promise<R>> transform) {
        super();

        promise.onComplete(response ->
                response.onSuccess(s -> transform.apply(s).onComplete(this::solve))
                        .onFailure(f -> this.solve(Try.failure(f)))
        );
    }

}
