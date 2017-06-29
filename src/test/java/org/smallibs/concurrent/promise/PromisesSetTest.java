package org.smallibs.concurrent.promise;

import com.jayway.awaitility.Duration;
import org.junit.Test;
import org.smallibs.concurrent.execution.Executor;
import org.smallibs.concurrent.execution.ExecutorHelper;
import org.smallibs.concurrent.promise.impl.PassivePromise;

import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.jayway.awaitility.Awaitility.await;
import static org.assertj.core.api.Assertions.assertThat;
import static org.smallibs.concurrent.promise.PromiseHelper.failure;
import static org.smallibs.concurrent.promise.PromiseHelper.success;

public class PromisesSetTest {

    @Test
    public void shouldJoinWhenEmpty() throws Exception {
        final AtomicBoolean aBoolean = new AtomicBoolean(false);

        PromiseHelper.join().onSuccess(unit -> aBoolean.set(true));

        assertThat(aBoolean.get()).isTrue();
    }

    @Test
    public void shouldJoinWhenSuccessPromise() throws Exception {
        final AtomicBoolean aBoolean = new AtomicBoolean(false);

        PromiseHelper.join(success(1)).onSuccess(unit -> aBoolean.set(true));

        assertThat(aBoolean.get()).isTrue();
    }

    @Test
    public void shouldJoinWhenFailurePromise() throws Exception {
        final AtomicBoolean aBoolean = new AtomicBoolean(false);

        PromiseHelper.join(failure(new SecurityException())).onSuccess(unit -> aBoolean.set(true));

        assertThat(aBoolean.get()).isTrue();
    }

    @Test
    public void shouldNotJoinWhenUnsolvedPromise() throws Exception {
        final AtomicBoolean aBoolean = new AtomicBoolean(false);
        final PassivePromise<Object> passivePromise = new PassivePromise<>();

        PromiseHelper.join(passivePromise).onSuccess(unit -> aBoolean.set(true));

        assertThat(aBoolean.get()).isFalse();
    }

    @Test
    public void shouldJoinWhenSolvedPromiseAfter() throws Exception {
        final AtomicBoolean aBoolean = new AtomicBoolean(false);
        final Executor executor = givenAnExecutor();

        final Promise<Integer> promise = executor.async(() -> {
            Thread.sleep(1000);
            return 1;
        });

        PromiseHelper.join(promise).onSuccess(unit -> aBoolean.set(true));

        await().atMost(Duration.FIVE_SECONDS).until(aBoolean::get);

        assertThat(aBoolean.get()).isTrue();
    }

    @Test
    public void shouldExistsWhenEmpty() throws Exception {
        final AtomicBoolean aBoolean = new AtomicBoolean(false);

        PromiseHelper.exists().onSuccess(unit -> aBoolean.set(true));

        assertThat(aBoolean.get()).isTrue();
    }

    @Test
    public void shouldExistsWhenSuccessPromise() throws Exception {
        final AtomicBoolean aBoolean = new AtomicBoolean(false);

        PromiseHelper.exists(success(1)).onSuccess(unit -> aBoolean.set(true));

        assertThat(aBoolean.get()).isTrue();
    }

    @Test
    public void shouldExistsWhenFailurePromise() throws Exception {
        final AtomicBoolean aBoolean = new AtomicBoolean(false);

        PromiseHelper.exists(failure(new SecurityException())).onFailure(unit -> aBoolean.set(true));

        assertThat(aBoolean.get()).isTrue();
    }

    @Test
    public void shouldNotExistsWhenUnsolvedPromise() throws Exception {
        final AtomicBoolean aBoolean = new AtomicBoolean(false);
        final PassivePromise<Object> passivePromise = new PassivePromise<>();

        PromiseHelper.exists(passivePromise).onComplete(unit -> aBoolean.set(true));

        assertThat(aBoolean.get()).isFalse();
    }

    @Test
    public void shouldExistsWheOneSolvedPromise() throws Exception {
        final AtomicBoolean aBoolean = new AtomicBoolean(false);

        PromiseHelper.exists(success(1), failure(new SecurityException())).onSuccess(unit -> aBoolean.set(true));

        assertThat(aBoolean.get()).isTrue();
    }

    @Test
    public void shouldExistsWhenSolvedPromiseAfter() throws Exception {
        final AtomicBoolean aBoolean = new AtomicBoolean(false);
        final Executor executor = givenAnExecutor();

        final Promise<Integer> promise = executor.async(() -> {
            Thread.sleep(1000);
            return 1;
        });

        PromiseHelper.exists(promise).onSuccess(unit -> aBoolean.set(true));

        await().atMost(Duration.FIVE_SECONDS).until(aBoolean::get);

        assertThat(aBoolean.get()).isTrue();
    }


    @Test
    public void shouldForallWhenEmpty() throws Exception {
        final AtomicBoolean aBoolean = new AtomicBoolean(false);

        PromiseHelper.forall().onSuccess(unit -> aBoolean.set(true));

        assertThat(aBoolean.get()).isTrue();
    }

    @Test
    public void shouldForallWhenSuccessPromise() throws Exception {
        final AtomicBoolean aBoolean = new AtomicBoolean(false);

        PromiseHelper.forall(success(1)).onSuccess(unit -> aBoolean.set(true));

        assertThat(aBoolean.get()).isTrue();
    }

    @Test
    public void shouldForallWhenFailurePromise() throws Exception {
        final AtomicBoolean aBoolean = new AtomicBoolean(false);

        PromiseHelper.forall(failure(new SecurityException())).onFailure(unit -> aBoolean.set(true));

        assertThat(aBoolean.get()).isTrue();
    }

    @Test
    public void shouldNotForallWhenUnsolvedPromise() throws Exception {
        final AtomicBoolean aBoolean = new AtomicBoolean(false);
        final PassivePromise<Object> passivePromise = new PassivePromise<>();

        PromiseHelper.forall(passivePromise).onComplete(unit -> aBoolean.set(true));

        assertThat(aBoolean.get()).isFalse();
    }

    @Test
    public void shouldForallWheOneSolvedPromise() throws Exception {
        final AtomicBoolean aBoolean = new AtomicBoolean(false);

        PromiseHelper.forall(success(1), failure(new SecurityException())).onFailure(unit -> aBoolean.set(true));

        assertThat(aBoolean.get()).isTrue();
    }

    @Test
    public void shouldForallWhenSolvedPromiseAfter() throws Exception {
        final AtomicBoolean aBoolean = new AtomicBoolean(false);
        final Executor executor = givenAnExecutor();

        final Promise<Integer> promise = executor.async(() -> {
            Thread.sleep(1000);
            return 1;
        });

        PromiseHelper.forall(promise).onSuccess(unit -> aBoolean.set(true));

        await().atMost(Duration.FIVE_SECONDS).until(aBoolean::get);

        assertThat(aBoolean.get()).isTrue();
    }

    private Executor givenAnExecutor() {
        return ExecutorHelper.create(Executors.newSingleThreadExecutor());
    }
}