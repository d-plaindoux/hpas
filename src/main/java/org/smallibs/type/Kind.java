/*
 * HPAS
 * https://github.com/d-plaindoux/hpas
 *
 * Copyright (c) 2016 Didier Plaindoux
 * Licensed under the LGPL2 license.
 */

package org.smallibs.type;

import java.util.function.Function;

public interface Kind<M, A, Self extends Kind<M, A, Self>> {

    <T> T accept(Function<Kind<M, A, Self>, T> f);

    Self self();

}
