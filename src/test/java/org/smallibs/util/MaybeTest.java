package org.smallibs.util;

import org.junit.Test;

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
    public void shouldMapSome() throws Exception {
        assertThat(Maybe.some(1).map(i -> i+1).hasSome()).isTrue();
    }

    @Test
    public void shouldMapSomeRetrieveValue() throws Exception {
        assertThat(Maybe.some(1).map(i -> i+1).get()).isEqualTo(2);
    }

    @Test
    public void shouldMapNone() throws Exception {
        assertThat(Maybe.<Integer>none().map(i -> i+1).hasSome()).isFalse();
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
}
