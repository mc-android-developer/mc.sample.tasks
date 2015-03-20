package mc.sample.tasks.fortegroup.utils;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;

public class NetworkHelper {
    private static final String LOGTAG = NetworkHelper.class.getSimpleName();
    private static final int BUFFER_SIZE = 8192;

    private static final String TEST_SERVER1 = "google.com";
    private static final String TEST_SERVER2 = "baidu.com";
    private static final String TEST_SERVER3 = "bing.com";
    private static final int CONNECT_TIMEOUT = 5000;

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

    public static String getAsText(String url, String encoding) {
        InputStream is = null;
        try {
            URLConnection c = new URL(url).openConnection();
            c.setConnectTimeout(CONNECT_TIMEOUT);
            c.connect();
            is = c.getInputStream();
            if (c.getContentEncoding() != null) {
                encoding = c.getContentEncoding();
            }
            return readStream(is, encoding);
        } catch (MalformedURLException e) {
            Log.e(LOGTAG, "Wrong url format", e);
        } catch (Exception e) {
            Log.e(LOGTAG, "Cannot get text", e);
        } finally {
            Util.close(is);
        }
        return null;
    }

    public static String readStream(InputStream src, String encoding) {
        InputStreamReader isr = null;
        try {
            StringBuilder res = new StringBuilder();
            isr = new InputStreamReader(src, encoding);
            char[] buff = new char[BUFFER_SIZE / (Character.SIZE / Byte.SIZE)];
            int size = isr.read(buff);
            while (size > 0) {
                res.append(buff, 0, size);
                size = isr.read(buff);
            }
            return res.toString();
        } catch (IOException e) {
            Log.e(LOGTAG, "Exception on stream reading", e);
        } finally {
            Util.close(isr);
        }
        return null;
    }
}
