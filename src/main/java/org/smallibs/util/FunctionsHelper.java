/*
 * HPAS
 * https://github.com/d-plaindoux/hpas
 *
 * Copyright (c) 2016-2017 Didier Plaindoux
 * Licensed under the LGPL2 license.
 */

package org.smallibs.util;

import org.smallibs.data.Try;

import java.util.function.Function;

public interface FunctionsHelper {

    static <T, R> FunctionWithError<T, R> fromFunction(Function<T, R> function) {
        return function::apply;
    }

    static <T, R> Function<T, Try<R>> toFunction(FunctionWithError<T, R> function) {
        return t -> {
            try {
                return Try.success(function.apply(t));
            } catch (Throwable throwable) {
                return Try.failure(throwable);
            }
        };
    }

}
