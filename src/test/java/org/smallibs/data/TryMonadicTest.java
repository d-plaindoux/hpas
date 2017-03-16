/*
 * HPAS
 * https://github.com/d-plaindoux/hpas
 *
 * Copyright (c) 2016 Didier Plaindoux
 * Licensed under the LGPL2 license.
 */

package org.smallibs.data;

import org.junit.Test;
import org.smallibs.control.Monad;
import org.smallibs.type.TApp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.smallibs.data.TryHelper.monad;

public class TryMonadicTest {

    @Test
    public void shouldHaveMonadicTry() throws Exception {
        final Monad<Try, Integer, Try<Integer>> integerTry = monad(Try.success(1));

        assertThat(integerTry.self().<Integer>fold(x -> x, __ -> 0)).isEqualTo(1);
    }

    @Test
    public void shouldMapMonadicTry() throws Exception {
        final Monad<Try, Integer, Try<Integer>> integerTry = monad(Try.success(1));
        final TApp<Try, Integer, Try<Integer>> mappedIntegerTry = integerTry.map(i -> i + 1);

        assertThat(mappedIntegerTry.self().<Integer>fold(x -> x, __ -> 0)).isEqualTo(2);
    }

    @Test
    public void shouldFlatmapMonadicTry() throws Exception {
        final Monad<Try, Integer, Try<Integer>> integerTry = monad(Try.success(1));
        final TApp<Try, Integer, Try<Integer>> flatMappedIntegerTry = integerTry.flatmap(i -> Try.success(i + 1));

        assertThat(flatMappedIntegerTry.self().<Integer>fold(x -> x, __ -> 0)).isEqualTo(2);
    }

    @Test
    public void shouldApplypMonadicTry() throws Exception {
        final Monad<Try, Integer, Try<Integer>> integerTry = monad(Try.success(1));
        final TApp<Try, Integer, Try<Integer>> appliedIntegerTry = integerTry.apply(monad(Try.success(i -> i + 1)));

        assertThat(appliedIntegerTry.self().<Integer>fold(x -> x, __ -> 0)).isEqualTo(2);
    }

}
