/*
 * HPAS
 * https://github.com/d-plaindoux/hpas
 *
 * Copyright (c) 2016-2025 Didier Plaindoux
 * Licensed under the LGPL2 license.
 */

package org.smallibs.data;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

public class TryTest {

    @Test
    public void shouldHaveSuccess() {
        assertThat(Try.success(1).isSuccess()).isTrue();
    }

    @Test
    public void shouldHaveSuccessValue() {
        assertThat(Try.success(1).<Integer>fold(x -> x, __ -> 0)).isEqualTo(1);
    }

    @Test
    public void shouldHaveFailure() {
        assertThat(Try.failure(new Exception()).isSuccess()).isFalse();
    }


    @Test
    public void shouldFilterSuccess() {
        assertThat(Try.success(1).filter(i -> i == 1).isSuccess()).isTrue();
    }

    @Test
    public void shouldFilterSuccessRetrieveValue() {
        assertThat(Try.success(1).filter(i -> i == 1).<Integer>fold(x -> x, __ -> 0)).isEqualTo(1);
    }

    @Test
    public void shouldNotFilterSuccess() {
        assertThat(Try.success(1).filter(i -> i != 1).isSuccess()).isFalse();
    }

    @Test
    public void shouldFilterFailure() {
        assertThat(Try.<Integer>failure(new Exception()).filter(i -> i == 1).isSuccess()).isFalse();
    }

    @Test
    public void shouldMapSuccess() {
        assertThat((Try.success(1).map(i -> i + 1)).isSuccess()).isTrue();
    }

    @Test
    public void shouldMapSuccessRetrieveValue() {
        assertThat((Try.success(1).map(i -> i + 1)).<Integer>fold(x -> x, __ -> 0)).isEqualTo(2);
    }

    @Test
    public void shouldMapFailure() {
        assertThat(Try.<Integer>failure(new Exception()).map(i -> i + 1).isSuccess()).isFalse();
    }

    @Test
    public void shouldFlatMapSuccessToFailure() {
        assertThat(Try.success(1).flatmap(i -> Try.failure(new Exception())).isSuccess()).isFalse();
    }

    @Test
    public void shouldFlatMapFailure() {
        assertThat(Try.<Integer>failure(new Exception()).flatmap(Try::success).isSuccess()).isFalse();
    }

    @Test
    public void shouldOnSuccessSuccess() {
        final AtomicInteger integer = new AtomicInteger(0);

        Try.success(1).onSuccess(integer::set);

        assertThat(integer.get()).isEqualTo(1);
    }

    @Test
    public void shouldOnSuccessFailure() {
        final AtomicInteger integer = new AtomicInteger(0);

        Try.<Integer>failure(new Exception()).onSuccess(integer::set);

        assertThat(integer.get()).isEqualTo(0);
    }

    @Test
    public void shouldOnFailureSuccess() {
        final AtomicInteger integer = new AtomicInteger(0);

        Try.success(1).onFailure(throwable -> integer.set(0));

        assertThat(integer.get()).isEqualTo(0);
    }

    @Test
    public void shouldOnFailureFailure() {
        final AtomicInteger integer = new AtomicInteger(0);

        Try.<Integer>failure(new Exception()).onFailure(throwable -> integer.set(1));

        assertThat(integer.get()).isEqualTo(1);
    }

    @Test
    public void shouldRecoverWithSuccess() {
        assertThat(Try.success(1).recoverWith(0)).isEqualTo(1);
    }

    @Test
    public void shouldRecoverWithFailure() {
        assertThat(Try.<Integer>failure(new Exception()).recoverWith(0)).isEqualTo(0);
    }

    @Test
    public void shouldOrLazyElseSuccess() {
        assertThat(Try.success(1).recoverWith(__ -> 0)).isEqualTo(1);
    }

    @Test
    public void shouldOrLazyElseFailure() {
        assertThat(Try.<Integer>failure(new Exception()).recoverWith(__ -> 0)).isEqualTo(0);
    }

    @Test
    public void shouldToTrySuccess() {
        assertThat(TryHelper.toMaybe(Try.success(1)).hasSome()).isTrue();
    }

    @Test
    public void shouldToTrySuccessValue() {
        assertThat(TryHelper.toMaybe(Try.success(1)).fold(x -> x, () -> 0)).isEqualTo(1);
    }

    @Test
    public void shouldToTryFailure() {
        assertThat(TryHelper.toMaybe(Try.failure(new Exception())).hasSome()).isFalse();
    }

    @Test
    public void shouldFailureRaiseASuppliedError() {
        Assertions.assertThatThrownBy(() -> Try.failure(new UnknownError()).orElseThrow(() -> new IOException()))
                .isInstanceOf(IOException.class);
    }

    @Test
    public void shouldSuccessNotRaiseASuppliedError() throws Throwable {
        Try.success(1).orElseThrow(() -> new IOException());
        assertThat(true).isTrue();
    }

    @Test
    public void shouldFailureBeRaised() {
        Assertions.assertThatThrownBy(() -> Try.failure(new UnknownError()).orElseThrow())
                .isInstanceOf(UnknownError.class);
    }

    @Test
    public void shouldSuccessNotRaiseAnError() throws Throwable {
        Try.success(1).orElseThrow();
        assertThat(true).isTrue();
    }

    @Test
    public void shouldFailureDoAnExceptionRaise() throws Throwable {
        Assertions.assertThatThrownBy(() -> Try.failure(new UnknownError()).orElseThrow(x -> new IOException()))
                .isInstanceOf(IOException.class);
    }

    @Test
    public void shouldSuccessNotDoAnExceptionRaise() throws Throwable {
        Try.success(1).orElseThrow(x -> new IOException());
        assertThat(true).isTrue();
    }
}
