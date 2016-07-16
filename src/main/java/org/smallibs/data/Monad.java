package org.smallibs.data;

import java.util.function.Function;

public interface Monad<M, A> extends Applicative<M, A> {

    <B, Self extends TApp<M, B, Self>> TApp<M, B, Self> map(Function<? super A, B> function);
    <B, Self extends TApp<M, B, Self>> TApp<M, B, Self> flatmap(Function<? super A, TApp<M, B, Self>> function);

}
