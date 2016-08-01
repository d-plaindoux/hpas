/*
 * HPAS
 * https://github.com/d-plaindoux/hpas
 *
 * Copyright (c) 2016 Didier Plaindoux
 * Licensed under the LGPL2 license.
 */

package org.smallibs.control;

import org.smallibs.type.TApp;

import java.util.function.Predicate;

public interface Filter<M, A, Self extends TApp<M, A, Self>> extends TApp<M, A, Self> {

    TApp<M, A, Self> filter(Predicate<? super A> predicate);

}
