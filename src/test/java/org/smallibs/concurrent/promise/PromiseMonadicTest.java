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
import org.smallibs.control.Applicative;
import org.smallibs.control.Functor;
import org.smallibs.control.Monad;
import org.smallibs.type.HK;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.smallibs.concurrent.promise.PromiseHelper.applicative;
import static org.smallibs.concurrent.promise.PromiseHelper.functor;
import static org.smallibs.concurrent.promise.PromiseHelper.monad;

public class PromiseMonadicTest {

    @Test
    public void shouldHaveMonadicPromise() throws Exception {
        final Executor executor = givenAnExecutor();

        final Monad<Promise, Integer, Promise<Integer>> integerPromise = monad(executor.async(() -> 1).map(i -> i + 1));

        assertThat(integerPromise.self().getFuture().get(5, TimeUnit.SECONDS)).isEqualTo(2);
    }

    @Test
    public void shouldMapMonadicPromise() throws Exception {
        final Executor executor = givenAnExecutor();

        final Functor<Promise, Integer, Promise<Integer>> integerPromise = functor(executor.async(() -> 1).map(i -> i + 1));
        final HK<Promise, Integer, Promise<Integer>> mappedIntegerPromise = integerPromise.map(i -> i + 1);

        assertThat(mappedIntegerPromise.self().getFuture().get(5, TimeUnit.SECONDS)).isEqualTo(3);
    }

    @Test
    public void shouldApplyMonadicPromise() throws Exception {
        final Executor executor = givenAnExecutor();

        final Applicative<Promise, Integer, Promise<Integer>> integerPromise = applicative(executor.async(() -> 1).map(i -> i + 1));
        final Functor<Promise, Function<? super Integer, ? extends Integer>, Promise<Function<? super Integer, ? extends Integer>>> f = monad(executor.async(() -> i -> i + 1));
        final HK<Promise, Integer, Promise<Integer>> appliedIntegerPromise = integerPromise.apply(f);

        assertThat(appliedIntegerPromise.self().getFuture().get(5, TimeUnit.SECONDS)).isEqualTo(3);
    }

    @Test
    public void shouldFlatmapMonadicPromise() throws Exception {
        final Executor executor = givenAnExecutor();

        final Monad<Promise, Integer, Promise<Integer>> integerPromise = monad(executor.async(() -> 1).map(i -> i + 1));
        final HK<Promise, Integer, Promise<Integer>> mappedIntegerPromise = integerPromise.flatmap(i -> executor.async(() -> i + 1));

        assertThat(mappedIntegerPromise.self().getFuture().get(5, TimeUnit.SECONDS)).isEqualTo(3);
    }

    private Executor givenAnExecutor() {
        return ExecutorHelper.create(Executors.newSingleThreadExecutor());
    }

}