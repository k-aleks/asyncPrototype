import java.net.URI;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;

public class Main {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        Log.println("Begin");

        CompletableFuture<HttpResult> future = new ForkingStrategy().sendAsync(new HttpClient());

//        Thread.sleep(2000);
        HttpResult httpResult = future.get();

        Log.println("Final result: " + httpResult.getResultCode());

//        ForkJoinPool.commonPool().shutdownNow();
    }

}

