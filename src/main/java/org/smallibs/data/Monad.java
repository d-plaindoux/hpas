package org.smallibs.data;

import java.util.function.Function;

public interface Monad<M, A> extends Applicative<M, A> {

    @Override
    <B> Monad<M, B> map(Function<? super A, B> function);

    <B> Monad<M, B> flatmap(Function<? super A, Monad<M, B>> function);

    /* Returns an instance of M<A> */
    <C extends Monad<M, A>> C concretize();

}
