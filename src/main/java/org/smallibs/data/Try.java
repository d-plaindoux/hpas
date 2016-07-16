package org.smallibs.data;

import org.smallibs.exception.FilterException;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public abstract class Try<T> implements Monad<Try, T>, Selectable<Try, T> {

    private Try() {
    }

    public static <T> Try<T> success(T value) {
        return new Success<>(value);
    }

    public static <T> Try<T> failure(Throwable value) {
        return new Failure<>(value);
    }

    public Maybe<T> toMaybe() {
        return this.map(Maybe::some).recoverWith(Maybe.none());
    }

    public Try<T> filter(Predicate<? super T> predicate) {
        if (this.isSuccess() && predicate.test(this.success())) {
            return this;
        } else {
            return Try.failure(new FilterException());
        }
    }

    @Override
    public <B> Try<B> map(Function<? super T, B> mapper) {
        if (this.isSuccess()) {
            return Try.success(mapper.apply(this.success()));
        } else {
            return Try.failure(this.failure());
        }
    }

    @Override
    public <B> Try<B> flatmap(Function<? super T, Monad<Try, B>> mapper) {
        if (this.isSuccess()) {
            return mapper.apply(this.success()).concretize();
        } else {
            return Try.failure(this.failure());
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public Try<T> concretize() {
        return this;
    }

    public T recoverWith(T t) {
        if (this.isSuccess()) {
            return this.success();
        } else {
            return t;
        }
    }

    public Try<T> onSuccess(Consumer<T> onSuccess) {
        if (this.isSuccess()) {
            onSuccess.accept(this.success());
        }
        return this;
    }

    public Try<T> onFailure(Consumer<Throwable> onFailure) {
        if (!this.isSuccess()) {
            onFailure.accept(this.failure());
        }
        return this;
    }

    public T recoverWith(Function<Throwable, T> t) {
        if (this.isSuccess()) {
            return this.success();
        } else {
            return t.apply(this.failure());
        }
    }

    public <X extends Throwable> T orElseThrow(Supplier<? extends X> exceptionSupplier) throws X {
        if (this.isSuccess()) {
            return this.success();
        } else {
            throw exceptionSupplier.get();
        }
    }

    public <X extends Throwable> T orElseRetrieveAndThrow(Function<Throwable, ? extends X> exceptionSupplier) throws X {
        if (this.isSuccess()) {
            return this.success();
        } else {
            throw exceptionSupplier.apply(this.failure());
        }
    }

    public <X extends Throwable> T orElseRetrieveAndThrow() throws Throwable {
        if (this.isSuccess()) {
            return this.success();
        } else {
            throw this.failure();
        }
    }

    abstract public boolean isSuccess();

    abstract public T success();

    abstract public Throwable failure();

    /**
     * Success implementation
     */
    private static class Success<T> extends Try<T> {
        private final T value;

        private Success(T value) {
            this.value = value;
        }

        @Override
        public boolean isSuccess() {
            return true;
        }

        @Override
        public T success() {
            return value;
        }

        @Override
        public Throwable failure() {
            throw new IllegalAccessError();
        }
    }

    /**
     * Failure implementation
     */
    private static class Failure<T> extends Try<T> {
        private final Throwable value;

        private Failure(Throwable value) {
            this.value = value;
        }

        @Override
        public boolean isSuccess() {
            return false;
        }

        @Override
        public T success() {
            throw new IllegalAccessError();
        }

        @Override
        public Throwable failure() {
            return value;
        }
    }

}
