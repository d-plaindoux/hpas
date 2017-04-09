/*
 * HPAS
 * https://github.com/d-plaindoux/hpas
 *
 * Copyright (c) 2016 Didier Plaindoux
 * Licensed under the LGPL2 license.
 */

package org.smallibs.control;

import org.smallibs.type.Kind;

import java.util.function.Function;

public interface Applicative<M, A, Self extends Kind<M, A, Self>> extends Functor<M, A, Self> {

    <B, NSelf extends Kind<M, B, NSelf>> Kind<M, B, NSelf> apply(Functor<M, Function<? super A, ? extends B>, ?> functor);

}
