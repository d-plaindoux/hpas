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

public class TryMonadicTest {

    @Test
    public void shouldHaveMonadicTry() throws Exception {
        final Monad<Try, Integer, Try<Integer>> integerTry = Try.success(1).monad();

        assertThat(integerTry.self().success()).isEqualTo(1);
    }

    @Test
    public void shouldMapMonadicTry() throws Exception {
        final Monad<Try, Integer, Try<Integer>> integerTry = Try.success(1).monad();
        final TApp<Try, Integer, Try<Integer>> mappedIntegerTry = integerTry.map(i -> i + 1);

        assertThat(mappedIntegerTry.self().success()).isEqualTo(2);
    }

    @Test
    public void shouldFlatmapMonadicTry() throws Exception {
        final Monad<Try, Integer, Try<Integer>> integerTry = Try.success(1).monad();
        final TApp<Try, Integer, Try<Integer>> flatMappedIntegerTry = integerTry.flatmap(i -> Try.success(i + 1));

        assertThat(flatMappedIntegerTry.self().success()).isEqualTo(2);
    }
}
