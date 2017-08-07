import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class ForkingStrategy {
    private static CompletableFuture neverEndsFuture = new CompletableFuture();

    //в рельной жизни это параметры стратегии
    private int maxConcurrencyLevel = 3;
    private int[] delays = new int[] {1, 1, 2};

    //в реальной жизни это как-будто бы случайные значение:
    private int[] latencies = new int[] {4,3,1};

    public CompletableFuture<HttpResult> sendAsync(HttpClient httpClient) {
        CompletableFuture<HttpResult>[] activeFutures = new CompletableFuture[maxConcurrencyLevel + 1];
        RequestContext context = new RequestContext(httpClient, activeFutures);

        fillNullsWithNeverEndsFutures(context.getActiveFutures());

        sendNextAsyncRequest(context);

        return context.getResultFuture();
    }

    private void onResponseOrTimeout(Object futureResult, RequestContext context) {
        if (futureResult instanceof HttpResult) {
            context.getActiveFutures()[context.getCurrentConcurrencyLevel() - 1].cancel(false); //cancel delay
            Log.println("Completing future");
            context.getResultFuture().complete((HttpResult) futureResult);
        }
        //за отведенное время не дождали/**/сь ответа
        if (context.getCurrentConcurrencyLevel() < maxConcurrencyLevel) {
            sendNextAsyncRequest(context);
        }
        else {
            //Больше нет реплик, пора вернуть timeout
            context.getResultFuture().complete(new HttpResult(408));
        }
    }

    private void sendNextAsyncRequest(RequestContext context) {
        Log.println("Sending the request #" + context.getCurrentConcurrencyLevel());
        CompletableFuture<HttpResult> resultFuture = context.getHttpClient().sendAsync(latencies[context.getCurrentConcurrencyLevel()]);
        context.getActiveFutures()[context.getCurrentConcurrencyLevel()] = resultFuture;

        CompletableFuture delayFuture = AsyncDelay.delay(delays[context.getCurrentConcurrencyLevel()], TimeUnit.SECONDS);
        context.getActiveFutures()[context.getCurrentConcurrencyLevel()+1] = delayFuture;

        context.incrementCurrentConcurencyLevel();
        CompletableFuture.anyOf(context.getActiveFutures()).thenAccept(o -> onResponseOrTimeout(o, context));
    }

    private void fillNullsWithNeverEndsFutures(CompletableFuture[] futures) {
        for (int i = 0; i < futures.length; i++) {
           if (futures[i] == null)
               futures[i] = neverEndsFuture;
        }
    }
}
