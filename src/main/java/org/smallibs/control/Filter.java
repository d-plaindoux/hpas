/*
 * HPAS
 * https://github.com/d-plaindoux/hpas
 *
 * Copyright (c) 2016-2017 Didier Plaindoux
 * Licensed under the LGPL2 license.
 */

package org.smallibs.control;

import org.smallibs.type.HK;

import java.util.function.Predicate;

/**
 * Filter interface
 */
public interface Filter<M, A, Self extends HK<M, A, Self>> {

    /**
     * Method called when the current data must be filtered using a given predicate.
     *
     * @param predicate The filter
     * @return a filtered data
     */
    HK<M, A, Self> filter(Predicate<? super A> predicate);

}
