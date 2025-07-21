/*
 * HPAS
 * https://github.com/d-plaindoux/hpas
 *
 * Copyright (c) 2016-2025 Didier Plaindoux
 * Licensed under the LGPL2 license.
 */

package org.smallibs.data;

import org.smallibs.control.Filter;
import org.smallibs.type.HK;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public sealed interface Maybe<T> extends Filter<Maybe, T, Maybe<T>>, HK<Maybe, T, Maybe<T>> {

    static <T> Maybe<T> pure(T value) {
        return some(value);
    }

    static <T> Maybe<T> some(T value) {
        if (value == null) {
            return none();
        }

        return new Some<>(value);
    }

    static <T> Maybe<T> none() {
        return new None<>();
    }

    @Override
    default Maybe<T> self() {
        return this;
    }

    default Maybe<T> filter(Predicate<? super T> predicate) {
        return this.flatmap(t -> predicate.test(t) ? this : Maybe.none());
    }

    default <B> Maybe<B> map(Function<? super T, ? extends B> mapper) {
        return this.flatmap(x -> some(mapper.apply(x)));
    }

    default <B> B fold(Function<? super T, B> some, Supplier<B> none) {
        return this.map(some).orElse(none);
    }

    default T orElse(T t) {
        return this.orElse(() -> t);
    }

    default boolean hasSome() {
        return this.fold(__ -> true, () -> false);
    }

    Maybe<T> onSome(Consumer<T> onSuccess);

    Maybe<T> onNone(Runnable onNone);

    <B> Maybe<B> flatmap(Function<? super T, Maybe<B>> mapper);

    T orElse(Supplier<T> t);

    /**
     * Some implementation
     */
    record Some<T>(T value) implements Maybe<T> {

        @Override
        public boolean hasSome() {
            return true;
        }

        @Override
        public <B> Maybe<B> flatmap(Function<? super T, Maybe<B>> mapper) {
            return mapper.apply(this.value);
        }

        public Maybe<T> onSome(Consumer<T> onSuccess) {
            onSuccess.accept(this.value);
            return this;
        }

        @Override
        public Maybe<T> onNone(Runnable onNone) {
            return this;
        }

        public T orElse(Supplier<T> t) {
            return this.value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Some<?> some = (Some<?>) o;
            return Objects.equals(value, some.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(value);
        }
    }

    /**
     * None implementation
     */
    record None<T>() implements Maybe<T> {

        @Override
        public boolean hasSome() {
            return false;
        }

        @Override
        public <B> Maybe<B> flatmap(Function<? super T, Maybe<B>> mapper) {
            return Maybe.none();
        }

        public Maybe<T> onSome(Consumer<T> onSuccess) {
            return this;
        }

        @Override
        public Maybe<T> onNone(Runnable onNone) {
            onNone.run();
            return this;
        }

        public T orElse(Supplier<T> t) {
            return t.get();
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
