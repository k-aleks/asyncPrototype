import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class ForkingStrategy {
    private static CompletableFuture neverEndsFuture = new CompletableFuture();

    //в рельной жизни это параметры стратегии
    private int maxConcurrencyLevel = 3;
    private int[] delays = new int[] {1, 1, 1};

    //в реальной жизни это как-будто бы случайные значение:
    private int[] latencies = new int[] {4,3,1};

    public CompletableFuture<HttpResult> sendAsync(HttpClient httpClient) {
        int currentConcurrencyLevel = 0;
        CompletableFuture<HttpResult>[] activeFutures = new CompletableFuture[maxConcurrencyLevel + 1];
        fillNullsWithNeverEndsFutures(activeFutures);

        Log.println("Sending the request #" + currentConcurrencyLevel);
        CompletableFuture<HttpResult> resultFuture = httpClient.sendAsync(latencies[currentConcurrencyLevel]);
        activeFutures[currentConcurrencyLevel] = resultFuture;

        CompletableFuture delayFuture = AsyncDelay.delay(delays[currentConcurrencyLevel], TimeUnit.SECONDS);
        activeFutures[currentConcurrencyLevel+1] = delayFuture;

        int finalCurrentConcurrencyLevel = currentConcurrencyLevel + 1;
        CompletableFuture<HttpResult> future = CompletableFuture.anyOf(activeFutures)
                                .thenApply(o -> onResponseOrTimeout(o, finalCurrentConcurrencyLevel, httpClient, activeFutures))
                                .thenApply(o -> unwrapResult(o));
        return future;
    }

    public HttpResult getHttpResult(CompletableFuture<HttpResult> cf) {
        try {
            return cf.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return new HttpResult(500);
    }

    private HttpResult unwrapResult(Object o) {
        if (o instanceof HttpResult)
            return (HttpResult) o;
        return new HttpResult(408);
    }

    private CompletableFuture<HttpResult> onResponseOrTimeout(Object futureResult, int currentConcurrencyLevel, HttpClient httpClient, CompletableFuture<HttpResult>[] activeFutures) {
        if (futureResult instanceof HttpResult) {
            activeFutures[currentConcurrencyLevel - 1].cancel(false); //cancel delay
            return CompletableFuture.completedFuture((HttpResult) futureResult);
        }
        //за отведенное время не дождались ответа
        if (currentConcurrencyLevel < maxConcurrencyLevel) {
            Log.println("Sending the request #" + currentConcurrencyLevel);

            CompletableFuture<HttpResult> resultFuture = httpClient.sendAsync(latencies[currentConcurrencyLevel]);
            activeFutures[currentConcurrencyLevel] = resultFuture;

            CompletableFuture delayFuture = AsyncDelay.delay(delays[currentConcurrencyLevel], TimeUnit.SECONDS);
            activeFutures[currentConcurrencyLevel+1] = delayFuture;

            int finalCurrentConcurrencyLevel = currentConcurrencyLevel + 1;
            CompletableFuture<HttpResult> future = CompletableFuture.anyOf(activeFutures)
                    .thenApply(o -> onResponseOrTimeout(o, finalCurrentConcurrencyLevel, httpClient, activeFutures));

            return future;
        }

        //Больше нет реплик, пора вернуть timeout
        return CompletableFuture.completedFuture(new HttpResult(408));
    }

    private void fillNullsWithNeverEndsFutures(CompletableFuture[] futures) {
        for (int i = 0; i < futures.length; i++) {
           if (futures[i] == null)
               futures[i] = neverEndsFuture;
        }
    }
}
