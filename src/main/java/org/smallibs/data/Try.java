package org.smallibs.data;

import org.smallibs.exception.FilterException;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public interface Try<T> {

    static <T> Try<T> success(T value) {
        return new Success<>(value);
    }

    static <T> Try<T> failure(Throwable value) {
        return new Failure<>(value);
    }

    default Maybe<T> toMaybe() {
        return this.map(Maybe::some).recoverWith(Maybe.none());
    }


    default Try<T> filter(Predicate<? super T> predicate) {
        if (this.isSuccess() && predicate.test(this.success())) {
            return this;
        } else {
            return Try.failure(new FilterException());
        }
    }

    default <U> Try<U> map(Function<? super T, U> mapper) {
        if (this.isSuccess()) {
            return Try.success(mapper.apply(this.success()));
        } else {
            return Try.failure(this.failure());
        }
    }

    default <U> Try<U> flatmap(Function<? super T, ? extends Try<U>> mapper) {
        if (this.isSuccess()) {
            return mapper.apply(this.success());
        } else {
            return Try.failure(this.failure());
        }
    }

    default T recoverWith(T t) {
        if (this.isSuccess()) {
            return this.success();
        } else {
            return t;
        }
    }

    default Try<T> onSuccess(Consumer<T> onSuccess) {
        if (this.isSuccess()) {
            onSuccess.accept(this.success());
        }
        return this;
    }

    default Try<T> onFailure(Consumer<Throwable> onFailure) {
        if (!this.isSuccess()) {
            onFailure.accept(this.failure());
        }
        return this;
    }

    default T recoverWith(Function<Throwable, T> t) {
        if (this.isSuccess()) {
            return this.success();
        } else {
            return t.apply(this.failure());
        }
    }

    default <X extends Throwable> T orElseThrow(Supplier<? extends X> exceptionSupplier) throws X {
        if (this.isSuccess()) {
            return this.success();
        } else {
            throw exceptionSupplier.get();
        }
    }

    default <X extends Throwable> T orElseRetrieveAndThrow(Function<Throwable, ? extends X> exceptionSupplier) throws X {
        if (this.isSuccess()) {
            return this.success();
        } else {
            throw exceptionSupplier.apply(this.failure());
        }
    }

    default <X extends Throwable> T orElseRetrieveAndThrow() throws Throwable {
        if (this.isSuccess()) {
            return this.success();
        } else {
            throw this.failure();
        }
    }

    boolean isSuccess();

    T success();

    Throwable failure();

    /**
     * Success implementation
     */
    class Success<T> implements Try<T> {
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
    class Failure<T> implements Try<T> {
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
