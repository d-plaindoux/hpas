/*
 * HPAS
 * https://github.com/d-plaindoux/hpas
 *
 * Copyright (c) 2016-2017 Didier Plaindoux
 * Licensed under the LGPL2 license.
 */

package org.smallibs.concurrent.promise;

import com.jayway.awaitility.Duration;
import org.junit.Test;
import org.smallibs.concurrent.execution.Executor;
import org.smallibs.concurrent.execution.ExecutorHelper;
import org.smallibs.concurrent.promise.impl.SolvablePromise;
import org.smallibs.data.Unit;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.jayway.awaitility.Awaitility.await;
import static org.assertj.core.api.Assertions.assertThat;
import static org.smallibs.concurrent.promise.PromiseHelper.failure;
import static org.smallibs.concurrent.promise.PromiseHelper.success;

public class PromisesSetTest {

    @Test
    public void shouldJoinWhenEmpty() {
        final AtomicBoolean aBoolean = new AtomicBoolean(false);

        PromiseHelper.join().onSuccess(unit -> aBoolean.set(true));

        assertThat(aBoolean.get()).isTrue();
    }

    @Test
    public void shouldJoinAndReturnListWhenEmpty() throws InterruptedException, ExecutionException, TimeoutException {
        final Promise<Unit> promise = PromiseHelper.join();

        promise.getFuture().get(5, TimeUnit.SECONDS);

        assertThat(promise.getFuture().get()).isEqualTo(Unit.unit);
    }

    @Test
    public void shouldJoinWhenSuccessPromise() {
        final AtomicBoolean aBoolean = new AtomicBoolean(false);

        PromiseHelper.join(success(1)).onSuccess(unit -> aBoolean.set(true));

        assertThat(aBoolean.get()).isTrue();
    }

    @Test
    public void shouldJoinAndReturnListWhenSuccessPromise() throws InterruptedException, ExecutionException, TimeoutException {
        final Promise<Unit> promise = PromiseHelper.join(success(1));

        promise.getFuture().get(5, TimeUnit.SECONDS);

        assertThat(promise.getFuture().get()).isEqualTo(Unit.unit);
    }

    @Test
    public void shouldJoinWhenFailurePromise() {
        final AtomicBoolean aBoolean = new AtomicBoolean(false);

        PromiseHelper.join(failure(new SecurityException())).onSuccess(unit -> aBoolean.set(true));

        assertThat(aBoolean.get()).isTrue();
    }

    @Test
    public void shouldNotJoinWhenUnsolvedPromise() {
        final AtomicBoolean aBoolean = new AtomicBoolean(false);
        final SolvablePromise<Object> passivePromise = new SolvablePromise<>();

        PromiseHelper.join(passivePromise).onSuccess(unit -> aBoolean.set(true));

        assertThat(aBoolean.get()).isFalse();
    }

    @Test
    public void shouldJoinWhenSolvedPromiseAfter() {
        final AtomicBoolean aBoolean = new AtomicBoolean(false);
        final Executor executor = givenAnExecutor();

        final Promise<Integer> promise = executor.async(() -> {
            Thread.sleep(1000);
            return 1;
        });

        PromiseHelper.join(promise).onSuccess(unit -> aBoolean.set(true));

        await().atMost(Duration.FIVE_SECONDS).until(aBoolean::get);

        assertThat(aBoolean.get()).isTrue();
    }

    @Test
    public void shouldExistsWhenEmpty() {
        final AtomicBoolean aBoolean = new AtomicBoolean(false);

        PromiseHelper.exists().onFailure(unit -> aBoolean.set(true));

        assertThat(aBoolean.get()).isTrue();
    }

    @Test
    public void shouldExistsWhenSuccessPromise() {
        final AtomicBoolean aBoolean = new AtomicBoolean(false);

        PromiseHelper.exists(success(1)).onSuccess(unit -> aBoolean.set(true));

        assertThat(aBoolean.get()).isTrue();
    }

