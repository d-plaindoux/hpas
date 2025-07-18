/*
 * HPAS
 * https://github.com/d-plaindoux/hpas
 *
 * Copyright (c) 2016-2025 Didier Plaindoux
 * Licensed under the LGPL2 license.
 */

package org.smallibs.concurrent.promise.impl;

import org.smallibs.data.Try;

public final class SolvedPromise<T> extends SolvablePromise<T> {

    public SolvedPromise(Try<T> value) {
        this.solve(value);
    }

}
