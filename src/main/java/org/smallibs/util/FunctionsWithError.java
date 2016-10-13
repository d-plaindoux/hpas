package org.smallibs.util;

import org.smallibs.data.Try;

import java.util.function.Function;

public interface FunctionsWithError {

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
