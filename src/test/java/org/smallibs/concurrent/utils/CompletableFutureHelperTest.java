package org.smallibs.concurrent.utils;

import org.junit.Test;
import org.smallibs.concurrent.execution.ExecutorHelper;
import org.smallibs.concurrent.promise.Promise;
import org.smallibs.concurrent.promise.impl.SolvablePromise;
import org.smallibs.data.Try;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
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

    @Test(expected = ExecutionException.class)
    public void shouldHaveACompletableFutureCompletedExceptionally() throws Exception {
        final SolvablePromise<Integer> promise = new SolvablePromise<>();
        final CompletableFuture<Integer> completableFuture = CompletableFutureHelper.completableFuture(promise);

        promise.solve(Try.failure(new IllegalArgumentException()));

        completableFuture.get();
    }

    @Test(expected = TimeoutException.class)
    public void shouldHaveACompletableFutureNotCompleted() throws Exception {
        final SolvablePromise<Integer> promise = new SolvablePromise<>();
        final CompletableFuture<Integer> completableFuture = CompletableFutureHelper.completableFuture(promise);

        completableFuture.get(1, TimeUnit.SECONDS);
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

    @Test(expected = ExecutionException.class)
    public void shouldHaveACompletableFutureCompletedExceptionallyAfterAWhile() throws Exception {
        final Promise<Integer> promise = ExecutorHelper.create(Executors.newSingleThreadExecutor()).async(() -> {
            Thread.sleep(1000);
            throw new IllegalArgumentException();
        });

        final CompletableFuture<Integer> completableFuture = CompletableFutureHelper.completableFuture(promise);

        await().atMost(2, TimeUnit.SECONDS).until(completableFuture::isDone);

        assertThat(completableFuture.get()).isEqualTo(42);
    }

    // CompletableFuture -> Promise

    @Test
    public void shouldHaveAPromiseSuccessful() throws Exception {
        final CompletableFuture<Integer> completableFuture = CompletableFuture.supplyAsync(() -> 42);
        final Promise<Integer> promise = CompletableFutureHelper.promise(completableFuture);

        assertThat(promise.getFuture().get()).isEqualTo(42);
    }

    @Test(expected = ExecutionException.class)
    public void shouldHaveAPromiseFailure() throws Exception {
        final CompletableFuture<Integer> completableFuture = CompletableFuture.supplyAsync(() -> {
            throw new IllegalArgumentException();
        });

        final Promise<Integer> promise = CompletableFutureHelper.promise(completableFuture);

        promise.getFuture().get();
    }

    @Test(expected = TimeoutException.class)
    public void shouldHaveAPromiseNotSolved() throws Exception {
        final CompletableFuture<Integer> completableFuture = new CompletableFuture<>();
        final Promise<Integer> promise = CompletableFutureHelper.promise(completableFuture);

        promise.getFuture().get(1, TimeUnit.SECONDS);
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

    @Test(expected = ExecutionException.class)
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

        promise.getFuture().get();
    }

}