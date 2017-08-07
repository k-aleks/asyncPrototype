import java.util.concurrent.CompletableFuture;

public class RequestContext {
    private int currentConcurrencyLevel;
    private CompletableFuture<HttpResult> resultFuture;
    private HttpClient httpClient;
    private CompletableFuture<HttpResult>[] activeFutures;

    public RequestContext(HttpClient httpClient, CompletableFuture<HttpResult>[] activeFutures) {
        this.httpClient = httpClient;
        this.activeFutures = activeFutures;
        this.currentConcurrencyLevel = 0;
        this.resultFuture = new CompletableFuture<HttpResult>();
    }

    public int getCurrentConcurrencyLevel() {
        return currentConcurrencyLevel;
    }

    public CompletableFuture<HttpResult> getResultFuture() {
        return resultFuture;
    }

    public HttpClient getHttpClient() {
        return httpClient;
    }

    public void incrementCurrentConcurrencyLevel() {
        currentConcurrencyLevel++;
    }

    public CompletableFuture<HttpResult>[] getActiveFutures() {
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
