import java.util.concurrent.*;

public class AsyncDelay {
    private static ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(8);

    static <T> CompletableFuture<T> timeoutAfter(long timeout, TimeUnit unit) {
        CompletableFuture<T> future = new CompletableFuture<T>();
        scheduledExecutorService.schedule(() -> future.completeExceptionally(new TimeoutException()), timeout, unit);
        return future;
    }

    static <T> CompletableFuture<T> delay(long timeout, TimeUnit unit, T futureResult) {
        CompletableFuture<T> future = new CompletableFuture<T>();
        scheduledExecutorService.schedule(() -> future.complete(futureResult), timeout, unit);
        return future;
    }

    static CompletableFuture delay(long timeout, TimeUnit unit) {
        CompletableFuture future = new CompletableFuture();
        scheduledExecutorService.schedule(() -> future.complete(null), timeout, unit);
        return future;
    }
}
