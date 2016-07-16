package org.smallibs.data;

import java.util.function.Function;

public interface Functor<M, A> {

    <B, Self extends TApp<M, B, Self>> TApp<M, B, Self> map(Function<? super A, B> function);

}
