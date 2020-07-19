/*
 * HPAS
 * https://github.com/d-plaindoux/hpas
 *
 * Copyright (c) 2016-2017 Didier Plaindoux
 * Licensed under the LGPL2 license.
 */

package org.smallibs.type;

import java.util.function.Function;

public interface HK<M, A, Self extends HK<M, A, Self>> {

    default <T> T apply(Function<HK<M, A, Self>, T> f) {
        return f.apply(self());
    }

    Self self();

}
