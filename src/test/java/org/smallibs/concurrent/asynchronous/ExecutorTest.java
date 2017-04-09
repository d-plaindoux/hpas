/*
 * HPAS
 * https://github.com/d-plaindoux/hpas
 *
 * Copyright (c) 2016 Didier Plaindoux
 * Licensed under the LGPL2 license.
 */

package org.smallibs.concurrent.asynchronous;

import org.junit.Test;
import org.smallibs.concurrent.promise.Promise;

import java.util.concurrent.CancellationException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.assertj.core.api.Assertions.assertThat;

public class ExecutorTest {

    @Test
    public void shouldRetrieveAnIntegerValue() throws Throwable {
        final Executor executor = givenAnExecutor();
        final Promise<Integer> integerPromise = executor.async(() -> 1);

        assertThat(executor.await(integerPromise).orElseThrow()).isEqualTo(1);
    }

    @Test
    public void shouldRetrieveAnIntegerValueNotATimeOut() throws Throwable {
        final Executor executor = givenAnExecutor();
        final Promise<Integer> integerPromise = executor.async(() -> 1);

        assertThat(executor.await(integerPromise, 1, TimeUnit.SECONDS).orElseThrow()).isEqualTo(1);
    }

    @Test(expected = RuntimeException.class)
    public void shouldHaveARuntimeException() throws Throwable {
        final Executor executor = givenAnExecutor();
        final Promise<Integer> integerPromise = executor.async(() -> {
            throw new RuntimeException();
        });

        executor.await(integerPromise).orElseThrow();
    }

    @Test(expected = RuntimeException.class)
    public void shouldHaveARuntimeExceptionNotATimeout() throws Throwable {
        final Executor executor = givenAnExecutor();
        final Promise<Integer> integerPromise = executor.async(() -> {
            throw new RuntimeException();
        });

        executor.await(integerPromise, 1, TimeUnit.SECONDS).orElseThrow();
    }

    @Test(expected = TimeoutException.class)
    public void shouldHaveATimeoutException() throws Throwable {
        final Executor executor = givenAnExecutor();
        final Promise<Integer> integerPromise = executor.async(() -> {
            Thread.sleep(2000);
            return 1;
        });

        executor.await(integerPromise, 1, TimeUnit.SECONDS).orElseThrow();
    }

    @Test(expected = CancellationException.class)
    public void shouldHaveARuntimeCancellationException() throws Throwable {
        final Executor executor = givenAnExecutor();
        final Promise<Integer> integerPromise = executor.async(() -> {
            Thread.sleep(10000);
            return 1;
        });

        integerPromise.getFuture().cancel(true);

        executor.await(integerPromise).orElseThrow();
    }

    //
    // Private behaviors
    //

    private Executor givenAnExecutor() {
        return ExecutorBuilder.create(Executors.newSingleThreadScheduledExecutor());
    }

}