/*
 * HPAS
 * https://github.com/d-plaindoux/hpas
 *
 * Copyright (c) 2016-2025 Didier Plaindoux
 * Licensed under the LGPL2 license.
 */

package org.smallibs.concurrent.execution;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.smallibs.concurrent.promise.Promise;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.CancellationException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.smallibs.concurrent.execution.ExecutorHelper.await;

public class ExecutorTest {

    @Test
    public void shouldRetrieveAnIntegerValue() throws Throwable {
        final Executor executor = givenAnExecutor();
        final Promise<Integer> integerPromise = executor.async(() -> 1);

        assertThat(await(integerPromise, Duration.ofSeconds(5)).orElseThrow()).isEqualTo(1);
    }

    @Test
    public void shouldRetrieveAnIntegerValueNotATimeOut() throws Throwable {
        final Executor executor = givenAnExecutor();
        final Promise<Integer> integerPromise = executor.async(() -> 1);

        assertThat(await(integerPromise, Duration.ofSeconds(1)).orElseThrow()).isEqualTo(1);
    }

    @Test
    public void shouldHaveAnIOExceptionException() throws Throwable {
        final Executor executor = givenAnExecutor();
        final Promise<Integer> integerPromise = executor.async(() -> {
            throw new IOException();
        });

        Assertions.assertThatThrownBy(() -> await(integerPromise, Duration.ofSeconds(5)).orElseThrow())
                .isInstanceOf(IOException.class);
    }

    @Test
    public void shouldHaveAnIOExceptionNotATimeout() throws Throwable {
        final Executor executor = givenAnExecutor();
        final Promise<Integer> integerPromise = executor.async(() -> {
            throw new IOException();
        });

        Assertions.assertThatThrownBy(() -> await(integerPromise, Duration.ofSeconds(1)).orElseThrow())
                .isInstanceOf(IOException.class);
    }

    @Test
    public void shouldHaveATimeoutException() throws Throwable {
        final Executor executor = givenAnExecutor();
        final Promise<Integer> integerPromise = executor.async(() -> {
            Thread.sleep(2000);
            return 1;
        });

        Assertions.assertThatThrownBy(() -> await(integerPromise, Duration.ofSeconds(1)).orElseThrow())
                .isInstanceOf(TimeoutException.class);
    }

    public void shouldHaveACancellationException() throws Throwable {
        final Executor executor = givenAnExecutor();
        final Promise<Integer> integerPromise = executor.async(() -> {
            Thread.sleep(10000);
            return 1;
        });

        integerPromise.getFuture().cancel(true);

        Assertions.assertThatThrownBy(() -> await(integerPromise, Duration.ofSeconds(5)).orElseThrow())
                .isInstanceOf(CancellationException.class);
    }

    //
    // Private behaviors
    //

    private Executor givenAnExecutor() {
        return ExecutorHelper.create(Executors.newSingleThreadScheduledExecutor());
    }

}