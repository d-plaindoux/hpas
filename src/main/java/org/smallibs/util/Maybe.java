package org.smallibs.util;

import org.smallibs.exception.NoValueException;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public interface Maybe<T> {

    static <T> Maybe<T> some(T value) {
        if (value == null) {
            return none();
        }

        return new Some<>(value);
    }

    static <T> Maybe<T> none() {
        return new None<>();
    }

    default Try<T> toTry() {
        return this.map(Try::success).orElse(Try.failure(new NoValueException()));
    }

    default Maybe<T> filter(Predicate<? super T> predicate) {
        if (this.hasSome() && predicate.test(this.get())) {
            return this;
        } else {
            return Maybe.none();
        }
    }

    default <U> Maybe<U> map(Function<? super T, U> mapper) {
        if (this.hasSome()) {
            return Maybe.some(mapper.apply(this.get()));
        } else {
            return Maybe.none();
        }
    }

    default <U> Maybe<U> flatmap(Function<? super T, ? extends Maybe<U>> mapper) {
        if (this.hasSome()) {
            return mapper.apply(this.get());
        } else {
            return Maybe.none();
        }
    }

    default Maybe<T> onSome(Consumer<T> onSuccess) {
        if (this.hasSome()) {
            onSuccess.accept(this.get());
        }
        return this;
    }

    default T orLazyElse(Supplier<T> t) {
        if (this.hasSome()) {
            return this.get();
        } else {
            return t.get();
        }
    }

    default T orElse(T t) {
        if (this.hasSome()) {
            return this.get();
        } else {
            return t;
        }
    }

    boolean hasSome();

    T get();

    /**
     * Success implementation
     */
    class Some<T> implements Maybe<T> {
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
     * Failure implementation
     */
    class None<T> implements Maybe<T> {

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
