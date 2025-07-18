/*
 * HPAS
 * https://github.com/d-plaindoux/hpas
 *
 * Copyright (c) 2016-2025 Didier Plaindoux
 * Licensed under the LGPL2 license.
 */

package org.smallibs.data;

import org.junit.jupiter.api.Test;
import org.smallibs.control.Monad;
import org.smallibs.type.HK;

import static org.assertj.core.api.Assertions.assertThat;
import static org.smallibs.data.TryHelper.monad;

public class TryMonadicTest {

    @Test
    public void shouldHaveMonadicTry() {
        final Monad<Try, Integer, Try<Integer>> integerTry = monad(Try.success(1));

        assertThat(integerTry.self().<Integer>fold(x -> x, __ -> 0)).isEqualTo(1);
    }

    @Test
    public void shouldHaveAcceptedMonadicTry() {
        final Monad<Try, Integer, Try<Integer>> integerTry = monad(monad(Try.success(1)).apply(HK::self));

        assertThat(integerTry.self().<Integer>fold(x -> x, __ -> 0)).isEqualTo(1);
    }

    @Test
    public void shouldMapMonadicTry() {
        final Monad<Try, Integer, Try<Integer>> integerTry = monad(Try.success(1));
        final HK<Try, Integer, Try<Integer>> mappedIntegerTry = integerTry.map(i -> i + 1);

        assertThat(mappedIntegerTry.self().<Integer>fold(x -> x, __ -> 0)).isEqualTo(2);
    }

    @Test
    public void shouldFlatmapMonadicTry() {
        final Monad<Try, Integer, Try<Integer>> integerTry = monad(Try.success(1));
        final HK<Try, Integer, Try<Integer>> flatMappedIntegerTry = integerTry.flatmap(i -> Try.success(i + 1));

        assertThat(flatMappedIntegerTry.self().<Integer>fold(x -> x, __ -> 0)).isEqualTo(2);
    }

    @Test
    public void shouldApplypMonadicTry() {
        final Monad<Try, Integer, Try<Integer>> integerTry = monad(Try.success(1));
        final HK<Try, Integer, Try<Integer>> appliedIntegerTry = integerTry.apply(monad(Try.success(i -> i + 1)));

        assertThat(appliedIntegerTry.self().<Integer>fold(x -> x, __ -> 0)).isEqualTo(2);
    }

}
