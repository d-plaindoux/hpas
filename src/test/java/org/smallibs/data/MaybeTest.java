/*
 * HPAS
 * https://github.com/d-plaindoux/hpas
 *
 * Copyright (c) 2016-2025 Didier Plaindoux
 * Licensed under the LGPL2 license.
 */

package org.smallibs.data;

import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

public class MaybeTest {

    @Test
    public void shouldHaveSome() {
        assertThat(Maybe.some(1).hasSome()).isTrue();
    }

    @Test
    public void shouldHaveSomeValue() {
        assertThat(Maybe.some(1).fold(x -> x, () -> 0)).isEqualTo(1);
    }

    @Test
    public void shouldHaveNone() {
        assertThat(Maybe.none().hasSome()).isFalse();
    }

    @Test
    public void shouldFilterSome() {
        assertThat(Maybe.some(1).filter(i -> i == 1).hasSome()).isTrue();
    }

    @Test
    public void shouldFilterSomeRetrieveValue() {
        assertThat(Maybe.some(1).filter(i -> i == 1).fold(x -> x, () -> 0)).isEqualTo(1);
    }

    @Test
    public void shouldNotFilterSome() {
        assertThat(Maybe.some(1).filter(i -> i != 1).hasSome()).isFalse();
    }

    @Test
    public void shouldFilterNone() {
        assertThat(Maybe.<Integer>none().filter(i -> i == 1).hasSome()).isFalse();
    }

    @Test
    public void shouldMapSome() {
        assertThat(Maybe.some(1).map(i -> i + 1).hasSome()).isTrue();
    }

    @Test
    public void shouldMapSomeRetrieveValue() {
        assertThat(Maybe.some(1).map(i -> i + 1).fold(x -> x, () -> 0)).isEqualTo(2);
    }

    @Test
    public void shouldMapNone() {
        assertThat(Maybe.<Integer>none().map(i -> i + 1).hasSome()).isFalse();
    }

    @Test
    public void shouldFlatMapSomeToSome() {
        assertThat(Maybe.some(1).flatmap(Maybe::some).hasSome()).isTrue();
    }

    @Test
    public void shouldFlatMapSomeToNone() {
        assertThat(Maybe.some(1).flatmap(i -> Maybe.none()).hasSome()).isFalse();
    }

    @Test
    public void shouldFlatMapNone() {
        assertThat(Maybe.<Integer>none().flatmap(Maybe::some).hasSome()).isFalse();
    }

    @Test
    public void shouldOnSomeSome() {
        final AtomicInteger integer = new AtomicInteger(0);

        Maybe.some(1).onSome(integer::set);

        assertThat(integer.get()).isEqualTo(1);
    }

    @Test
    public void shouldOnSomeNone() {
        final AtomicInteger integer = new AtomicInteger(0);

        Maybe.<Integer>none().onSome(integer::set);

        assertThat(integer.get()).isEqualTo(0);
    }

    @Test
    public void shouldOnNoneSome() {
        final AtomicInteger integer = new AtomicInteger(0);

        Maybe.some(1).onNone(() -> integer.set(1));

        assertThat(integer.get()).isEqualTo(0);
    }

    @Test
    public void shouldOnNoneNone() {
        final AtomicInteger integer = new AtomicInteger(0);

        Maybe.none().onNone(() -> integer.set(1));

        assertThat(integer.get()).isEqualTo(1);
    }

    @Test
    public void shouldOrElseSome() {
        assertThat(Maybe.some(1).orElse(0)).isEqualTo(1);
    }

    @Test
    public void shouldOrElseNone() {
        assertThat(Maybe.<Integer>none().orElse(0)).isEqualTo(0);
    }

    @Test
    public void shouldOrLazyElseSome() {
        assertThat(Maybe.some(1).orElse(() -> 0)).isEqualTo(1);
    }

    @Test
    public void shouldOrLazyElseNone() {
        assertThat(Maybe.<Integer>none().orElse(() -> 0)).isEqualTo(0);
    }

    @Test
    public void shouldToTrySome() {
        assertThat(MaybeHelper.toTry(Maybe.some(1)).isSuccess()).isTrue();
    }

    @Test
    public void shouldToTrySomeValue() {
        assertThat(MaybeHelper.toTry(Maybe.some(1)).fold((Integer x) -> x, __ -> 0)).isEqualTo(1);
    }

    @Test
    public void shouldToTryNone() {
        assertThat(MaybeHelper.toTry(Maybe.none()).isSuccess()).isFalse();
    }

}
