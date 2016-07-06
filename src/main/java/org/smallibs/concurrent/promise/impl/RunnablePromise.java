package org.smallibs.concurrent.promise.impl;

import org.smallibs.concurrent.promise.Promise;
import org.smallibs.exception.PromiseException;
import org.smallibs.data.Maybe;
import org.smallibs.data.Try;

import java.lang.ref.WeakReference;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Function;

public class RunnablePromise<T> implements Promise<T>, RunnableFuture<T> {

    private final Callable<T> callable;
    private final AtomicReference<Try<T>> responseReference;

    private WeakReference<Thread> currentExecutor;
    private volatile boolean canceled;

    private Consumer<T> onSuccess;
    private Consumer<Throwable> onError;

    public RunnablePromise(Callable<T> callable) {
        Objects.requireNonNull(callable);

        this.callable = callable;
        this.responseReference = new AtomicReference<>();

        this.currentExecutor = new WeakReference<>(null);
        this.canceled = false;

        this.onSuccess = null;
        this.onError = null;
    }

    @Override
    public Future<T> getFuture() {
        return this;
    }

    @Override
    public void onSuccess(Consumer<T> consumer) {
        Objects.requireNonNull(consumer);

        Maybe<T> success = Maybe.none();

        synchronized (this.responseReference) {
            if (this.isDone() || this.isCancelled()) {
                if (this.responseReference.get().isSuccess()) {
                    success = Maybe.some(this.responseReference.get().success());
                }
            } else {
                this.onSuccess = consumer;
            }
        }

        if (success.hasSome()) {
            consumer.accept(success.get());
        }
    }

    @Override
    public void onFailure(Consumer<Throwable> consumer) {
        Objects.requireNonNull(consumer);

        Maybe<Throwable> failure = Maybe.none();

        synchronized (this.responseReference) {
            if (this.isDone() || this.isCancelled()) {
                if (!this.responseReference.get().isSuccess()) {
                    failure = Maybe.some(this.responseReference.get().failure());
                }
            } else {
                this.onError = consumer;
            }
        }

        if (failure.hasSome()) {
            consumer.accept(failure.get());
        }
    }

    @Override
    public void onComplete(final Consumer<Try<T>> consumer) {
        Objects.requireNonNull(consumer);

        this.onSuccess(t -> consumer.accept(Try.success(t)));
        this.onFailure(throwable -> consumer.accept(Try.failure(throwable)));
    }

    @Override
    public <R> Promise<R> map(Function<? super T, R> function) {
        Objects.requireNonNull(function);

        return new MappedPromise<>(this, function);
    }

    @Override
    public <R> Promise<R> flatmap(Function<? super T, ? extends Promise<R>> function) {
        Objects.requireNonNull(function);

        return new FlatMappedPromise<>(this, function);
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        synchronized (this.responseReference) {
            if (this.isDone() || this.isCancelled()) {
                return false;
            }

            if (mayInterruptIfRunning) {
                Maybe.some(this.currentExecutor.get()).onSome(Thread::interrupt);
            }

            this.responseReference.set(Try.failure(new CancellationException()));
            this.canceled = true;

            return true;
        }

    }

    @Override
    public boolean isCancelled() {
        return this.canceled;
    }

    @Override
    public boolean isDone() {
        return this.responseReference.get() != null;
    }

    @Override
    public T get() throws InterruptedException, ExecutionException {
        synchronized (responseReference) {
            if (responseReference.get() == null) {
                responseReference.wait();
            }
        }

        return getNow();
    }

    @Override
    public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        synchronized (responseReference) {
            if (responseReference.get() == null) {
                responseReference.wait(unit.toMillis(timeout));
            }

            if (responseReference.get() == null) {
                throw new TimeoutException();
            }
        }

        return getNow();
    }

    @Override
    public void run() {
        this.currentExecutor = new WeakReference<>(Thread.currentThread());

        try {
            manageResult(this.callable.call());
        } catch (final PromiseException exception) {
            manageError(exception.getCause());
        } catch (final Throwable exception) {
            manageError(exception);
        }

        this.currentExecutor.clear();
    }

    //
    // Private behaviors
    //

    private boolean manageResult(final T call) {
        synchronized (this.responseReference) {
            if (this.isCancelled()) {
                return true;
            }

            this.responseReference.set(Try.success(call));
            this.responseReference.notifyAll();
        }

        Maybe.some(this.onSuccess).onSome(consumer -> consumer.accept(call));
        return false;
    }

    private void manageError(final Throwable exception) {
        synchronized (this.responseReference) {
            if (this.isCancelled()) {
                return;
            }

            this.responseReference.set(Try.failure(exception));
            this.responseReference.notifyAll();
        }

        Maybe.some(this.onError).onSome(throwableConsumer -> throwableConsumer.accept(exception));
    }

    private T getNow() throws ExecutionException {
        if (responseReference.get().isSuccess()) {
            return responseReference.get().success();
        } else {
            throw new ExecutionException(responseReference.get().failure());
        }
    }
}
