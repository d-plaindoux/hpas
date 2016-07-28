/*
 * HPAS
 * https://github.com/d-plaindoux/hpas
 *
 * Copyright (c) 2016 Didier Plaindoux
 * Licensed under the LGPL2 license.
 */

package org.smallibs.data;

import java.util.function.Function;

public interface Functor<M, A, Self extends TApp<M, A, Self>> extends TApp<M, A, Self> {

    <B, NSelf extends TApp<M, B, NSelf>> TApp<M, B, NSelf> map(Function<? super A, B> function);

}
