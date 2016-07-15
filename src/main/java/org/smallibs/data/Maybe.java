package org.smallibs.data;

import org.smallibs.exception.NoValueException;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public interface Maybe<T> extends Monad<Maybe, T>, Selectable<Maybe, T> {

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
        final Maybe<Try<T>> concretize = this.map(Try::success).concretize();
        return concretize.orElse(Try.failure(new NoValueException()));
    }

    default Maybe<T> filter(Predicate<? super T> predicate) {
        if (this.hasSome() && predicate.test(this.get())) {
            return this;
        } else {
            return Maybe.none();
        }
    }

    @Override
    default <B> Maybe<B> map(Function<? super T, B> mapper) {
        if (this.hasSome()) {
            return Maybe.some(mapper.apply(this.get()));
        } else {
            return Maybe.none();
        }
    }

    @Override
    default <B> Maybe<B> flatmap(Function<? super T, Monad<Maybe, B>> mapper) {
        if (this.hasSome()) {
            return mapper.apply(this.get()).concretize();
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

    default T orElse(Supplier<T> t) {
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
     * Some implementation
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

        @Override
        public Maybe<T> concretize() {
            return this;
        }
    }

    /**
     * None implementation
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

        @SuppressWarnings("unchecked")
        @Override
        public Maybe<T> concretize() {
            return this;
        }
    }
}
