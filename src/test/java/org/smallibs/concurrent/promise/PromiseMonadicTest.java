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
import org.smallibs.data.Monad;
import org.smallibs.data.TApp;

import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

public class PromiseMonadicTest {

    @Test
    public void shouldHaveMonadicPromise() throws Exception {
        final Executor executor = givenAnExecutor();

        final Monad<Promise, Integer, Promise<Integer>> integerPromise = executor.async(() -> 1).map(i -> i + 1).monad();

        assertThat(integerPromise.self().getFuture().get()).isEqualTo(2);
    }

    @Test
    public void shouldMapMonadicPromise() throws Exception {
        final Executor executor = givenAnExecutor();

        final Monad<Promise, Integer, Promise<Integer>> integerPromise = executor.async(() -> 1).map(i -> i + 1).monad();
        final TApp<Promise, Integer, Promise<Integer>> mappedIntegerPromise = integerPromise.map(i -> i + 1);

        assertThat(mappedIntegerPromise.self().getFuture().get()).isEqualTo(3);
    }

    @Test
    public void shouldFlatmapMonadicPromise() throws Exception {
        final Executor executor = givenAnExecutor();

        final Monad<Promise, Integer, Promise<Integer>> integerPromise = executor.async(() -> 1).map(i -> i + 1).monad();
        final TApp<Promise, Integer, Promise<Integer>> mappedIntegerPromise = integerPromise.flatmap(i -> executor.async(() -> i + 1));

        assertThat(mappedIntegerPromise.self().getFuture().get()).isEqualTo(3);
    }

    private Executor givenAnExecutor() {
        return ExecutorBuilder.create(Executors.newSingleThreadExecutor());
    }


}