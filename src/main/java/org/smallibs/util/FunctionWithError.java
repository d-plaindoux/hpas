package org.smallibs.util;

@FunctionalInterface
public interface FunctionWithError<I, O> {

    O apply(I i) throws Throwable;

}
