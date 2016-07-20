package org.smallibs.data;

import org.smallibs.exception.NoValueException;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public abstract class Maybe<T> implements Filter<Maybe, T>, TApp<Maybe, T, Maybe<T>> {

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

    public Monad<Maybe, T, Maybe<T>> monad() {
        return new Monadic<T>(this);
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

    /**
     * @param <T>
     */
    private final static class Monadic<T> implements Monad<Maybe, T, Maybe<T>> {
        private final Maybe<T> aMaybe;

        private Monadic(Maybe<T> aMaybe) {
            this.aMaybe = aMaybe;
        }

        @Override
        public <B, NSelf extends TApp<Maybe, B, NSelf>> TApp<Maybe, B, NSelf> map(Function<? super T, B> function) {
            return generalize(new Monadic<>(aMaybe.map(function)));
        }

        @Override
        public <B, NSelf extends TApp<Maybe, B, NSelf>> TApp<Maybe, B, NSelf> flatmap(Function<? super T, TApp<Maybe, B, NSelf>> function) {
            final Function<T, Maybe<B>> tMaybeFunction = t -> {
                final TApp<Maybe, B, NSelf> apply = function.apply(t);
                return Maybe.specialize(apply).self();
            };

            return generalize(new Monadic<>(aMaybe.flatmap(tMaybeFunction)));
        }

        @Override
        public <T1> T1 accept(Function<TApp<Maybe, T, Maybe<T>>, T1> f) {
            return aMaybe.accept(f);
        }

        @Override
        public Maybe<T> self() {
            return aMaybe;
        }
    }

}
