/*
 * HPAS
 * https://github.com/d-plaindoux/hpas
 *
 * Copyright (c) 2016 Didier Plaindoux
 * Licensed under the LGPL2 license.
 */

package org.smallibs.type;

import java.util.function.Function;

public interface TApp<M, A, Self extends TApp<M, A, Self>> {

    <T> T accept(Function<TApp<M, A, Self>, T> f);

    Self self();

}
