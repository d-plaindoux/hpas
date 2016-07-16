package org.smallibs.concurrent.promise;

import org.junit.Test;
import org.smallibs.concurrent.asynchronous.Executor;
import org.smallibs.concurrent.asynchronous.ExecutorBuilder;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.smallibs.concurrent.promise.Promise.specialize;

public class MappedPromiseTest {

    @Test
    public void shouldApplyOnSuccess() throws Exception {
        final AtomicBoolean aBoolean = new AtomicBoolean(false);
        final Executor executor = givenAnExecutor();

        final Promise<Integer> integerPromise = specialize(executor.async(() -> 1).map(i -> i + 1)).self();

        integerPromise.onSuccess(i -> aBoolean.set(true));
        integerPromise.getFuture().get();

        assertThat(aBoolean.get()).isTrue();
    }

    @Test
    public void shouldApplyOnSuccessAfterOneSecond() throws Exception {
        final AtomicBoolean aBoolean = new AtomicBoolean(false);
        final Executor executor = givenAnExecutor();

        final Promise<Integer> integerPromise = specialize(executor.async(() -> {
            Thread.sleep(1000);
            return 1;
        }).map(i -> i + 1)).self();

        integerPromise.onSuccess(i -> aBoolean.set(true));
        integerPromise.getFuture().get();

        assertThat(aBoolean.get()).isTrue();
    }

    @Test
    public void shouldApplyOnFailure() throws Exception {
        final AtomicBoolean aBoolean = new AtomicBoolean(false);
        final Executor executor = givenAnExecutor();

        final Promise<Integer> integerPromise = specialize(executor.<Integer>async(() -> {
            throw new SecurityException();
        }).map(i -> i + 1)).self();

        integerPromise.onFailure(i -> aBoolean.set(true));

        try {
            integerPromise.getFuture().get();
        } catch (ExecutionException e) {
            // consume
        }

        assertThat(aBoolean.get()).isTrue();
    }

    @Test
    public void shouldApplyOnFailureAfterOneSecond() throws Exception {
        final AtomicBoolean aBoolean = new AtomicBoolean(false);
        final Executor executor = givenAnExecutor();

        final Promise<Integer> integerPromise = specialize(executor.<Integer>async(() -> {
            Thread.sleep(1000);
            throw new SecurityException();
        }).map(i -> i + 1)).self();

        integerPromise.onFailure(i -> aBoolean.set(true));

        try {
            integerPromise.getFuture().get();
        } catch (ExecutionException e) {
            // consume
        }

        assertThat(aBoolean.get()).isTrue();
    }

    @Test
    public void shouldApplyOnComplete() throws Exception {
        final AtomicBoolean aBoolean = new AtomicBoolean(false);
        final Executor executor = givenAnExecutor();

        final Promise<Integer> integerPromise = specialize(executor.<Integer>async(() -> {
            throw new SecurityException();
        }).map(i -> i + 1)).self();
        ;

        integerPromise.onComplete(i -> aBoolean.set(true));

        try {
            integerPromise.getFuture().get();
        } catch (ExecutionException e) {
            // consume
        }

        assertThat(aBoolean.get()).isTrue();
    }

    @Test
    public void shouldApplyOnCompleteAfterOneSecond() throws Exception {
        final AtomicBoolean aBoolean = new AtomicBoolean(false);
        final Executor executor = givenAnExecutor();

        final Promise<Integer> integerPromise = specialize(executor.<Integer>async(() -> {
            Thread.sleep(1000);
            throw new SecurityException();
        }).map(i -> i + 1)).self();

        integerPromise.onComplete(i -> aBoolean.set(true));

        try {
            integerPromise.getFuture().get();
        } catch (ExecutionException e) {
            // consume
        }

        assertThat(aBoolean.get()).isTrue();
    }

    @Test
    public void shouldApplyPromiseMap() throws Exception {
        final Executor executor = givenAnExecutor();

        final Promise<Integer> integerPromise = specialize(specialize(executor.async(() -> 1).
                map(i -> i + 1)).self().
                map(i -> i + 1)).self();

        assertThat(integerPromise.getFuture().get()).isEqualTo(3);
    }

    @Test(expected = ExecutionException.class)
    public void shouldNotApplyPromiseMap() throws Exception {
        final Executor executor = givenAnExecutor();

        final Promise<Integer> integerPromise = specialize(specialize(executor.<Integer>async(() -> {
            throw new SecurityException();
        }).map(i -> i + 1)).self().map(i -> i + 1)).self();

        integerPromise.getFuture().get();
    }

    @Test
    public void shouldApplyPromiseFlatMap() throws Exception {
        final Executor executor = givenAnExecutor();

        final Promise<Integer> integerPromise = specialize(executor.async(() -> 1).
                map(i -> i + 1)).self().
                flatmap(i -> executor.async(() -> i + 1)).self();

        assertThat(integerPromise.getFuture().get()).isEqualTo(3);
    }

    @Test(expected = ExecutionException.class)
    public void shouldNotApplyPromiseFlatMap() throws Exception {
        final Executor executor = givenAnExecutor();

        final Promise<Integer> integerPromise = specialize(specialize(executor.<Integer>async(() -> {
            throw new SecurityException();
        }).map(i -> i + 1)).self().flatmap(i -> executor.async(() -> i + 1))).self();

        integerPromise.getFuture().get();
    }

    private Executor givenAnExecutor() {
        return ExecutorBuilder.create(Executors.newSingleThreadExecutor());
    }

}