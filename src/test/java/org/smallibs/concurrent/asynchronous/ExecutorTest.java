package org.smallibs.concurrent.asynchronous;

import org.junit.Test;
import org.smallibs.concurrent.promise.Promise;

import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

public class ExecutorTest {

    @Test
    public void shouldRetrieveAnIntegerValue() throws Exception {
        final Executor executor = givenAnAsynchronous();
        final Promise<Integer> integerPromise = executor.async(() -> 1);

        assertThat(executor.await(integerPromise)).isEqualTo(1);
    }

    //
    // Private behaviors
    //

    private Executor givenAnAsynchronous() {
        return Executor.create(Executors.newSingleThreadScheduledExecutor());
    }

}