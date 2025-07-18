/*
 * HPAS
 * https://github.com/d-plaindoux/hpas
 *
 * Copyright (c) 2016-2025 Didier Plaindoux
 * Licensed under the LGPL2 license.
 */

package org.smallibs.type;

import java.util.function.Function;

public interface HK<M, A, S extends HK<M, A, S>> {

    default <T> T apply(Function<HK<M, A, S>, T> f) {
        return f.apply(self());
    }

    S self();

}
