package org.smallibs.data;

import org.smallibs.exception.NoValueException;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public abstract class Maybe<T> implements Monad<Maybe, T>, Selectable<Maybe, T> {

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

    public Try<T> toTry() {
        return this.map(Try::success).orElse(Try.failure(new NoValueException()));
    }

    public Maybe<T> filter(Predicate<? super T> predicate) {
        if (this.hasSome() && predicate.test(this.get())) {
            return this;
        } else {
            return Maybe.none();
        }
    }

    @Override
    public <B> Maybe<B> map(Function<? super T, B> mapper) {
        if (this.hasSome()) {
            return Maybe.some(mapper.apply(this.get()));
        } else {
            return Maybe.none();
        }
    }

    @Override
    public <B> Maybe<B> flatmap(Function<? super T, Monad<Maybe, B>> mapper) {
        if (this.hasSome()) {
            return mapper.apply(this.get()).concretize();
        } else {
            return Maybe.none();
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public Maybe<T> concretize() {
        return this;
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

    /**
     * Some implementation
     */
    private static class Some<T> extends Maybe<T> {
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
    private static class None<T> extends Maybe<T> {

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
