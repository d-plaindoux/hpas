package org.smallibs.util;

@FunctionalInterface
public interface SupplierWithError<O> {

    O get() throws Throwable;

}
