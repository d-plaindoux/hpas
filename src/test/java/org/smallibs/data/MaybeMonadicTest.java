/*
 * HPAS
 * https://github.com/d-plaindoux/hpas
 *
 * Copyright (c) 2016-2025 Didier Plaindoux
 * Licensed under the LGPL2 license.
 */

package org.smallibs.data;

import org.junit.jupiter.api.Test;
import org.smallibs.control.Applicative;
import org.smallibs.control.Functor;
import org.smallibs.control.Monad;
import org.smallibs.type.HK;

import static org.assertj.core.api.Assertions.assertThat;
import static org.smallibs.data.MaybeHelper.monad;

@SuppressWarnings("rawtypes")
public class MaybeMonadicTest {

    @Test
    public void shouldHaveMonadicMaybe() {
        final Monad<Maybe, Integer, Maybe<Integer>> integerMaybe = monad(Maybe.some(1));

        assertThat(integerMaybe.self().fold(x -> x, () -> 0)).isEqualTo(1);
    }

    @Test
    public void shouldHaveAcceptedMonadicMaybe() {
        final Monad<Maybe, Integer, Maybe<Integer>> integerMaybe = monad(monad(Maybe.some(1)).apply(HK::self));

        assertThat(integerMaybe.self().fold(x -> x, () -> 0)).isEqualTo(1);
    }

    @Test
    public void shouldMapMonadicMaybe() {
        final Functor<Maybe, Integer, Maybe<Integer>> integerMaybe = monad(Maybe.some(1));
        final HK<Maybe, Integer, Maybe<Integer>> mappedIntegerMaybe = integerMaybe.map(i -> i + 1);

        assertThat(mappedIntegerMaybe.self().fold(x -> x, () -> 0)).isEqualTo(2);
    }

    @Test
    public void shouldApplypMonadicMaybe() {
        final Applicative<Maybe, Integer, Maybe<Integer>> integerMaybe = monad(Maybe.some(1));
        final HK<Maybe, Integer, Maybe<Integer>> appliedIntegerMaybe = integerMaybe.apply(monad(Maybe.some(i -> i + 1)));

        assertThat(appliedIntegerMaybe.self().fold(x -> x, () -> 0)).isEqualTo(2);
    }

    @Test
    public void shouldFlatmapMonadicMaybe() {
        final Monad<Maybe, Integer, Maybe<Integer>> integerMaybe = monad(Maybe.some(1));
        final HK<Maybe, Integer, Maybe<Integer>> flatMappedIntegerMaybe = integerMaybe.flatmap(i -> Maybe.some(i + 1));

        assertThat(flatMappedIntegerMaybe.self().fold(x -> x, () -> 0)).isEqualTo(2);
    }
}
