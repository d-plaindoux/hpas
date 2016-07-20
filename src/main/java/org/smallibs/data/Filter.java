package org.smallibs.data;

import java.util.function.Predicate;

public interface Filter<M, A> {

    Filter<M, A> filter(Predicate<? super A> predicate);


}
