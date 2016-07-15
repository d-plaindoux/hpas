package org.smallibs.data;

import java.util.function.Function;

public interface Functor<M, A> {

    <B> Functor<M, B> map(Function<? super A, B> function);

}
