/*
 * HPAS
 * https://github.com/d-plaindoux/hpas
 *
 * Copyright (c) 2016-2025 Didier Plaindoux
 * Licensed under the LGPL2 license.
 */

package org.smallibs.data;

import org.smallibs.concurrent.promise.Promise;
import org.smallibs.concurrent.promise.PromiseHelper;
import org.smallibs.control.Filter;
import org.smallibs.exception.FilterException;
import org.smallibs.type.HK;
import org.smallibs.util.SupplierWithError;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

@SuppressWarnings("rawtypes")
public sealed interface Try<T> extends Filter<Try, T, Try<T>>, HK<Try, T, Try<T>> {

    static <O> Try<O> handle(SupplierWithError<O> supplier) {
        try {
            return Try.pure(supplier.get());
        } catch (Throwable e) {
            return Try.failure(e);
        }
    }

    default Promise<T> toPromise() {
        return switch (this) {
            case Try.Success<T> v -> PromiseHelper.success(v.value);
            case Try.Failure<T> v -> PromiseHelper.failure(v.value);
        };
    }

    static <T> Try<T> pure(T value) {
        return success(value);
    }

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
    record Success<T>(T value) implements Try<T> {

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

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Success<?> success = (Success<?>) o;
            return Objects.equals(value, success.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(value);
        }
    }

    /**
     * Failure implementation
     */
    record Failure<T>(Throwable value) implements Try<T> {

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

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            return o != null && getClass() == o.getClass();
        }

        @Override
        public int hashCode() {
            return 13;
        }
    }

}
