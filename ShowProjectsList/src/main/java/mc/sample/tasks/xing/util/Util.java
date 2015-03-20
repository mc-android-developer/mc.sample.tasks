package mc.sample.tasks.xing.util;

import android.util.Log;

import java.io.Closeable;
import java.io.IOException;
import java.net.HttpURLConnection;

public class Util {
    private static final String LOGTAG = Util.class.getSimpleName();

    public static void close(Closeable c) {
        if (c == null) {
            return;
        }
        try {
            c.close();
        } catch (IOException e) {
            Log.e(LOGTAG, "Unable to close", e);
        }
    }

    public static void close(HttpURLConnection con) {
        if (con != null) {
            con.disconnect();
        }
    }
}
