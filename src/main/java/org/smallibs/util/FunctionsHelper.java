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

public enum FunctionsHelper {
    ;

    public static <A, B, C> Function<A, C> compose(Function<B, C> f, Function<A, B> g) {
        return x -> f.apply(g.apply(x));
    }

    public static <A> Function<A, A> id() {
        return Function.identity();
    }

    public static <T, R> FunctionWithError<T, R> fromFunction(Function<T, R> function) {
        return function::apply;
    }

    public static <T, R> Function<T, Try<R>> toFunction(FunctionWithError<T, R> function) {
        return t -> {
            try {
                return Try.success(function.apply(t));
            } catch (Throwable throwable) {
                return Try.failure(throwable);
            }
        };
    }

}
