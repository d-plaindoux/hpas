package org.smallibs.concurrent.promise;

import com.jayway.awaitility.Awaitility;
import com.jayway.awaitility.Duration;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.smallibs.concurrent.execution.Executor;
import org.smallibs.concurrent.execution.ExecutorHelper;
import org.smallibs.data.Try;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import static java.time.Duration.ofSeconds;

public class PromiseAwaitTest {

    @Test
    public void shouldPerform_1_000_000_Tasks() {
        // Given
        var executor = ExecutorHelper.create(Executors.newVirtualThreadPerTaskExecutor());
        var running_tasks = new AtomicInteger(1_000_000);

        // When
        for (int i = 0; i < 1_000_000; i++) {
            executor.async(() -> {
                Thread.sleep(1_000);
                running_tasks.decrementAndGet();
            });
        }

        // Then
        Awaitility.await()
                .atMost(new Duration(30, TimeUnit.SECONDS))
                .until(() -> running_tasks.get() == 0);
    }

    @Test
    public void shouldAwaitFor_1_000_000_Tasks() throws Exception {
        // Given
        var executor = ExecutorHelper.create(Executors.newVirtualThreadPerTaskExecutor());
        var running_tasks = new AtomicInteger(1_000_000);

        // When
        var promises = IntStream.range(0, 1_000_000)
                .mapToObj(__ ->
                        executor.async(() -> {
                            Thread.sleep(1_000);
                            running_tasks.decrementAndGet();
                        })
                ).toArray(Promise[]::new);

        PromiseHelper.join(promises).await(ofSeconds(30));

        // Then
        Assertions.assertThat(running_tasks.get()).isEqualTo(0);
    }

    @Test
    public void shouldAwaitForPromiseResponse() {
        // Given
        var executor = ExecutorHelper.create(Executors.newVirtualThreadPerTaskExecutor());

        //When
        var aLongAddition = Try.handle(() -> {
            var firstInteger = integer(executor, 1_000);

            Thread.sleep(1_000);

            var secondInteger = integer(executor, 3_000);

            return firstInteger.await() + secondInteger.await();
        });

        // Then
        Assertions.assertThat(aLongAddition).isEqualTo(Try.success(4_000));
    }

    private static Promise<Integer> integer(Executor executor, int value) {
        return executor.async(() -> {
            Thread.sleep(value);
            return value;
        });
    }
}
