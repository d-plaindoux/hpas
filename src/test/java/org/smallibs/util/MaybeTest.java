package org.smallibs.util;

import org.junit.Test;
import org.smallibs.exception.NoValueException;

import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

public class MaybeTest {

    @Test
    public void shouldHaveSome() throws Exception {
        assertThat(Maybe.some(1).hasSome()).isTrue();
    }

    @Test
    public void shouldHaveSomeValue() throws Exception {
        assertThat(Maybe.some(1).get()).isEqualTo(1);
    }

    @Test
    public void shouldHaveNone() throws Exception {
        assertThat(Maybe.none().hasSome()).isFalse();
    }

    @Test(expected = IllegalAccessError.class)
    public void shouldHaveNoneException() throws Exception {
        Maybe.none().get();
    }

    @Test
    public void shouldFilterSome() throws Exception {
        assertThat(Maybe.some(1).filter(i -> i == 1).hasSome()).isTrue();
    }

    @Test
    public void shouldFilterSomeRetrieveValue() throws Exception {
        assertThat(Maybe.some(1).filter(i -> i == 1).get()).isEqualTo(1);
    }

    @Test
    public void shouldNotFilterSome() throws Exception {
        assertThat(Maybe.some(1).filter(i -> i != 1).hasSome()).isFalse();
    }

    @Test
    public void shouldFilterNone() throws Exception {
        assertThat(Maybe.<Integer>none().filter(i -> i == 1).hasSome()).isFalse();
    }

    @Test
    public void shouldMapSome() throws Exception {
        assertThat(Maybe.some(1).map(i -> i + 1).hasSome()).isTrue();
    }

    @Test
    public void shouldMapSomeRetrieveValue() throws Exception {
        assertThat(Maybe.some(1).map(i -> i + 1).get()).isEqualTo(2);
    }

    @Test
    public void shouldMapNone() throws Exception {
        assertThat(Maybe.<Integer>none().map(i -> i + 1).hasSome()).isFalse();
    }

    @Test
    public void shouldFlatMapSomeToSome() throws Exception {
        assertThat(Maybe.some(1).flatmap(Maybe::some).hasSome()).isTrue();
    }

    @Test
    public void shouldFlatMapSomeToNone() throws Exception {
        assertThat(Maybe.some(1).flatmap(i -> Maybe.none()).hasSome()).isFalse();
    }

    @Test
    public void shouldFlatMapNone() throws Exception {
        assertThat(Maybe.<Integer>none().flatmap(Maybe::some).hasSome()).isFalse();
    }

    @Test
    public void shouldOnSomeSome() throws Exception {
        final AtomicInteger integer = new AtomicInteger(0);

        Maybe.some(1).onSome(integer::set);

        assertThat(integer.get()).isEqualTo(1);
    }

    @Test
    public void shouldOnSomeNone() throws Exception {
        final AtomicInteger integer = new AtomicInteger(0);

        Maybe.<Integer>none().onSome(integer::set);

        assertThat(integer.get()).isEqualTo(0);
    }

    @Test
    public void shouldOrElseSome() throws Exception {
        assertThat(Maybe.some(1).orElse(0)).isEqualTo(1);
    }

    @Test
    public void shouldOrElseNone() throws Exception {
        assertThat(Maybe.<Integer>none().orElse(0)).isEqualTo(0);
    }

    @Test
    public void shouldOrLazyElseSome() throws Exception {
        assertThat(Maybe.some(1).orElse(() -> 0)).isEqualTo(1);
    }

    @Test
    public void shouldOrLazyElseNone() throws Exception {
        assertThat(Maybe.<Integer>none().orElse(() -> 0)).isEqualTo(0);
    }

    @Test
    public void shouldToTrySome() throws Exception {
        assertThat(Maybe.some(1).toTry().isSuccess()).isTrue();
    }

    @Test
    public void shouldToTrySomeValue() throws Exception {
        assertThat(Maybe.some(1).toTry().success()).isEqualTo(1);
    }

    @Test
    public void shouldToTryNone() throws Exception {
        assertThat(Maybe.none().toTry().isSuccess()).isFalse();
    }

    @Test(expected = NoValueException.class)
    public void shouldToTryNoneValue() throws Throwable {
        throw Maybe.none().toTry().failure();
    }
}
