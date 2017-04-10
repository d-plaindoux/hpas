/*
 * HPAS
 * https://github.com/d-plaindoux/hpas
 *
 * Copyright (c) 2016-2017 Didier Plaindoux
 * Licensed under the LGPL2 license.
 */

package org.smallibs.control;

import org.smallibs.type.Kind;

import java.util.function.Function;

public interface Functor<M, A, Self extends Kind<M, A, Self>> extends Kind<M, A, Self> {

    <B, NSelf extends Kind<M, B, NSelf>> Kind<M, B, NSelf> map(Function<? super A, B> function);

}
