package mc.sample.tasks.swrve.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.io.IOException;
import java.net.Socket;

public class NetworkHelper {
    private static final String LOGTAG = NetworkHelper.class.getSimpleName();

    private static final String TEST_SERVER1 = "google.com";
    private static final String TEST_SERVER2 = "baidu.com";
    private static final String TEST_SERVER3 = "bing.com";

    public static boolean hasConnection(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni != null && ni.isAvailable() && ni.isConnected()) {
            if (hasConnection(TEST_SERVER1) || hasConnection(TEST_SERVER2) || hasConnection(TEST_SERVER3)) {
                Log.i(LOGTAG, "Internet connection detected");
                return true;
            }
        }

        Log.i(LOGTAG, "No internet connection detected");
        return false;
    }

    private static boolean hasConnection(String url) {
        Socket s = null;
        boolean res = false;
        try {
            s = new Socket(url, 80);
            if (s.isBound() && s.isConnected()) {
                res = true;
            }
        } catch (IOException e) {
            Log.e(LOGTAG, "Error on connection to " + url, e);
        } finally {
            if (s != null)
                Util.close(s);
        }
        return res;
    }
}