    @Test
    public void shouldExistsWhenFailurePromise() {
        final AtomicBoolean aBoolean = new AtomicBoolean(false);

        PromiseHelper.exists(failure(new SecurityException())).onFailure(unit -> aBoolean.set(true));

        assertThat(aBoolean.get()).isTrue();
    }

    @Test
    public void shouldNotExistsWhenUnsolvedPromise() {
        final AtomicBoolean aBoolean = new AtomicBoolean(false);
        final SolvablePromise<Object> passivePromise = new SolvablePromise<>();

        PromiseHelper.exists(passivePromise).onComplete(unit -> aBoolean.set(true));

        assertThat(aBoolean.get()).isFalse();
    }

    @Test
    public void shouldExistsWheOneSolvedPromise() {
        final AtomicBoolean aBoolean = new AtomicBoolean(false);

        PromiseHelper.exists(success(1), failure(new SecurityException())).onSuccess(unit -> aBoolean.set(true));

        assertThat(aBoolean.get()).isTrue();
    }

    @Test
    public void shouldExistsWhenSolvedPromiseAfter() {
        final AtomicBoolean aBoolean = new AtomicBoolean(false);
        final Executor executor = givenAnExecutor();

        final Promise<Integer> promise = executor.async(() -> {
            Thread.sleep(1000);
            return 1;
        });

        PromiseHelper.exists(promise).onSuccess(unit -> aBoolean.set(true));

        await().atMost(Duration.FIVE_SECONDS).until(aBoolean::get);

        assertThat(aBoolean.get()).isTrue();
    }


    @Test
    public void shouldForallWhenEmpty() {
        final AtomicBoolean aBoolean = new AtomicBoolean(false);

        PromiseHelper.forall().onSuccess(unit -> aBoolean.set(true));

        assertThat(aBoolean.get()).isTrue();
    }

    @Test
    public void shouldForallWhenSuccessPromise() {
        final AtomicBoolean aBoolean = new AtomicBoolean(false);

        PromiseHelper.forall(success(1)).onSuccess(unit -> aBoolean.set(true));

        assertThat(aBoolean.get()).isTrue();
    }

    @Test
    public void shouldForallWhenFailurePromise() {
        final AtomicBoolean aBoolean = new AtomicBoolean(false);

        PromiseHelper.forall(failure(new SecurityException())).onFailure(unit -> aBoolean.set(true));

        assertThat(aBoolean.get()).isTrue();
    }

    @Test
    public void shouldNotForallWhenUnsolvedPromise() {
        final AtomicBoolean aBoolean = new AtomicBoolean(false);
        final SolvablePromise<Object> passivePromise = new SolvablePromise<>();

        PromiseHelper.forall(passivePromise).onComplete(unit -> aBoolean.set(true));

        assertThat(aBoolean.get()).isFalse();
    }

    @Test
    public void shouldForallWheOneSolvedPromise() {
        final AtomicBoolean aBoolean = new AtomicBoolean(false);

        PromiseHelper.forall(success(1), failure(new SecurityException())).onFailure(unit -> aBoolean.set(true));

        assertThat(aBoolean.get()).isTrue();
    }

    @Test
    public void shouldForallWhenSolvedPromiseAfter() {
        final AtomicBoolean aBoolean = new AtomicBoolean(false);
        final Executor executor = givenAnExecutor();

        final Promise<Integer> promise = executor.async(() -> {
            Thread.sleep(1000);
            return 1;
        });

        PromiseHelper.forall(promise).onSuccess(unit -> aBoolean.set(true));

        await().atMost(Duration.FIVE_SECONDS).until(aBoolean::get);

        assertThat(aBoolean.get()).isTrue();
    }

    private Executor givenAnExecutor() {
        return ExecutorHelper.create(Executors.newSingleThreadExecutor());
    }
}