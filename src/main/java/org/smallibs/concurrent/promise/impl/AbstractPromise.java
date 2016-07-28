package org.smallibs.concurrent.promise.impl;

import org.smallibs.concurrent.promise.Promise;
import org.smallibs.control.Monad;
import org.smallibs.data.TApp;

import java.util.Objects;
import java.util.function.Function;

import static org.smallibs.concurrent.promise.Promise.generalize;

abstract class AbstractPromise<T> implements Promise<T> {

    AbstractPromise() {
    }

    @Override
    public Monad<Promise, T, Promise<T>> monad() {
        return new Monadic<>(this);
    }

    @Override
    public <R> R accept(Function<TApp<Promise, T, Promise<T>>, R> f) {
        return f.apply(this);
    }

    @Override
    public final <R> Promise<R> map(Function<? super T, R> function) {
        Objects.requireNonNull(function);

        return new MappedPromise<>(this, function);
    }

    @Override
    public final <R> Promise<R> flatmap(Function<? super T, Promise<R>> function) {
        Objects.requireNonNull(function);

        return new FlatMappedPromise<>(this, function);
    }

    @Override
    final public Promise<T> self() {
        return this;
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
                return Promise.specialize(apply).self();
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
