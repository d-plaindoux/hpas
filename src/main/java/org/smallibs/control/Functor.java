/*
 * HPAS
 * https://github.com/d-plaindoux/hpas
 *
 * Copyright (c) 2016 Didier Plaindoux
 * Licensed under the LGPL2 license.
 */

package org.smallibs.control;

import org.smallibs.type.HoType;

import java.util.function.Function;

public interface Functor<M, A, Self extends HoType<M, A, Self>> extends HoType<M, A, Self> {

    <B, NSelf extends HoType<M, B, NSelf>> HoType<M, B, NSelf> map(Function<? super A, B> function);

}
