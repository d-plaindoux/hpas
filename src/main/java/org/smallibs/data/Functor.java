package org.smallibs.data;

import java.util.function.Function;

public interface Functor<M, A, Self extends TApp<M, A, Self>> extends TApp<M, A, Self> {

    <B, NSelf extends TApp<M, B, NSelf>> TApp<M, B, NSelf> map(Function<? super A, B> function);

}
