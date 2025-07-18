/*
 * HPAS
 * https://github.com/d-plaindoux/hpas
 *
 * Copyright (c) 2016-2025 Didier Plaindoux
 * Licensed under the LGPL2 license.
 */

package org.smallibs.concurrent.promise;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.smallibs.concurrent.execution.Executor;
import org.smallibs.concurrent.execution.ExecutorHelper;
import org.smallibs.exception.FilterException;

import java.time.Duration;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.smallibs.concurrent.execution.ExecutorHelper.await;

public class MappedPromiseTest {

    @Test
    public void shouldApplyMap() throws Exception {
        final Executor executor = givenAnExecutor();

        final Promise<Integer> integerPromise = executor.async(() -> 1).and(i -> i + 1);

        assertThat(integerPromise.getFuture().get(5, TimeUnit.SECONDS)).isEqualTo(2);
    }

    @Test
    public void shouldApplyOnSuccess() throws Exception {
        final AtomicBoolean aBoolean = new AtomicBoolean(false);
        final Executor executor = givenAnExecutor();

        final Promise<Integer> integerPromise = executor.async(() -> 1).and(i -> i + 1);

        integerPromise.onSuccess(i -> aBoolean.set(true));
        integerPromise.getFuture().get(5, TimeUnit.SECONDS);

        assertThat(aBoolean.get()).isTrue();
    }

    @Test
    public void shouldApplyOnSuccessAfterOneSecond() throws Exception {
        final AtomicBoolean aBoolean = new AtomicBoolean(false);
        final Executor executor = givenAnExecutor();

        final Promise<Integer> integerPromise = executor.async(() -> {
            Thread.sleep(1000);
            return 1;
        }).and(i -> i + 1);

        integerPromise.onSuccess(i -> aBoolean.set(true));
        integerPromise.getFuture().get(5, TimeUnit.SECONDS);

        assertThat(aBoolean.get()).isTrue();
    }

    @Test
    public void shouldApplyOnFailure() throws Exception {
        final AtomicBoolean aBoolean = new AtomicBoolean(false);
        final Executor executor = givenAnExecutor();

        final Promise<Integer> integerPromise = executor.<Integer>async(() -> {
            throw new SecurityException();
        }).and(i -> i + 1);

        integerPromise.onFailure(i -> aBoolean.set(true));

        try {
            integerPromise.getFuture().get(5, TimeUnit.SECONDS);
        } catch (ExecutionException e) {
            // consume
        }

        assertThat(aBoolean.get()).isTrue();
    }

    @Test
    public void shouldApplyOnFailureAfterOneSecond() throws Exception {
        final AtomicBoolean aBoolean = new AtomicBoolean(false);
        final Executor executor = givenAnExecutor();

        final Promise<Integer> integerPromise = executor.<Integer>async(() -> {
            Thread.sleep(1000);
            throw new SecurityException();
        }).and(i -> i + 1);

        integerPromise.onFailure(i -> aBoolean.set(true));

        try {
            integerPromise.getFuture().get(5, TimeUnit.SECONDS);
        } catch (ExecutionException e) {
            // consume
        }

        assertThat(aBoolean.get()).isTrue();
    }

    @Test
    public void shouldApplyOnComplete() throws Exception {
        final AtomicBoolean aBoolean = new AtomicBoolean(false);
        final Executor executor = givenAnExecutor();

        final Promise<Integer> integerPromise = executor.<Integer>async(() -> {
            throw new SecurityException();
        }).and(i -> i + 1);

        integerPromise.onComplete(i -> aBoolean.set(true));

        try {
            integerPromise.getFuture().get(5, TimeUnit.SECONDS);
        } catch (ExecutionException e) {
            // consume
        }

        assertThat(aBoolean.get()).isTrue();
    }

    @Test
    public void shouldApplyOnCompleteAfterOneSecond() throws Exception {
        final AtomicBoolean aBoolean = new AtomicBoolean(false);
        final Executor executor = givenAnExecutor();

        final Promise<Integer> integerPromise = executor.<Integer>async(() -> {
            Thread.sleep(1000);
            throw new SecurityException();
        }).and(i -> i + 1);

        integerPromise.onComplete(i -> aBoolean.set(true));

        try {
            integerPromise.getFuture().get(5, TimeUnit.SECONDS);
        } catch (ExecutionException e) {
            // consume
        }

        assertThat(aBoolean.get()).isTrue();
    }

    @Test
    public void shouldApplyPromiseMap() throws Exception {
        final Executor executor = givenAnExecutor();

        final Promise<Integer> integerPromise = executor.async(() -> 1).
                and(i -> i + 1).
                and(i -> i + 1);

        assertThat(integerPromise.getFuture().get(5, TimeUnit.SECONDS)).isEqualTo(3);
    }

    @Test
    public void shouldNotApplyPromiseMap() {
        final Executor executor = givenAnExecutor();

        final Promise<Integer> integerPromise = executor.<Integer>async(() -> {
            throw new SecurityException();
        }).and(i -> i + 1).and(i -> i + 1);

        Assertions.assertThatThrownBy(() -> integerPromise.getFuture().get(5, TimeUnit.SECONDS))
                .cause()
                .isInstanceOf(SecurityException.class);
    }

    @Test
    public void shouldApplyPromiseFlatMap() throws Exception {
        final Executor executor = givenAnExecutor();

        final Promise<Integer> integerPromise = executor.async(() -> 1).
                and(i -> i + 1).
                then(i -> executor.async(() -> i + 1));

        assertThat(integerPromise.getFuture().get(5, TimeUnit.SECONDS)).isEqualTo(3);
    }

    @Test
    public void shouldNotApplyPromiseFlatMap() {
        final Executor executor = givenAnExecutor();

        final Promise<Integer> integerPromise = executor.<Integer>async(() -> {
            throw new SecurityException();
        }).and(i -> i + 1).then(i -> executor.async(() -> i + 1));

        Assertions.assertThatThrownBy(() -> integerPromise.getFuture().get(5, TimeUnit.SECONDS))
                .cause()
                .isInstanceOf(SecurityException.class);
    }

    @Test
    public void shouldFilterPromise() throws Exception {
        final Executor executor = givenAnExecutor();

        final Promise<Integer> integerPromise = executor.async(() -> 1).
                map(i -> i + 1).
                filter(i -> i == 2).
                self();

        assertThat(integerPromise.getFuture().get(5, TimeUnit.SECONDS)).isEqualTo(2);
    }

    @Test
    public void shouldNotFilterPromise() {
        final Executor executor = givenAnExecutor();

        final Promise<Integer> integerPromise = executor.async(() -> 1).
                map(i -> i + 1).
                filter(i -> i == 3).
                self();

        Assertions.assertThatThrownBy(() -> await(integerPromise, Duration.ofSeconds(5)).orElseThrow())
                .isInstanceOf(FilterException.class);
    }

    private Executor givenAnExecutor() {
        return ExecutorHelper.create(Executors.newSingleThreadExecutor());
    }

}