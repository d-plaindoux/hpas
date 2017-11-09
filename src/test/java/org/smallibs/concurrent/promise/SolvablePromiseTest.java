/*
 * HPAS
 * https://github.com/d-plaindoux/hpas
 *
 * Copyright (c) 2016-2017 Didier Plaindoux
 * Licensed under the LGPL2 license.
 */

package org.smallibs.concurrent.promise;

import org.junit.Test;
import org.smallibs.concurrent.execution.Executor;
import org.smallibs.concurrent.execution.ExecutorHelper;
import org.smallibs.concurrent.promise.impl.SolvablePromise;
import org.smallibs.data.Try;
import org.smallibs.exception.FilterException;
import org.smallibs.type.HK;

import java.time.Duration;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.smallibs.concurrent.execution.ExecutorHelper.await;

public class SolvablePromiseTest {

    @Test
    public void shouldApplyOnSuccess() throws Exception {
        final AtomicBoolean aBoolean = new AtomicBoolean(false);

        final SolvablePromise<Integer> integerPromise = new SolvablePromise<>();

        integerPromise.solve(Try.success(1));
        integerPromise.onSuccess(i -> aBoolean.set(true));
        integerPromise.getFuture().get(5, TimeUnit.SECONDS);

        assertThat(aBoolean.get()).isTrue();
    }

    @Test
    public void shouldApplyAndAcceptOnSuccess() throws Exception {
        final AtomicBoolean aBoolean = new AtomicBoolean(false);

        final SolvablePromise<Integer> solvablePromise = new SolvablePromise<>();
        final Promise<Integer> integerPromise = solvablePromise.accept(HK::self);

        solvablePromise.solve(Try.success(1));
        integerPromise.onSuccess(i -> aBoolean.set(true));
        integerPromise.getFuture().get(5, TimeUnit.SECONDS);

        assertThat(aBoolean.get()).isTrue();
    }


    @Test
    public void shouldApplyOnSuccessTwice() throws Exception {
        final AtomicInteger anInteger = new AtomicInteger(0);

        final SolvablePromise<Integer> integerPromise = new SolvablePromise<>();

        integerPromise.solve(Try.success(1));
        integerPromise.onSuccess(i -> anInteger.incrementAndGet());
        integerPromise.onSuccess(i -> anInteger.incrementAndGet());
        integerPromise.getFuture().get(5, TimeUnit.SECONDS);

        assertThat(anInteger.get()).isEqualTo(2);
    }

    @Test
    public void shouldApplyOnSuccessAfterOneSecond() throws Exception {
        final AtomicBoolean aBoolean = new AtomicBoolean(false);
        final Executor executor = givenAnExecutor();

        final SolvablePromise<Integer> integerPromise = new SolvablePromise<>();

        executor.async(() -> {
            Thread.sleep(1000);
            integerPromise.solve(Try.success(1));
        });

        integerPromise.onSuccess(i -> aBoolean.set(true));
        integerPromise.getFuture().get(5, TimeUnit.SECONDS);

        assertThat(aBoolean.get()).isTrue();
    }


    @Test
    public void shouldApplyOnSuccessTwiceAfterOneSecond() throws Exception {
        final Executor executor = givenAnExecutor();

        final AtomicInteger anInteger = new AtomicInteger(0);

        final SolvablePromise<Integer> integerPromise = new SolvablePromise<>();

        integerPromise.onSuccess(i -> anInteger.incrementAndGet());
        integerPromise.onSuccess(i -> anInteger.incrementAndGet());

        executor.async(() -> {
            Thread.sleep(1000);
            integerPromise.solve(Try.success(1));
        });

        integerPromise.getFuture().get(5, TimeUnit.SECONDS);

        assertThat(anInteger.get()).isEqualTo(2);
    }

    @Test
    public void shouldApplyOnFailure() throws Exception {
        final AtomicBoolean aBoolean = new AtomicBoolean(false);

        final SolvablePromise<Integer> integerPromise = new SolvablePromise<>();

        integerPromise.solve(Try.failure(new SecurityException()));

        integerPromise.onFailure(i -> aBoolean.set(true));

        try {
            integerPromise.getFuture().get(5, TimeUnit.SECONDS);
            assertThat(true).isFalse();
        } catch (ExecutionException e) {
            // consume
        }

        assertThat(aBoolean.get()).isTrue();
    }

    @Test
    public void shouldApplyOnFailureAfterResolution() throws Exception {
        final AtomicReference<Throwable> reference = new AtomicReference<>();

        final SolvablePromise<Integer> integerPromise = new SolvablePromise<>();

        integerPromise.solve(Try.failure(new SecurityException()));

        integerPromise.onFailure(reference::set);

        try {
            integerPromise.getFuture().get(5, TimeUnit.SECONDS);
            assertThat(true).isFalse();
        } catch (ExecutionException e) {
            // consume
        }

        assertThat(reference.get()).isInstanceOf(SecurityException.class);
    }

    @Test
    public void shouldApplyOnFailureBeforeResolution() throws Exception {
        final AtomicReference<Throwable> reference = new AtomicReference<>();

        final SolvablePromise<Integer> integerPromise = new SolvablePromise<>();

        integerPromise.onFailure(reference::set);

        integerPromise.solve(Try.failure(new SecurityException()));

        try {
            integerPromise.getFuture().get(5, TimeUnit.SECONDS);
            assertThat(true).isFalse();
        } catch (ExecutionException e) {
            // consume
        }

        assertThat(reference.get()).isInstanceOf(SecurityException.class);
    }

