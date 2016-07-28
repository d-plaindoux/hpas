/*
 * HPAS
 * https://github.com/d-plaindoux/hpas
 *
 * Copyright (c) 2016 Didier Plaindoux
 * Licensed under the LGPL2 license.
 */

package org.smallibs.concurrent.promise;

import org.junit.Test;
import org.smallibs.concurrent.asynchronous.Executor;
import org.smallibs.concurrent.asynchronous.ExecutorBuilder;
import org.smallibs.type.TApp;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.assertj.core.api.Assertions.assertThat;

public class FlatMappedPromiseTest {

    @Test
    public void shouldApplyOnSuccess() throws Exception {
        final AtomicBoolean aBoolean = new AtomicBoolean(false);
        final Executor executor = givenAnExecutor();

        final Promise<Integer> integerPromise = executor.async(() -> 1).
                flatmap(i -> executor.async(() -> i + 1));

        integerPromise.onSuccess(i -> aBoolean.set(true));
        integerPromise.getFuture().get();

        assertThat(aBoolean.get()).isTrue();
    }

    @Test
    public void shouldApplyOnSuccessAfterOneSecond() throws Exception {
        final AtomicBoolean aBoolean = new AtomicBoolean(false);
        final Executor executor = givenAnExecutor();

        final Promise<Integer> integerPromise = executor.async(() -> {
            Thread.sleep(1000);
            return 1;
        }).flatmap(i -> executor.async(() -> i + 1));

        integerPromise.onSuccess(i -> aBoolean.set(true));
        integerPromise.getFuture().get();

        assertThat(aBoolean.get()).isTrue();
    }

    @Test
    public void shouldApplyOnFailure() throws Exception {
        final AtomicBoolean aBoolean = new AtomicBoolean(false);
        final Executor executor = givenAnExecutor();

        final Promise<Integer> integerPromise = executor.<Integer>async(() -> {
            throw new SecurityException();
        }).flatmap(i -> executor.async(() -> i + 1));

        integerPromise.onFailure(i -> aBoolean.set(true));

        try {
            integerPromise.getFuture().get();
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
        }).flatmap(i -> executor.async(() -> i + 1));

        integerPromise.onFailure(i -> aBoolean.set(true));

        try {
            integerPromise.getFuture().get();
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
        }).flatmap(i -> executor.async(() -> i + 1));

        integerPromise.onComplete(i -> aBoolean.set(true));

        try {
            integerPromise.getFuture().get();
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
        }).flatmap(i -> executor.async(() -> i + 1));

        integerPromise.onComplete(i -> aBoolean.set(true));

        try {
            integerPromise.getFuture().get();
        } catch (ExecutionException e) {
            // consume
        }

        assertThat(aBoolean.get()).isTrue();
    }

    @Test
    public void shouldApplyPromiseMap() throws Exception {
        final Executor executor = givenAnExecutor();

        final TApp<Promise, Integer, Promise<Integer>> map = executor.async(() -> 1).map(i -> i + 1);
        final TApp<Promise, Integer, Promise<Integer>> map1 = map.self().map(i -> i + 1);

        assertThat(map1.self().getFuture().get()).isEqualTo(3);
    }

    @Test(expected = ExecutionException.class)
    public void shouldNotApplyPromiseMap() throws Exception {
        final Executor executor = givenAnExecutor();

        final Promise<Integer> integerPromise = executor.<Integer>async(() -> {
            throw new SecurityException();
        }).flatmap(i -> executor.async(() -> i + 1)).map(i -> i + 1);

        integerPromise.getFuture().get();
    }

    @Test
    public void shouldApplyPromiseFlatMap() throws Exception {
        final Executor executor = givenAnExecutor();

        final Promise<Integer> integerPromise =
                executor.async(() -> 1).map(i -> i + 1).
                        flatmap(i -> executor.async(() -> i + 1));

        assertThat(integerPromise.getFuture().get()).isEqualTo(3);
    }

    @Test(expected = ExecutionException.class)
    public void shouldNotApplyPromiseFlatMap() throws Exception {
        final Executor executor = givenAnExecutor();

        final Promise<Integer> integerPromise = executor.<Integer>async(() -> {
            throw new SecurityException();
        }).flatmap(i -> executor.async(() -> i + 1)).flatmap(i -> executor.async(() -> i + 1));

        integerPromise.getFuture().get();
    }

    private Executor givenAnExecutor() {
        return ExecutorBuilder.create(Executors.newSingleThreadExecutor());
    }

}