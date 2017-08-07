import java.net.URI;
import java.util.concurrent.*;

public class HttpClient {

    public CompletableFuture<HttpResult> sendAsync(int secondsWait) {
        CompletableFuture delayFuture = AsyncDelay.delay(secondsWait, TimeUnit.SECONDS);
        CompletableFuture<HttpResult> future = delayFuture.thenApply(o -> {
            Log.println("Request finished after delay of " + secondsWait + " seconds");
            return new HttpResult(200);
        });
        return future;
    }
}

