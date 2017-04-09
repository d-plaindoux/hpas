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

public interface Monad<M, A, Self extends Kind<M, A, Self>> extends Applicative<M, A, Self> {

    <B, NSelf extends Kind<M, B, NSelf>> Kind<M, B, NSelf> flatmap(Function<? super A, Kind<M, B, NSelf>> function);

}
