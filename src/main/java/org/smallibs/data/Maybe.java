/*
 * HPAS
 * https://github.com/d-plaindoux/hpas
 *
 * Copyright (c) 2016 Didier Plaindoux
 * Licensed under the LGPL2 license.
 */

package org.smallibs.data;

import org.smallibs.control.Filter;
import org.smallibs.type.Kind;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public interface Maybe<T> extends Filter<Maybe, T, Maybe<T>>, Kind<Maybe, T, Maybe<T>> {

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
    default <R> R accept(Function<Kind<Maybe, T, Maybe<T>>, R> f) {
        return f.apply(this);
    }

    @Override
    default Maybe<T> self() {
        return this;
    }

    default Maybe<T> filter(Predicate<? super T> predicate) {
        return this.flatmap(t -> predicate.test(t) ? this : Maybe.none());
    }

    default <B> Maybe<B> map(Function<? super T, B> mapper) {
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

    <B> Maybe<B> flatmap(Function<? super T, Maybe<B>> mapper);

    T orElse(Supplier<T> t);

    /**
     * Some implementation
     */
    final class Some<T> implements Maybe<T> {
        private final T value;

        private Some(T value) {
            this.value = value;
        }

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

        public T orElse(Supplier<T> t) {
            return this.value;
        }

    }

    /**
     * None implementation
     */
    final class None<T> implements Maybe<T> {

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

        public T orElse(Supplier<T> t) {
            return t.get();
        }
    }

}
