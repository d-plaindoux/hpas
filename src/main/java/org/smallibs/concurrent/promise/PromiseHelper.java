package org.smallibs.concurrent.promise;

import org.smallibs.control.Monad;
import org.smallibs.type.TApp;

import java.util.function.Function;

public class PromiseHelper {

    private PromiseHelper() {
    }

    public static <B> Promise<B> specialize(TApp<Promise, B, ?> app) {
        return ((TApp<Promise, B, Promise<B>>) app).self();
    }

    public static <B, Self extends TApp<Promise, B, Self>> TApp<Promise, B, Self> generalize(TApp<Promise, B, Promise<B>> app) {
        return (TApp<Promise, B, Self>) app;
    }

    public static <T> Monad<Promise, T, Promise<T>> monad(Promise<T> promise) {
        return new Monadic<>(promise);
    }

    /**
     * @param <T>
     */
    private final static class Monadic<T> implements Monad<Promise, T, Promise<T>> {
        private final Promise<T> promise;

        Monadic(Promise<T> promise) {
            this.promise = promise;
        }

        @Override
        public <B, NSelf extends TApp<Promise, B, NSelf>> TApp<Promise, B, NSelf> map(Function<? super T, B> function) {
            return generalize(new Monadic<>(promise.map(function)));
        }

        @Override
        public <B, NSelf extends TApp<Promise, B, NSelf>> TApp<Promise, B, NSelf> flatmap(Function<? super T, TApp<Promise, B, NSelf>> function) {
            final Function<T, Promise<B>> tPromiseFunction = t -> {
                final TApp<Promise, B, NSelf> apply = function.apply(t);
                return PromiseHelper.specialize(apply).self();
            };
            return generalize(new Monadic<>(promise.flatmap(tPromiseFunction)));
        }

        @Override
        public <T1> T1 accept(Function<TApp<Promise, T, Promise<T>>, T1> f) {
            return promise.accept(f);
        }

        @Override
        public Promise<T> self() {
            return promise;
        }
    }

}
