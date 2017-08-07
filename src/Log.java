import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Log {
    private static final DateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss.S");

    public static void println(String message) {
        System.out.format("%s [%s] %s\r\n", sdf.format(new Date()), Thread.currentThread().getId(), message);
    }

    public static void println(int resultCode) {
        println(Integer.toString(resultCode));
    }
}
