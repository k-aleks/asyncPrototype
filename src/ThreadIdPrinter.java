public class ThreadIdPrinter {
    public static void print() {
        System.out.format("Thread Id: %s\r\n", Thread.currentThread().getId());
    }
}
