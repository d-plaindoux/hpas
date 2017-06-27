package org.smallibs.concurrent.promise;

import org.junit.Test;
import org.smallibs.concurrent.execution.Executor;
import org.smallibs.concurrent.execution.ExecutorHelper;
import org.smallibs.concurrent.promise.impl.PassivePromise;
import org.smallibs.data.Try;
import org.smallibs.exception.FilterException;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.smallibs.concurrent.execution.ExecutorHelper.await;

public class PassivePromiseTest {

    @Test
    public void shouldApplyOnSuccess() throws Exception {
        final AtomicBoolean aBoolean = new AtomicBoolean(false);

        final PassivePromise<Integer> integerPromise = new PassivePromise<>();

        integerPromise.response(Try.success(1));
        integerPromise.onSuccess(i -> aBoolean.set(true));
        integerPromise.getFuture().get();

        assertThat(aBoolean.get()).isTrue();
    }


    @Test
    public void shouldApplyOnSuccessTwice() throws Exception {
        final AtomicInteger anInteger = new AtomicInteger(0);

        final PassivePromise<Integer> integerPromise = new PassivePromise<>();

        integerPromise.response(Try.success(1));
        integerPromise.onSuccess(i -> anInteger.incrementAndGet());
        integerPromise.onSuccess(i -> anInteger.incrementAndGet());
        integerPromise.getFuture().get();

        assertThat(anInteger.get()).isEqualTo(2);
    }

    @Test
    public void shouldApplyOnSuccessAfterOneSecond() throws Exception {
        final AtomicBoolean aBoolean = new AtomicBoolean(false);
        final Executor executor = givenAnExecutor();

        final PassivePromise<Integer> integerPromise = new PassivePromise<>();

        executor.async(() -> {
            Thread.sleep(1000);
            integerPromise.response(Try.success(1));
        });

        integerPromise.onSuccess(i -> aBoolean.set(true));
        integerPromise.getFuture().get();

        assertThat(aBoolean.get()).isTrue();
    }


    @Test
    public void shouldApplyOnSuccessTwiceAfterOneSecond() throws Exception {
        final Executor executor = givenAnExecutor();

        final AtomicInteger anInteger = new AtomicInteger(0);

        final PassivePromise<Integer> integerPromise = new PassivePromise<>();

        integerPromise.onSuccess(i -> anInteger.incrementAndGet());
        integerPromise.onSuccess(i -> anInteger.incrementAndGet());

        executor.async(() -> {
            Thread.sleep(1000);
            integerPromise.response(Try.success(1));
        });

        integerPromise.getFuture().get();

        assertThat(anInteger.get()).isEqualTo(2);
    }

    @Test
    public void shouldApplyOnFailure() throws Exception {
        final AtomicBoolean aBoolean = new AtomicBoolean(false);

        final PassivePromise<Integer> integerPromise = new PassivePromise<>();

        integerPromise.response(Try.failure(new SecurityException()));

        integerPromise.onFailure(i -> aBoolean.set(true));

        try {
            integerPromise.getFuture().get();
            assertThat(true).isFalse();
        } catch (ExecutionException e) {
            // consume
        }

        assertThat(aBoolean.get()).isTrue();
    }

    @Test
    public void shouldApplyOnFailureAfterOneSecond() throws Exception {
        final AtomicBoolean aBoolean = new AtomicBoolean(false);
        final Executor executor = givenAnExecutor();

        final PassivePromise<Integer> integerPromise = new PassivePromise<>();

        executor.async(() -> {
            Thread.sleep(1000);
            integerPromise.response(Try.failure(new SecurityException()));
        });

        integerPromise.onFailure(i -> aBoolean.set(true));

        try {
            integerPromise.getFuture().get();
            assertThat(true).isFalse();
        } catch (ExecutionException e) {
            // consume
        }

        assertThat(aBoolean.get()).isTrue();
    }

