/*
 * HPAS
 * https://github.com/d-plaindoux/hpas
 *
 * Copyright (c) 2016 Didier Plaindoux
 * Licensed under the LGPL2 license.
 */

package org.smallibs.control;

import java.util.function.Predicate;

public interface Filter<M, A> {

    Filter<M, A> filter(Predicate<? super A> predicate);


}
