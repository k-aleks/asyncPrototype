import java.net.URI;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;

public class Main {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        test1();
        test2();
        System.exit(0);
    }

    private static void test1() throws InterruptedException, ExecutionException {
        Log.println("Begin test 1");
        CompletableFuture<HttpResult> future = new ForkingStrategy().sendAsync(new HttpClient());
        HttpResult httpResult = future.get();
        Log.println("Final result: " + httpResult.getResultCode());
        Thread.sleep(5000);
//        ForkJoinPool.commonPool().shutdownNow(); //todo: how to wait correctly?
    }

    private static void test2() throws InterruptedException, ExecutionException {
        Log.println("Begin test 2");
        CompletableFuture<HttpResult> future = new ForkingStrategy().sendAsync(new HttpClient());
        future.cancel(false);
        Log.println("The future is canceled");
        Thread.sleep(5000);
//        ForkJoinPool.commonPool().shutdownNow(); //todo: how to wait correctly?
    }

}

