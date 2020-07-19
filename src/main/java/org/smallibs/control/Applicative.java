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

public interface Applicative<M, A, S extends HK<M, A, S>> extends Functor<M, A, S> {

    <B, NS extends HK<M, B, NS>> HK<M, B, NS> apply(Functor<M, Function<? super A, ? extends B>, ?> functor);

}
