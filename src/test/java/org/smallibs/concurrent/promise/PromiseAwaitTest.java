package org.smallibs.concurrent.promise;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.smallibs.concurrent.execution.Executor;
import org.smallibs.concurrent.execution.ExecutorHelper;

import java.util.concurrent.Executors;

public class PromiseAwaitTest {

    @Test
    public void shouldAwaitForPromiseResponse() throws Throwable {
        // Given
        var executor = ExecutorHelper.create(Executors.newVirtualThreadPerTaskExecutor());

        var aLongAddition = executor.async(() -> {
            var firstInteger = integer(executor, 5000);
            Thread.sleep(3000);
            var secondInteger = integer(executor, 7000);

            return firstInteger.await() + secondInteger.await();
        }).await();

        Assertions.assertThat(aLongAddition).isEqualTo(12_000);
    }

    private static Promise<Integer> integer(Executor executor, int value) {
        return executor.async(() -> {
            Thread.sleep(value);
            return value;
        });
    }
}