    @Test
    public void shouldApplyOnFailureAfterOneSecond() throws Exception {
        final AtomicBoolean aBoolean = new AtomicBoolean(false);
        final Executor executor = givenAnExecutor();

        final SolvablePromise<Integer> integerPromise = new SolvablePromise<>();

        executor.async(() -> {
            Thread.sleep(1000);
            integerPromise.solve(Try.failure(new SecurityException()));
        });

        integerPromise.onFailure(i -> aBoolean.set(true));

        try {
            integerPromise.getFuture().get(5, TimeUnit.SECONDS);
            assertThat(true).isFalse();
        } catch (ExecutionException e) {
            // consume
        }

        assertThat(aBoolean.get()).isTrue();
    }

    @Test
    public void shouldApplyOnComplete() throws Exception {
        final AtomicBoolean aBoolean = new AtomicBoolean(false);

        final SolvablePromise<Integer> integerPromise = new SolvablePromise<>();

        integerPromise.solve(Try.failure(new SecurityException()));

        integerPromise.onComplete(i -> aBoolean.set(true));

        try {
            integerPromise.getFuture().get(5, TimeUnit.SECONDS);
            assertThat(true).isFalse();
        } catch (ExecutionException e) {
            // consume
        }

        assertThat(aBoolean.get()).isTrue();
    }

    @Test
    public void shouldApplyOnCompleteAfterOneSecond() throws Exception {
        final AtomicBoolean aBoolean = new AtomicBoolean(false);
        final Executor executor = givenAnExecutor();

        final SolvablePromise<Integer> integerPromise = new SolvablePromise<>();

        executor.async(() -> {
            Thread.sleep(1000);
            integerPromise.solve(Try.failure(new SecurityException()));
        });

        integerPromise.onComplete(i -> aBoolean.set(true));

        try {
            integerPromise.getFuture().get(5, TimeUnit.SECONDS);
            assertThat(true).isFalse();
        } catch (ExecutionException e) {
            // consume
        }

        assertThat(aBoolean.get()).isTrue();
    }

    @Test
    public void shouldApplyPromiseMap() throws Exception {
        final SolvablePromise<Integer> integerPromise = new SolvablePromise<>();

        integerPromise.solve(Try.success(1));

        final Promise<Integer> promise = integerPromise.map(i -> i + 1);

        assertThat(promise.getFuture().get(5, TimeUnit.SECONDS)).isEqualTo(2);
    }

    @Test(expected = ExecutionException.class)
    public void shouldNotApplyPromiseMap() throws Exception {
        final SolvablePromise<Integer> integerPromise = new SolvablePromise<>();

        integerPromise.solve(Try.failure(new SecurityException()));

        final Promise<Integer> promise = integerPromise.map(i -> i + 1);

        promise.getFuture().get(5, TimeUnit.SECONDS);
    }

    @Test
    public void shouldApplyPromiseFlatMap() throws Exception {
        final Executor executor = givenAnExecutor();

        final SolvablePromise<Integer> integerPromise = new SolvablePromise<>();

        integerPromise.solve(Try.success(1));

        final Promise<Integer> promise = integerPromise.flatmap(i -> executor.async(() -> i + 1));

        assertThat(promise.getFuture().get(5, TimeUnit.SECONDS)).isEqualTo(2);
    }

    @Test
    public void shouldApplyPromiseFlatMapMap() throws Exception {
        final Executor executor = givenAnExecutor();

        SolvablePromise<Integer> integerPromise = new SolvablePromise<>();

        integerPromise.solve(Try.success(1));

        final Promise<Integer> promise = integerPromise.flatmap(i -> executor.async(() -> i + 1)).map(i -> i + 1);

        assertThat(promise.getFuture().get(5, TimeUnit.SECONDS)).isEqualTo(3);
    }

    @Test(expected = ExecutionException.class)
    public void shouldNotApplyPromiseFlatMap() throws Exception {
        final Executor executor = givenAnExecutor();

        SolvablePromise<Integer> integerPromise = new SolvablePromise<>();

        integerPromise.solve(Try.failure(new SecurityException()));

        final Promise<Integer> promise = integerPromise.flatmap(i -> executor.async(() -> i + 1));

        promise.getFuture().get(5, TimeUnit.SECONDS);
    }

    @Test
    public void shouldFilterPromise() throws Exception {
        final Executor executor = givenAnExecutor();

        final SolvablePromise<Integer> integerPromise = new SolvablePromise<>();

        integerPromise.solve(Try.success(1));

        final Promise<Integer> promise = executor.async(() -> 1).filter(i -> i == 1).self();

        assertThat(promise.getFuture().get(5, TimeUnit.SECONDS)).isEqualTo(1);
    }

    @Test(expected = FilterException.class)
    public void shouldNotFilterPromise() throws Throwable {
        final Executor executor = givenAnExecutor();

        final SolvablePromise<Integer> integerPromise = new SolvablePromise<>();

        integerPromise.solve(Try.success(1));

        final Promise<Integer> promise = executor.async(() -> 1).filter(i -> i == 2).self();

        await(promise, Duration.ofSeconds(5)).orElseThrow();
    }

    private Executor givenAnExecutor() {
        return ExecutorHelper.create(Executors.newSingleThreadExecutor());
    }

}