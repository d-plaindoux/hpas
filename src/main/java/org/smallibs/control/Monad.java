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

public interface Monad<M, A, S extends HK<M, A, S>> extends Applicative<M, A, S> {

    <B, NS extends HK<M, B, NS>> HK<M, B, NS> flatmap(Function<? super A, HK<M, B, NS>> function);

}
