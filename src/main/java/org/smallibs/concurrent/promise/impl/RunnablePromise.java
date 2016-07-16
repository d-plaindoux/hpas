package org.smallibs.concurrent.promise.impl;

import org.smallibs.data.Maybe;
import org.smallibs.data.Try;
import org.smallibs.exception.PromiseException;

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

public final class RunnablePromise<T> extends AbstractPromise<T> implements RunnableFuture<T> {

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

        this.onSuccess = __ -> {
        };
        this.onError = __ -> {
        };
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
            manageResponse(Try.success(this.callable.call()));
        } catch (final PromiseException exception) {
            manageResponse(Try.failure(exception.getCause()));
        } catch (final Throwable exception) {
            manageResponse(Try.failure(exception));
        }

        this.currentExecutor.clear();
    }

    //
    // Private behaviors
    //

    private void manageResponse(final Try<T> response) {
        synchronized (this.responseReference) {
            if (this.isCancelled()) {
                return;
            }

            this.responseReference.set(response);
        }

        response.onSuccess(s -> onSuccess.accept(s)).
                onFailure(t -> onError.accept(t));

        synchronized (this.responseReference) {
            this.responseReference.notifyAll();
        }
    }

    private T getNow() throws ExecutionException {
        if (responseReference.get().isSuccess()) {
            return responseReference.get().success();
        } else {
            throw new ExecutionException(responseReference.get().failure());
        }
    }
}
