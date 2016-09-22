package org.smallibs.util;

public interface FunctionWithError<T, R> {
    R apply(T t) throws Throwable;
}
