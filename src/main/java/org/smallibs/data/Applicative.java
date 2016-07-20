package org.smallibs.data;

public interface Applicative<M, A, Self extends TApp<M, A, Self>> extends Functor<M, A, Self> {

    /*
    default <B> Applicative<M, B> apply(Applicative<M, Function<? super A, B>> function) {
        throw new IllegalAccessError(); // Not yet implemented
    }
    */

}
