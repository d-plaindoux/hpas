/*
 * HPAS
 * https://github.com/d-plaindoux/hpas
 *
 * Copyright (c) 2016-2017 Didier Plaindoux
 * Licensed under the LGPL2 license.
 */

package org.smallibs.util;

@FunctionalInterface
public interface FunctionWithError<T, R> {
    R apply(T t) throws Throwable;
}
