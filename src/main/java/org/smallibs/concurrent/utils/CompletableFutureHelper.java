package org.smallibs.concurrent.utils;

import org.smallibs.concurrent.promise.Promise;
import org.smallibs.concurrent.promise.impl.SolvablePromise;
import org.smallibs.data.Try;

import java.util.concurrent.CompletableFuture;

public interface CompletableFutureHelper {

    static  <T> CompletableFuture<T> completableFuture(Promise<T> promise) {
        final CompletableFuture<T> future = new CompletableFuture<>();

        promise.onComplete(tTry ->
                tTry.onSuccess(future::complete).onFailure(future::completeExceptionally)
        );

        return future;
    }

    static  <T> Promise<T> promise(CompletableFuture<T> completableFuture) {
        final SolvablePromise<T> promise = new SolvablePromise<>();

        completableFuture.whenComplete((v, e) -> {
            if (e != null) {
                promise.solve(Try.failure(e));
            } else {
                promise.solve(Try.success(v));
            }
        });

        return promise;
    }

}
