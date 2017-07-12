/*
 * HPAS
 * https://github.com/d-plaindoux/hpas
 *
 * Copyright (c) 2016-2017 Didier Plaindoux
 * Licensed under the LGPL2 license.
 */

package org.smallibs.data;

import org.smallibs.control.Filter;
import org.smallibs.exception.FilterException;
import org.smallibs.type.HK;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public interface Try<T> extends Filter<Try, T, Try<T>>, HK<Try, T, Try<T>> {

    static <T> Try<T> success(T value) {
        if (Throwable.class.isInstance(value)) {
            return new Failure<>(Throwable.class.cast(value));
        }

        return new Success<>(value);
    }

    static <T> Try<T> failure(Throwable value) {
        return new Failure<>(value);
    }

    @Override
    default Try<T> filter(Predicate<? super T> predicate) {
        return this.flatmap(t -> predicate.test(t) ? this : Try.failure(new FilterException()));
    }

    @Override
    default <R> R accept(Function<HK<Try, T, Try<T>>, R> f) {
        return f.apply(this);
    }

    @Override
    default Try<T> self() {
        return this;
    }

    default <B> Try<B> map(Function<? super T, ? extends B> mapper) {
        return this.flatmap(t -> success(mapper.apply(t)));
    }

    default <B> B fold(Function<? super T, B> success, Function<? super Throwable, B> failure) {
        return this.map(success).recoverWith(failure);
    }

    default T recoverWith(T t) {
        return this.recoverWith(x -> t);
    }

    default <X extends Throwable> T orElseThrow(Supplier<? extends X> exceptionSupplier) throws X {
        return this.orElseThrow(x -> exceptionSupplier.get());
    }

    default T orElseThrow() throws Throwable {
        return this.orElseThrow(x -> x);
    }

    default boolean isSuccess() {
        return this.fold(t -> true, f -> false);
    }

    T recoverWith(Function<? super Throwable, T> t);

    <X extends Throwable> T orElseThrow(Function<? super Throwable, ? extends X> exceptionSupplier) throws X;

    <B> Try<B> flatmap(Function<? super T, Try<B>> mapper);

    Try<T> onSuccess(Consumer<? super T> onSuccess);

    Try<T> onFailure(Consumer<? super Throwable> onFailure);

    /**
     * Success implementation
     */
    final class Success<T> implements Try<T> {
        private final T value;

        private Success(T value) {
            this.value = value;
        }

        @Override
        public <B> Try<B> flatmap(Function<? super T, Try<B>> mapper) {
            return mapper.apply(this.value);
        }

        @Override
        public Try<T> onSuccess(Consumer<? super T> onSuccess) {
            onSuccess.accept(this.value);
            return this;
        }

        @Override
        public Try<T> onFailure(Consumer<? super Throwable> onFailure) {
            return this;
        }

        @Override
        public <X extends Throwable> T orElseThrow(Function<? super Throwable, ? extends X> exceptionSupplier) throws X {
            return this.value;
        }

        @Override
        public T recoverWith(Function<? super Throwable, T> t) {
            return this.value;
        }
    }

    /**
     * Failure implementation
     */
    final class Failure<T> implements Try<T> {
        private final Throwable value;

        private Failure(Throwable value) {
            this.value = value;
        }

        @Override
        public <B> Try<B> flatmap(Function<? super T, Try<B>> mapper) {
            return Try.failure(this.value);
        }

        @Override
        public Try<T> onSuccess(Consumer<? super T> onSuccess) {
            return this;
        }

        @Override
        public Try<T> onFailure(Consumer<? super Throwable> onFailure) {
            onFailure.accept(this.value);
            return this;
        }

        @Override
        public <X extends Throwable> T orElseThrow(Function<? super Throwable, ? extends X> exceptionSupplier) throws X {
            throw exceptionSupplier.apply(this.value);
        }

        @Override
        public T recoverWith(Function<? super Throwable, T> t) {
            return t.apply(this.value);
        }

    }

}
