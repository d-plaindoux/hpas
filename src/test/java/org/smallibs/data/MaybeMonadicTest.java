/*
 * HPAS
 * https://github.com/d-plaindoux/hpas
 *
 * Copyright (c) 2016 Didier Plaindoux
 * Licensed under the LGPL2 license.
 */

package org.smallibs.data;

import org.junit.Test;
import org.smallibs.control.Applicative;
import org.smallibs.control.Functor;
import org.smallibs.control.Monad;
import org.smallibs.type.Kind;

import static org.assertj.core.api.Assertions.assertThat;
import static org.smallibs.data.MaybeHelper.monad;

public class MaybeMonadicTest {

    @Test
    public void shouldHaveMonadicMaybe() throws Exception {
        final Monad<Maybe, Integer, Maybe<Integer>> integerMaybe = monad(Maybe.some(1));

        assertThat(integerMaybe.self().fold(x -> x, () -> 0)).isEqualTo(1);
    }

    @Test
    public void shouldMapMonadicMaybe() throws Exception {
        final Functor<Maybe, Integer, Maybe<Integer>> integerMaybe = monad(Maybe.some(1));
        final Kind<Maybe, Integer, Maybe<Integer>> mappedIntegerMaybe = integerMaybe.map(i -> i + 1);

        assertThat(mappedIntegerMaybe.self().fold(x -> x, () -> 0)).isEqualTo(2);
    }

    @Test
    public void shouldApplypMonadicMaybe() throws Exception {
        final Applicative<Maybe, Integer, Maybe<Integer>> integerMaybe = monad(Maybe.some(1));
        final Kind<Maybe, Integer, Maybe<Integer>> appliedIntegerMaybe = integerMaybe.apply(monad(Maybe.some(i -> i + 1)));

        assertThat(appliedIntegerMaybe.self().fold(x -> x, () -> 0)).isEqualTo(2);
    }

    @Test
    public void shouldFlatmapMonadicMaybe() throws Exception {
        final Monad<Maybe, Integer, Maybe<Integer>> integerMaybe = monad(Maybe.some(1));
        final Kind<Maybe, Integer, Maybe<Integer>> flatMappedIntegerMaybe = integerMaybe.flatmap(i -> Maybe.some(i + 1));

        assertThat(flatMappedIntegerMaybe.self().fold(x -> x, () -> 0)).isEqualTo(2);
    }
}
