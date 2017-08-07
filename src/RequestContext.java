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

    public void incrementCurrentConcurencyLevel() {
        currentConcurrencyLevel++;
    }

    public CompletableFuture<HttpResult>[] getActiveFutures() {
        return activeFutures;
    }
}
