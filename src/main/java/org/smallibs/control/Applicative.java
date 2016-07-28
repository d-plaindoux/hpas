/*
 * HPAS
 * https://github.com/d-plaindoux/hpas
 *
 * Copyright (c) 2016 Didier Plaindoux
 * Licensed under the LGPL2 license.
 */

package org.smallibs.control;

import org.smallibs.data.TApp;

public interface Applicative<M, A, Self extends TApp<M, A, Self>> extends Functor<M, A, Self> {

    /*
    default <B> Applicative<M, B> apply(Applicative<M, Function<? super A, B>> function) {
        throw new IllegalAccessError(); // Not yet implemented
    }
    */

}
