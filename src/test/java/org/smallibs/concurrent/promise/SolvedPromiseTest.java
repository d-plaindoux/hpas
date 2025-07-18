/*
 * HPAS
 * https://github.com/d-plaindoux/hpas
 *
 * Copyright (c) 2016-2025 Didier Plaindoux
 * Licensed under the LGPL2 license.
 */

package org.smallibs.concurrent.promise;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.smallibs.exception.FilterException;
import org.smallibs.type.HK;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.assertj.core.api.Assertions.assertThat;

public class SolvedPromiseTest {

    @Test
    public void shouldApplyOnSuccess() throws Exception {
        final AtomicBoolean aBoolean = new AtomicBoolean(false);

        final Promise<Integer> integerPromise = PromiseHelper.success(1);
        integerPromise.onSuccess(i -> aBoolean.set(true));
        integerPromise.getFuture().get(5, TimeUnit.SECONDS);

        assertThat(aBoolean.get()).isTrue();
    }

    @Test
    public void shouldApplyOnSuccessWhenSettingNull() throws Exception {
        final AtomicBoolean aBoolean = new AtomicBoolean(false);

        final Promise<Integer> integerPromise = PromiseHelper.success(null);
        integerPromise.onSuccess(i -> aBoolean.set(true));
        integerPromise.getFuture().get(5, TimeUnit.SECONDS);

        assertThat(aBoolean.get()).isTrue();
    }

    @Test
    public void shouldApplyAndAcceptOnSuccess() throws Exception {
        final AtomicBoolean aBoolean = new AtomicBoolean(false);

        final Promise<Integer> solvedPromise = PromiseHelper.success(1);
        final Promise<Integer> integerPromise = solvedPromise.apply(HK::self);

        integerPromise.onSuccess(i -> aBoolean.set(true));
        integerPromise.getFuture().get(5, TimeUnit.SECONDS);

        assertThat(aBoolean.get()).isTrue();
    }


    @Test
    public void shouldApplyOnFailure() throws Exception {
        final AtomicBoolean aBoolean = new AtomicBoolean(false);

        final Promise<Integer> integerPromise = PromiseHelper.failure(new SecurityException());

        integerPromise.onFailure(i -> aBoolean.set(true));

        try {
            integerPromise.getFuture().get(5, TimeUnit.SECONDS);
        } catch (ExecutionException e) {
            // consume
        }

        assertThat(aBoolean.get()).isTrue();
    }

    @Test
    public void shouldApplyOnComplete() throws Exception {
        final AtomicBoolean aBoolean = new AtomicBoolean(false);

        final Promise<Integer> integerPromise = PromiseHelper.failure(new SecurityException());

        integerPromise.onComplete(i -> aBoolean.set(true));

        try {
            integerPromise.getFuture().get(5, TimeUnit.SECONDS);
            assertThat(true).isFalse();
        } catch (ExecutionException e) {
            // consume
        }

        assertThat(aBoolean.get()).isTrue();
    }

    @Test
    public void shouldApplyPromiseMap() throws Exception {
        final Promise<Integer> integerPromise = PromiseHelper.success(1).and(i -> i + 1);

        assertThat(integerPromise.getFuture().get(5, TimeUnit.SECONDS)).isEqualTo(2);
    }

    @Test
    public void shouldNotApplyPromiseMap() {
        final Promise<Integer> integerPromise = PromiseHelper.<Integer>failure(new SecurityException()).
                and(i -> i + 1);

        Assertions.assertThatThrownBy(() -> integerPromise.getFuture().get(5, TimeUnit.SECONDS))
                .cause()
                .isInstanceOf(SecurityException.class);
    }

    @Test
    public void shouldApplyPromiseFlatMap() throws Exception {
        final Promise<Integer> integerPromise = PromiseHelper.success(1).
                then(i -> PromiseHelper.success(i + 1));

        assertThat(integerPromise.getFuture().get(5, TimeUnit.SECONDS)).isEqualTo(2);
    }

    @Test
    public void shouldApplyPromiseFlatMapMap() throws Exception {
        final Promise<Integer> integerPromise = PromiseHelper.success(1).
                then(i -> PromiseHelper.success(i + 1)).
                and(i -> i + 1);

        assertThat(integerPromise.getFuture().get(5, TimeUnit.SECONDS)).isEqualTo(3);
    }

    @Test
    public void shouldNotApplyPromiseFlatMap() {

        final Promise<Integer> integerPromise = PromiseHelper.<Integer>failure(new SecurityException()).
                then(i -> PromiseHelper.success(i + 1));

        Assertions.assertThatThrownBy(() -> integerPromise.getFuture().get(5, TimeUnit.SECONDS))
                .cause()
                .isInstanceOf(SecurityException.class);
    }


    @Test
    public void shouldFilterPromise() throws Exception {
        final Promise<Integer> integerPromise = PromiseHelper.success(1).filter(i -> i == 1).self();

        assertThat(integerPromise.getFuture().get(5, TimeUnit.SECONDS)).isEqualTo(1);
    }

    @Test
    public void shouldNotFilterPromise() {
        final Promise<Integer> integerPromise = PromiseHelper.success(1).filter(i -> i == 2).self();

        Assertions.assertThatThrownBy(() -> integerPromise.getFuture().get(5, TimeUnit.SECONDS))
                .cause()
                .isInstanceOf(FilterException.class);
    }

}