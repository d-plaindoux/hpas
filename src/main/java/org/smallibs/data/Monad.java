package org.smallibs.data;

import java.util.function.Function;

public interface Monad<M, A> extends Applicative<M, A> {

    @Override
    <B> Monad<M, B> map(Function<? super A, B> function);

    <B> Monad<M, B> flatmap(Function<? super A, Monad<M, B>> function);

    /**
     * This method is an helper used to downcast the current data to its concrete type. This method has not a
     * strong type as required since it MUST returns an <tt>M&lt;A></tt>. In Java High Order Type concept is a
     * missing feature this is why this <b>unsafe</b> coercion method is done for.
     *
     * @return an instance of M<A> (MUST)
     */
    <C extends Monad<M, A>> C concretize();

}
