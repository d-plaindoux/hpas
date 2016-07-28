/*
 * HPAS
 * https://github.com/d-plaindoux/hpas
 *
 * Copyright (c) 2016 Didier Plaindoux
 * Licensed under the LGPL2 license.
 */

package org.smallibs.data;

import org.smallibs.control.Filter;
import org.smallibs.control.Monad;
import org.smallibs.exception.FilterException;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public abstract class Try<T> implements Filter<Try, T>, TApp<Try, T, Try<T>> {

    private Try() {
    }

    public static <B> TApp<Try, B, Try<B>> specialize(TApp<Try, B, ?> app) {
        //noinspection unchecked
        return (TApp<Try, B, Try<B>>) app;
    }

    public static <B, Self extends TApp<Try, B, Self>> TApp<Try, B, Self> generalize(TApp<Try, B, Try<B>> app) {
        //noinspection unchecked
        return (TApp<Try, B, Self>) app;
    }

    public static <T> Try<T> success(T value) {
        return new Success<>(value);
    }

    public static <T> Try<T> failure(Throwable value) {
        return new Failure<>(value);
    }

    public Monad<Try, T, Try<T>> monad() {
        return new Monadic<>(this);
    }

    public Maybe<T> toMaybe() {
        final TApp<Try, Maybe<T>, ? extends Try<Maybe<T>>> map = this.map(Maybe::some);
        return map.self().recoverWith(Maybe.none());
    }

    public Try<T> filter(Predicate<? super T> predicate) {
        if (this.isSuccess() && predicate.test(this.success())) {
            return this;
        } else {
            return Try.failure(new FilterException());
        }
    }

    @Override
    public <R> R accept(Function<TApp<Try, T, Try<T>>, R> f) {
        return f.apply(this);
    }

    public <B> Try<B> map(Function<? super T, B> mapper) {
        if (this.isSuccess()) {
            return Try.success(mapper.apply(this.success()));
        } else {
            return Try.failure(this.failure());
        }
    }

    public <B> Try<B> flatmap(Function<? super T, Try<B>> mapper) {
        if (this.isSuccess()) {
            return mapper.apply(this.success());
        } else {
            return Try.failure(this.failure());
        }
    }

    public T recoverWith(T t) {
        if (this.isSuccess()) {
            return this.success();
        } else {
            return t;
        }
    }

    public Try<T> onSuccess(Consumer<T> onSuccess) {
        if (this.isSuccess()) {
            onSuccess.accept(this.success());
        }
        return this;
    }

    public Try<T> onFailure(Consumer<Throwable> onFailure) {
        if (!this.isSuccess()) {
            onFailure.accept(this.failure());
        }
        return this;
    }

    public T recoverWith(Function<Throwable, T> t) {
        if (this.isSuccess()) {
            return this.success();
        } else {
            return t.apply(this.failure());
        }
    }

    public <X extends Throwable> T orElseThrow(Supplier<? extends X> exceptionSupplier) throws X {
        if (this.isSuccess()) {
            return this.success();
        } else {
            throw exceptionSupplier.get();
        }
    }

    public <X extends Throwable> T orElseRetrieveAndThrow(Function<Throwable, ? extends X> exceptionSupplier) throws X {
        if (this.isSuccess()) {
            return this.success();
        } else {
            throw exceptionSupplier.apply(this.failure());
        }
    }

    public T orElseRetrieveAndThrow() throws Throwable {
        if (this.isSuccess()) {
            return this.success();
        } else {
            throw this.failure();
        }
    }

    abstract public boolean isSuccess();

    abstract public T success();

    abstract public Throwable failure();

    @Override
    public Try<T> self() {
        return this;
    }

    /**
     * Success implementation
     */
    private final static class Success<T> extends Try<T> {
        private final T value;

        private Success(T value) {
            this.value = value;
        }

        @Override
        public boolean isSuccess() {
            return true;
        }

        @Override
        public T success() {
            return value;
        }

        @Override
        public Throwable failure() {
            throw new IllegalAccessError();
        }
    }

    /**
     * Failure implementation
     */
    private final static class Failure<T> extends Try<T> {
        private final Throwable value;

        private Failure(Throwable value) {
            this.value = value;
        }

        @Override
        public boolean isSuccess() {
            return false;
        }

        @Override
        public T success() {
            throw new IllegalAccessError();
        }

        @Override
        public Throwable failure() {
            return value;
        }
    }


    /**
     * @param <T>
     */
    private final static class Monadic<T> implements Monad<Try, T, Try<T>> {
        private final Try<T> aTry;

        private Monadic(Try<T> aTry) {
            this.aTry = aTry;
        }

        @Override
        public <B, NSelf extends TApp<Try, B, NSelf>> TApp<Try, B, NSelf> map(Function<? super T, B> function) {
            return generalize(new Monadic<>(aTry.map(function)));
        }

        @Override
        public <B, NSelf extends TApp<Try, B, NSelf>> TApp<Try, B, NSelf> flatmap(Function<? super T, TApp<Try, B, NSelf>> function) {
            final Function<T, Try<B>> tTryFunction = t -> {
                final TApp<Try, B, NSelf> apply = function.apply(t);
                return Try.specialize(apply).self();
            };

            return generalize(new Monadic<>(aTry.flatmap(tTryFunction)));
        }

        @Override
        public <T1> T1 accept(Function<TApp<Try, T, Try<T>>, T1> f) {
            return aTry.accept(f);
        }

        @Override
        public Try<T> self() {
            return aTry;
        }
    }

}
