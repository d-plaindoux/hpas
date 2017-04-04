package org.smallibs.util;

@FunctionalInterface
public interface FunctionWithError<T, R> {
    R apply(T t) throws Throwable;
}
