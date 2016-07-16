package org.smallibs.data;

public interface Applicative<M, A> extends Functor<M, A> {

    // TODO
    // <B> Applicative<M, B> apply(Applicative<M, Function<? super A, B>> function);

}
