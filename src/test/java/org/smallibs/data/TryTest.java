/*
 * HPAS
 * https://github.com/d-plaindoux/hpas
 *
 * Copyright (c) 2016 Didier Plaindoux
 * Licensed under the LGPL2 license.
 */

package org.smallibs.data;

import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

public class TryTest {

    @Test
    public void shouldHaveSuccess() throws Exception {
        assertThat(Try.success(1).isSuccess()).isTrue();
    }

    @Test
    public void shouldHaveSuccessValue() throws Exception {
        assertThat(Try.success(1).<Integer>fold(x -> x, __ -> 0)).isEqualTo(1);
    }

    @Test
    public void shouldHaveFailure() throws Exception {
        assertThat(Try.failure(new Exception()).isSuccess()).isFalse();
    }


    @Test
    public void shouldFilterSuccess() throws Exception {
        assertThat(Try.success(1).filter(i -> i == 1).isSuccess()).isTrue();
    }

    @Test
    public void shouldFilterSuccessRetrieveValue() throws Exception {
        assertThat(Try.success(1).filter(i -> i == 1).<Integer>fold(x -> x, __ -> 0)).isEqualTo(1);
    }

    @Test
    public void shouldNotFilterSuccess() throws Exception {
        assertThat(Try.success(1).filter(i -> i != 1).isSuccess()).isFalse();
    }

    @Test
    public void shouldFilterFailure() throws Exception {
        assertThat(Try.<Integer>failure(new Exception()).filter(i -> i == 1).isSuccess()).isFalse();
    }

    @Test
    public void shouldMapSuccess() throws Exception {
        assertThat((Try.success(1).map(i -> i + 1)).isSuccess()).isTrue();
    }

    @Test
    public void shouldMapSuccessRetrieveValue() throws Exception {
        assertThat((Try.success(1).map(i -> i + 1)).<Integer>fold(x -> x, __ -> 0)).isEqualTo(2);
    }

    @Test
    public void shouldMapFailure() throws Exception {
        assertThat(Try.<Integer>failure(new Exception()).map(i -> i + 1).isSuccess()).isFalse();
    }

    @Test
    public void shouldFlatMapSuccessToFailure() throws Exception {
        assertThat(Try.success(1).flatmap(i -> Try.failure(new Exception())).isSuccess()).isFalse();
    }

    @Test
    public void shouldFlatMapFailure() throws Exception {
        assertThat(Try.<Integer>failure(new Exception()).flatmap(Try::success).isSuccess()).isFalse();
    }

    @Test
    public void shouldOnSuccessSuccess() throws Exception {
        final AtomicInteger integer = new AtomicInteger(0);

        Try.success(1).onSuccess(integer::set);

        assertThat(integer.get()).isEqualTo(1);
    }

    @Test
    public void shouldOnSuccessFailure() throws Exception {
        final AtomicInteger integer = new AtomicInteger(0);

        Try.<Integer>failure(new Exception()).onSuccess(integer::set);

        assertThat(integer.get()).isEqualTo(0);
    }

    @Test
    public void shouldOnFailureSuccess() throws Exception {
        final AtomicInteger integer = new AtomicInteger(0);

        Try.success(1).onFailure(throwable -> integer.set(0));

        assertThat(integer.get()).isEqualTo(0);
    }

    @Test
    public void shouldOnFailureFailure() throws Exception {
        final AtomicInteger integer = new AtomicInteger(0);

        Try.<Integer>failure(new Exception()).onFailure(throwable -> integer.set(1));

        assertThat(integer.get()).isEqualTo(1);
    }

    @Test
    public void shouldRecoverWithSuccess() throws Exception {
        assertThat(Try.success(1).recoverWith(0)).isEqualTo(1);
    }

    @Test
    public void shouldRecoverWithFailure() throws Exception {
        assertThat(Try.<Integer>failure(new Exception()).recoverWith(0)).isEqualTo(0);
    }

    @Test
    public void shouldOrLazyElseSuccess() throws Exception {
        assertThat(Try.success(1).recoverWith(__ -> 0)).isEqualTo(1);
    }

    @Test
    public void shouldOrLazyElseFailure() throws Exception {
        assertThat(Try.<Integer>failure(new Exception()).recoverWith(__ -> 0)).isEqualTo(0);
    }

    @Test
    public void shouldToTrySuccess() throws Exception {
        assertThat(TryHelper.toMaybe(Try.success(1)).hasSome()).isTrue();
    }

    @Test
    public void shouldToTrySuccessValue() throws Exception {
        assertThat(TryHelper.toMaybe(Try.success(1)).fold(x -> x, () -> 0)).isEqualTo(1);
    }

    @Test
    public void shouldToTryFailure() throws Exception {
        assertThat(TryHelper.toMaybe(Try.failure(new Exception())).hasSome()).isFalse();
    }

    @Test(expected = IOException.class)
    public void shouldFailureRaiseASuppliedError() throws Throwable {
        Try.failure(new UnknownError()).orElseThrow(() -> new IOException());
    }

    @Test
    public void shouldSuccessNotRaiseASuppliedError() throws Throwable {
        Try.success(1).orElseThrow(() -> new IOException());
        assertThat(true).isTrue();
    }

    @Test(expected = UnknownError.class)
    public void shouldFailureBeRaised() throws Throwable {
        Try.failure(new UnknownError()).orElseThrow();
    }

    @Test
    public void shouldSuccessNotRaiseAnError() throws Throwable {
        Try.success(1).orElseThrow();
        assertThat(true).isTrue();
    }

    @Test(expected = IOException.class)
    public void shouldFailureDoAnExceptionRaise() throws Throwable {
        Try.failure(new UnknownError()).orElseThrow(x -> new IOException());
    }

    @Test
    public void shouldSuccessNotDoAnExceptionRaise() throws Throwable {
        Try.success(1).orElseThrow(x -> new IOException());
        assertThat(true).isTrue();
    }
}
