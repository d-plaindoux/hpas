package org.smallibs.data;

import java.util.function.Function;

public interface Applicative<M, A> extends Functor<M, A> {

    @Override
    <B> Applicative<M, B> map(Function<? super A, B> function);

    // TODO
    // <B> Applicative<M, B> apply(Applicative<M, Function<? super A, B>> function);

}
