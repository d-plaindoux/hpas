/*
 * HPAS
 * https://github.com/d-plaindoux/hpas
 *
 * Copyright (c) 2016 Didier Plaindoux
 * Licensed under the LGPL2 license.
 */

package org.smallibs.control;

import org.smallibs.type.TApp;

import java.util.function.Function;

public interface Applicative<M, A, Self extends TApp<M, A, Self>> extends Functor<M, A, Self> {

    <B, NSelf extends TApp<M, B, NSelf>> TApp<M, B, NSelf> apply(Functor<M, Function<? super A, ? extends B>, ?> functor);

}
