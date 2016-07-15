package org.smallibs.data;

import java.util.function.Predicate;

public interface Selectable<M, A> {

    Selectable<M, A> filter(Predicate<? super A> predicate);


}
