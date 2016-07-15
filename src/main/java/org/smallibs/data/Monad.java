package org.smallibs.data;

import java.util.function.Function;

public interface Monad<M, A> {

    <B> Monad<M, B> map(Function<? super A, B> function);

    <B> Monad<M, B> flatmap(Function<? super A, Monad<M, B>> function);

    <C extends Monad<M, A>> C concretize();

}
