package org.smallibs.data;

@FunctionalInterface
public interface Transformer<M, A, Self extends TApp<M, A, Self>> {

    <N, NSelf extends TApp<N, A, NSelf>> TApp<N, A, NSelf> transform(TApp<N, A, NSelf> function);

}
