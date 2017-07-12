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

public interface Functor<M, A, Self extends HK<M, A, Self>> extends HK<M, A, Self> {

    <B, NSelf extends HK<M, B, NSelf>> HK<M, B, NSelf> map(Function<? super A, ? extends B> function);

}
