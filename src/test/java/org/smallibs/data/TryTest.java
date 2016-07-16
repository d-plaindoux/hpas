package org.smallibs.data;

import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.smallibs.data.Try.specialize;

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
        assertThat(specialize(Try.success(1).map(i -> i + 1)).self().isSuccess()).isTrue();
    }

    @Test
    public void shouldMapSuccessRetrieveValue() throws Exception {
        assertThat(specialize(Try.success(1).map(i -> i + 1)).self().success()).isEqualTo(2);
    }

    @Test
    public void shouldMapFailure() throws Exception {
        assertThat(specialize(Try.<Integer>failure(new Exception()).map(i -> i + 1)).self().isSuccess()).isFalse();
    }

    @Test
    public void shouldFlatMapSuccessToSuccess() throws Exception {
        assertThat(specialize(Try.success(1).flatmap(Try::success)).self().isSuccess()).isTrue();
    }

    @Test
    public void shouldFlatMapSuccessToFailure() throws Exception {
        assertThat(specialize(Try.success(1).flatmap(i -> Try.failure(new Exception()))).self().isSuccess()).isFalse();
    }

    @Test
    public void shouldFlatMapFailure() throws Exception {
        assertThat(specialize(Try.<Integer>failure(new Exception()).flatmap(Try::success)).self().isSuccess()).isFalse();
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
        Try.failure(new UnknownError()).orElseRetrieveAndThrow();
    }

    @Test
    public void shouldSuccessNotRaiseAnError() throws Throwable {
        Try.success(1).orElseRetrieveAndThrow();
        assertThat(true).isTrue();
    }

    @Test(expected = IOException.class)
    public void shouldFailureDoAnExceptionRaise() throws Throwable {
        Try.failure(new UnknownError()).orElseRetrieveAndThrow(x -> new IOException());
    }

    @Test
    public void shouldSuccessNotDoAnExceptionRaise() throws Throwable {
        Try.success(1).orElseRetrieveAndThrow(x -> new IOException());
        assertThat(true).isTrue();
    }
}
