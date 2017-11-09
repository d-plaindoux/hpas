/*
 * HPAS
 * https://github.com/d-plaindoux/hpas
 *
 * Copyright (c) 2016-2017 Didier Plaindoux
 * Licensed under the LGPL2 license.
 */

package org.smallibs.type;

import java.util.function.Function;

public interface HK2<M, F, A, Self extends HK2<M, F, A, Self>> {

    <T> T accept(Function<HK2<M, F, A, Self>, T> f);

    Self self();

}
