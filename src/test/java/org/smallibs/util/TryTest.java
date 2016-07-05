package org.smallibs.util;

import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

public class TryTest {

    @Test
    public void shouldHaveSuccess() throws Exception {
        assertThat(Try.success(1).isSuccess()).isTrue();
    }

    @Test(expected = IllegalAccessError.class)
    public void shouldHaveSuccessNotFailure() throws Exception {
        Try.success(1).failure();
    }

    @Test
    public void shouldHaveSuccessValue() throws Exception {
        assertThat(Try.success(1).success()).isEqualTo(1);
    }

    @Test
    public void shouldHaveFailure() throws Exception {
        assertThat(Try.failure(new Exception()).isSuccess()).isFalse();
    }

    @Test(expected = IllegalAccessError.class)
    public void shouldHaveFailureNotSuccess() throws Exception {
        Try.failure(new Exception()).success();
    }


    @Test
    public void shouldFilterSuccess() throws Exception {
        assertThat(Try.success(1).filter(i -> i == 1).isSuccess()).isTrue();
    }

    @Test
    public void shouldFilterSuccessRetrieveValue() throws Exception {
        assertThat(Try.success(1).filter(i -> i == 1).success()).isEqualTo(1);
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
        assertThat(Try.success(1).map(i -> i + 1).isSuccess()).isTrue();
    }

    @Test
    public void shouldMapSuccessRetrieveValue() throws Exception {
        assertThat(Try.success(1).map(i -> i + 1).success()).isEqualTo(2);
    }

    @Test
    public void shouldMapFailure() throws Exception {
        assertThat(Try.<Integer>failure(new Exception()).map(i -> i + 1).isSuccess()).isFalse();
    }

    @Test
    public void shouldFlatMapSuccessToSuccess() throws Exception {
        assertThat(Try.success(1).flatmap(Try::success).isSuccess()).isTrue();
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
        assertThat(Try.success(1).toMaybe().hasSome()).isTrue();
    }

    @Test
    public void shouldToTrySuccessValue() throws Exception {
        assertThat(Try.success(1).toMaybe().get()).isEqualTo(1);
    }

    @Test
    public void shouldToTryFailure() throws Exception {
        assertThat(Try.failure(new Exception()).toMaybe().hasSome()).isFalse();
    }

    @Test(expected = IllegalAccessError.class)
    public void shouldToTryFailureValue() throws Throwable {
        Try.failure(new Exception()).toMaybe().get();
    }
}
