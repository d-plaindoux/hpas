/*
 * HPAS
 * https://github.com/d-plaindoux/hpas
 *
 * Copyright (c) 2016 Didier Plaindoux
 * Licensed under the LGPL2 license.
 */

package org.smallibs.data;

import org.smallibs.control.Filter;
import org.smallibs.exception.NoValueException;
import org.smallibs.type.TApp;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public abstract class Maybe<T> implements Filter<Maybe, T, Maybe<T>>, TApp<Maybe, T, Maybe<T>> {

    private Maybe() {
    }

    public static <T> Maybe<T> some(T value) {
        if (value == null) {
            return none();
        }

        return new Some<>(value);
    }

    public static <T> Maybe<T> none() {
        return new None<>();
    }

    @Override
    public <R> R accept(Function<TApp<Maybe, T, Maybe<T>>, R> f) {
        return f.apply(this);
    }

    public Try<T> toTry() {
        final TApp<Maybe, Try<T>, Maybe<Try<T>>> map = this.map(Try::success);
        return map.self().orElse(Try.failure(new NoValueException()));
    }

    public Maybe<T> filter(Predicate<? super T> predicate) {
        if (this.hasSome() && predicate.test(this.get())) {
            return this;
        } else {
            return Maybe.none();
        }
    }

    public <B> Maybe<B> map(Function<? super T, B> mapper) {
        if (this.hasSome()) {
            return Maybe.some(mapper.apply(this.get()));
        } else {
            return Maybe.<B>none();
        }
    }

    public <B> Maybe<B> flatmap(Function<? super T, Maybe<B>> mapper) {
        if (this.hasSome()) {
            return mapper.apply(this.get());
        } else {
            return Maybe.none();
        }
    }

    public Maybe<T> onSome(Consumer<T> onSuccess) {
        if (this.hasSome()) {
            onSuccess.accept(this.get());
        }
        return this;
    }

    public T orElse(Supplier<T> t) {
        if (this.hasSome()) {
            return this.get();
        } else {
            return t.get();
        }
    }

    public T orElse(T t) {
        if (this.hasSome()) {
            return this.get();
        } else {
            return t;
        }
    }

    abstract public boolean hasSome();

    abstract public T get();

    @Override
    public Maybe<T> self() {
        return this;
    }

    /**
     * Some implementation
     */
    private final static class Some<T> extends Maybe<T> {
        private final T value;

        private Some(T value) {
            this.value = value;
        }

        @Override
        public boolean hasSome() {
            return true;
        }

        @Override
        public T get() {
            return value;
        }

    }

    /**
     * None implementation
     */
    private final static class None<T> extends Maybe<T> {

        @Override
        public boolean hasSome() {
            return false;
        }

        @Override
        public T get() {
            throw new IllegalAccessError();
        }
    }

}
