package org.smallibs.data;

/**
 * Free Monad
 *
 * @param <M>
 * @param <A>
 */
public abstract class Free<M, A> {

    private Free() {
    }

    public static <M, A> Free<M, A> point(A a) {
        return new Point<>(a);
    }

    public static <M, A, Self extends TApp<M, Free<M, A>, Self>> Free<M, A> join(TApp<M, Free<M, A>, Self> s) {
        return new Join<>(s);
    }

    private final static class Point<M, A> extends Free<M, A> {
        private final A a;

        private Point(A a) {
            this.a = a;
        }
    }

    private final static class Join<M, A, Self extends TApp<M, Free<M, A>, Self>> extends Free<M, A> {
        private final TApp<M, Free<M, A>, Self> s;

        private Join(TApp<M, Free<M, A>, Self> s) {
            this.s = s;
        }
    }
}
