package org.smallibs.data;

public interface Applicative<M, A> extends Functor<M, A> {

    /*
    default <B> Applicative<M, B> apply(Applicative<M, Function<? super A, B>> function) {
        throw new IllegalAccessError(); // Not yet implemented
    }
    */

}
