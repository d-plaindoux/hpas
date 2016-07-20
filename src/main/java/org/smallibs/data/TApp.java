package org.smallibs.data;

import java.util.function.Function;

public interface TApp<M, A, Self extends TApp<M, A, Self>> {

    <T> T accept(Function<TApp<M, A, Self>, T> f);

    Self self();

}
