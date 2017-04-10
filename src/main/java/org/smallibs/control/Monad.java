/*
 * HPAS
 * https://github.com/d-plaindoux/hpas
 *
 * Copyright (c) 2016-2017 Didier Plaindoux
 * Licensed under the LGPL2 license.
 */

package org.smallibs.control;

import org.smallibs.type.HK;

import java.util.function.Function;

public interface Monad<M, A, Self extends HK<M, A, Self>> extends Applicative<M, A, Self> {

    <B, NSelf extends HK<M, B, NSelf>> HK<M, B, NSelf> flatmap(Function<? super A, HK<M, B, NSelf>> function);

}
