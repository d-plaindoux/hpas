package org.smallibs.data;

import org.smallibs.exception.NoValueException;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public abstract class Maybe<T> implements Monad<Maybe, T>, Selectable<Maybe, T>, TApp<Maybe, T, Maybe<T>> {

    private Maybe() {
    }

    public static <B> TApp<Maybe, B, Maybe<B>> specialize(TApp<Maybe, B, ?> app) {
        //noinspection unchecked
        return (TApp<Maybe, B, Maybe<B>>) app;
    }

    public static <B, Self extends TApp<Maybe, B, Self>> TApp<Maybe, B, Self> generalize(TApp<Maybe, B, Maybe<B>> app) {
        //noinspection unchecked
        return (TApp<Maybe, B, Self>) app;
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
    public <R> R accept(Function<TApp<Maybe, T, ? extends Maybe>, R> f) {
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

    @Override
    public <B, Self extends TApp<Maybe, B, Self>> TApp<Maybe, B, Self> map(Function<? super T, B> mapper) {
        if (this.hasSome()) {
            return generalize(Maybe.some(mapper.apply(this.get())));
        } else {
            return generalize(Maybe.<B>none());
        }
    }

    @Override
    public <B, Self extends TApp<Maybe, B, Self>> TApp<Maybe, B, Self> flatmap(Function<? super T, TApp<Maybe, B, Self>> mapper) {
        if (this.hasSome()) {
            return mapper.apply(this.get());
        } else {
            return (TApp<Maybe, B, Self>) Maybe.none();
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
