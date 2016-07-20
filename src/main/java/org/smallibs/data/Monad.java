package org.smallibs.data;

import java.util.function.Function;

public interface Monad<M, A, Self extends TApp<M, A, Self>> extends Applicative<M, A, Self> {

    <B, NSelf extends TApp<M, B, NSelf>> TApp<M, B, NSelf> map(Function<? super A, B> function);

    <B, NSelf extends TApp<M, B, NSelf>> TApp<M, B, NSelf> flatmap(Function<? super A, TApp<M, B, NSelf>> function);

}
