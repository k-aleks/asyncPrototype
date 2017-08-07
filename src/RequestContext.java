import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

class RequestContext {
    private final CompletableFuture<HttpResult> resultFuture;
    private final HttpClient httpClient;
    private final CompletableFuture<HttpResult>[] activeFutures;
    private int currentConcurrencyLevel;
    private AtomicBoolean isRequestCancelled = new AtomicBoolean(false);

    RequestContext(HttpClient httpClient, CompletableFuture<HttpResult>[] activeFutures) {
        this.httpClient = httpClient;
        this.activeFutures = activeFutures;
        this.currentConcurrencyLevel = 0;
        this.resultFuture = new CompletableFuture<HttpResult>();
    }

    boolean IsRequestCancelled() {
        if (isRequestCancelled.get() || resultFuture.isCancelled())
            return true;
        return false;
    }

    int getCurrentConcurrencyLevel() {
        return currentConcurrencyLevel;
    }

    CompletableFuture<HttpResult> getResultFuture() {
        return resultFuture;
    }

    HttpClient getHttpClient() {
        return httpClient;
    }

    void incrementCurrentConcurrencyLevel() {
        currentConcurrencyLevel++;
    }

    CompletableFuture<HttpResult>[] getActiveFutures() {
        return activeFutures;
    }
}

class RequestContextFactory {
    private static CompletableFuture neverEndsFuture = new CompletableFuture();

    static RequestContext create(HttpClient httpClient, int maxConcurrencyLevel) {
        CompletableFuture<HttpResult>[] activeFutures = new CompletableFuture[maxConcurrencyLevel + 1];
        fillNullsWithNeverEndsFutures(activeFutures);
        return new RequestContext(httpClient, activeFutures);
    }

    private static void fillNullsWithNeverEndsFutures(CompletableFuture[] futures) {
        for (int i = 0; i < futures.length; i++) {
            if (futures[i] == null)
                futures[i] = neverEndsFuture;
        }
    }
}