    @Test
    public void shouldApplyOnComplete() throws Exception {
        final AtomicBoolean aBoolean = new AtomicBoolean(false);

        final PassivePromise<Integer> integerPromise = new PassivePromise<>();

        integerPromise.response(Try.failure(new SecurityException()));

        integerPromise.onComplete(i -> aBoolean.set(true));

        try {
            integerPromise.getFuture().get();
            assertThat(true).isFalse();
        } catch (ExecutionException e) {
            // consume
        }

        assertThat(aBoolean.get()).isTrue();
    }

    @Test
    public void shouldApplyOnCompleteAfterOneSecond() throws Exception {
        final AtomicBoolean aBoolean = new AtomicBoolean(false);
        final Executor executor = givenAnExecutor();

        final PassivePromise<Integer> integerPromise = new PassivePromise<>();

        executor.async(() -> {
            Thread.sleep(1000);
            integerPromise.response(Try.failure(new SecurityException()));
        });

        integerPromise.onComplete(i -> aBoolean.set(true));

        try {
            integerPromise.getFuture().get();
            assertThat(true).isFalse();
        } catch (ExecutionException e) {
            // consume
        }

        assertThat(aBoolean.get()).isTrue();
    }

    @Test
    public void shouldApplyPromiseMap() throws Exception {
        final PassivePromise<Integer> integerPromise = new PassivePromise<>();

        integerPromise.response(Try.success(1));

        final Promise<Integer> promise = integerPromise.map(i -> i + 1);

        assertThat(promise.getFuture().get()).isEqualTo(2);
    }

    @Test(expected = ExecutionException.class)
    public void shouldNotApplyPromiseMap() throws Exception {
        final Executor executor = givenAnExecutor();

        final PassivePromise<Integer> integerPromise = new PassivePromise<>();

        integerPromise.response(Try.failure(new SecurityException()));

        final Promise<Integer> promise = integerPromise.map(i -> i + 1);

        promise.getFuture().get();
    }

    @Test
    public void shouldApplyPromiseFlatMap() throws Exception {
        final Executor executor = givenAnExecutor();

        final PassivePromise<Integer> integerPromise = new PassivePromise<>();

        integerPromise.response(Try.success(1));

        final Promise<Integer> promise = integerPromise.flatmap(i -> executor.async(() -> i + 1));

        assertThat(promise.getFuture().get()).isEqualTo(2);
    }

    @Test
    public void shouldApplyPromiseFlatMapMap() throws Exception {
        final Executor executor = givenAnExecutor();

        PassivePromise<Integer> integerPromise = new PassivePromise<>();

        integerPromise.response(Try.success(1));

        final Promise<Integer> promise = integerPromise.flatmap(i -> executor.async(() -> i + 1)).map(i -> i + 1);

        assertThat(promise.getFuture().get()).isEqualTo(3);
    }

    @Test(expected = ExecutionException.class)
    public void shouldNotApplyPromiseFlatMap() throws Exception {
        final Executor executor = givenAnExecutor();

        PassivePromise<Integer> integerPromise = new PassivePromise<>();

        integerPromise.response(Try.failure(new SecurityException()));

        final Promise<Integer> promise = integerPromise.flatmap(i -> executor.async(() -> i + 1));

        promise.getFuture().get();
    }

    @Test
    public void shouldFilterPromise() throws Exception {
        final Executor executor = givenAnExecutor();

        final PassivePromise<Integer> integerPromise = new PassivePromise<>();

        integerPromise.response(Try.success(1));

        final Promise<Integer> promise = executor.<Integer>async(() -> 1).filter(i -> i == 1).self();

        assertThat(promise.getFuture().get()).isEqualTo(1);
    }

    @Test(expected = FilterException.class)
    public void shouldNotFilterPromise() throws Throwable {
        final Executor executor = givenAnExecutor();

        final PassivePromise<Integer> integerPromise = new PassivePromise<>();

        integerPromise.response(Try.success(1));

        final Promise<Integer> promise = executor.<Integer>async(() -> 1).filter(i -> i == 2).self();

        await(promise).orElseThrow();
    }

    private Executor givenAnExecutor() {
        return ExecutorHelper.create(Executors.newSingleThreadExecutor());
    }

}