/*
 * HPAS
 * https://github.com/d-plaindoux/hpas
 *
 * Copyright (c) 2016-2017 Didier Plaindoux
 * Licensed under the LGPL2 license.
 */

package org.smallibs.control;

import org.smallibs.type.HK;

public interface Free<F extends Functor<F, A, Free<F, A>>, A> extends HK<F, A, Free<F, A>> {
    // TODO
}
