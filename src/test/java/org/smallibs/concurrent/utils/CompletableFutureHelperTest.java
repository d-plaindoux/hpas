package org.smallibs.concurrent.utils;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.smallibs.concurrent.execution.ExecutorHelper;
import org.smallibs.concurrent.promise.Promise;
import org.smallibs.concurrent.promise.impl.SolvablePromise;
import org.smallibs.data.Try;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static com.jayway.awaitility.Awaitility.await;
import static org.assertj.core.api.Assertions.assertThat;

public class CompletableFutureHelperTest {

    // Promise -> CompletableFuture

    @Test
    public void shouldHaveACompletableFutureCompleted() throws Exception {
        final SolvablePromise<Integer> promise = new SolvablePromise<>();
        final CompletableFuture<Integer> completableFuture = CompletableFutureHelper.completableFuture(promise);

        promise.solve(Try.success(42));

        assertThat(completableFuture.get()).isEqualTo(42);
    }

    @Test
    public void shouldHaveACompletableFutureCompletedExceptionally() {
        final SolvablePromise<Integer> promise = new SolvablePromise<>();
        final CompletableFuture<Integer> completableFuture = CompletableFutureHelper.completableFuture(promise);

        promise.solve(Try.failure(new IllegalArgumentException()));

        Assertions.assertThatThrownBy(completableFuture::get)
                .rootCause()
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void shouldHaveACompletableFutureNotCompleted() throws Exception {
        final SolvablePromise<Integer> promise = new SolvablePromise<>();
        final CompletableFuture<Integer> completableFuture = CompletableFutureHelper.completableFuture(promise);

        Assertions.assertThatThrownBy(() -> completableFuture.get(1, TimeUnit.SECONDS))
                .isInstanceOf(TimeoutException.class);
    }

    @Test
    public void shouldHaveACompletableFutureCompletedAfterAWhile() throws Exception {
        final Promise<Integer> promise = ExecutorHelper.create(Executors.newSingleThreadExecutor()).async(() -> {
            Thread.sleep(1000);
            return 42;
        });

        final CompletableFuture<Integer> completableFuture = CompletableFutureHelper.completableFuture(promise);

        await().atMost(2, TimeUnit.SECONDS).until(completableFuture::isDone);

        assertThat(completableFuture.get()).isEqualTo(42);
    }

    @Test
    public void shouldHaveACompletableFutureCompletedExceptionallyAfterAWhile() throws Exception {
        final Promise<Integer> promise = ExecutorHelper.create(Executors.newSingleThreadExecutor()).async(() -> {
            Thread.sleep(1000);
            throw new IllegalArgumentException();
        });

        final CompletableFuture<Integer> completableFuture = CompletableFutureHelper.completableFuture(promise);

        await().atMost(2, TimeUnit.SECONDS).until(completableFuture::isDone);

        Assertions.assertThatThrownBy(() -> assertThat(completableFuture.get()).isEqualTo(42))
                .rootCause()
                .isInstanceOf(IllegalArgumentException.class);
    }

    // CompletableFuture -> Promise

    @Test
    public void shouldHaveAPromiseSuccessful() throws Exception {
        final CompletableFuture<Integer> completableFuture = CompletableFuture.supplyAsync(() -> 42);
        final Promise<Integer> promise = CompletableFutureHelper.promise(completableFuture);

        assertThat(promise.getFuture().get()).isEqualTo(42);
    }

    @Test
    public void shouldHaveAPromiseFailure() throws Exception {
        final CompletableFuture<Integer> completableFuture = CompletableFuture.supplyAsync(() -> {
            throw new IllegalArgumentException();
        });

        final Promise<Integer> promise = CompletableFutureHelper.promise(completableFuture);

        Assertions.assertThatThrownBy(() -> promise.getFuture().get())
                .rootCause()
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void shouldHaveAPromiseNotSolved() throws Exception {
        final CompletableFuture<Integer> completableFuture = new CompletableFuture<>();
        final Promise<Integer> promise = CompletableFutureHelper.promise(completableFuture);

        Assertions.assertThatThrownBy(() -> promise.getFuture().get(1, TimeUnit.SECONDS))
                .isInstanceOf(TimeoutException.class);
    }

    @Test
    public void shouldHaveAPromiseSuccessfulAfterAWhile() throws Exception {
        final CompletableFuture<Integer> completableFuture = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ignored) {
            }
            return 42;
        });

        final Promise<Integer> promise = CompletableFutureHelper.promise(completableFuture);

        await().atMost(2, TimeUnit.SECONDS).until(promise.getFuture()::isDone);

        assertThat(promise.getFuture().get()).isEqualTo(42);
    }

    @Test
    public void shouldHaveAPromiseFailureAfterAWhile() throws Exception {
        final CompletableFuture<Integer> completableFuture = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ignored) {
            }
            throw new IllegalArgumentException();
        });

        final Promise<Integer> promise = CompletableFutureHelper.promise(completableFuture);

        await().atMost(2, TimeUnit.SECONDS).until(promise.getFuture()::isDone);

        Assertions.assertThatThrownBy(() -> promise.getFuture().get())
                .rootCause()
                .isInstanceOf(IllegalArgumentException.class);
    }

}