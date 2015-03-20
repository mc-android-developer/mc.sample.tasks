package mc.sample.tasks.treto.network;

import android.util.Log;

import java.io.IOException;
import java.net.Socket;

import mc.sample.tasks.treto.utils.Util;

public class NetworkHelper {
    private static final String LOGTAG = NetworkHelper.class.getSimpleName();

    private static final String TEST_SERVER1 = "google.com";
    private static final String TEST_SERVER2 = "baidu.com";
    private static final String TEST_SERVER3 = "bing.com";

    public static boolean hasConnection() {
        if (testConnection(TEST_SERVER1) || testConnection(TEST_SERVER2) || testConnection(TEST_SERVER3)) {
            Log.i(LOGTAG, "Internet connection detected");
            return true;
        }
        Log.i(LOGTAG, "No internet connection detected");
        return false;
    }

    private static boolean testConnection(String url) {
        Socket s = null;
        boolean res = false;
        try {
            s = new Socket(url, 80);
            if (s.isBound() && s.isConnected()) {
                res = true;
            }
        } catch (IOException e) {
            Log.e(LOGTAG, "Error on testConnection to " + url, e);
        } finally {
            Util.close(s);
        }
        return res;
    }
}
