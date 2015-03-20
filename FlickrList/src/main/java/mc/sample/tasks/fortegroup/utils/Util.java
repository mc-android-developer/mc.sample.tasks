package mc.sample.tasks.fortegroup.utils;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import org.apache.http.HttpEntity;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.net.Socket;

public class Util {
    private static final String LOGTAG = Util.class.getSimpleName();

    public static File getCacheDir(Context context) {
        File cacheDir = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            cacheDir = context.getExternalCacheDir();
        }
        if (cacheDir == null || !cacheDir.canWrite()) {
            cacheDir = context.getCacheDir();
        }
        return cacheDir;
    }

    public static String urlToFileName(String url) {
        if (TextUtils.isEmpty(url)) {
            return null;
        }
        Uri uri = Uri.parse(url);
        String lastSegment = uri.getLastPathSegment();
        int index = lastSegment.lastIndexOf('.');
        String extension = "";
        if (index > -1 && lastSegment.length() - index < 5) {
            extension = lastSegment.substring(index);
            lastSegment = lastSegment.substring(0, index);
        }
        return lastSegment + Math.abs(url.hashCode()) + extension;
    }

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

    public static void close(Socket s) {
        if (s == null) {
            return;
        }
        try {
            s.close();
        } catch (IOException e) {
            Log.e(LOGTAG, "Unable to close", e);
        }
    }

    public static void close(HttpEntity entity) {
        if (entity == null) {
            return;
        }
        try {
            entity.consumeContent();
        } catch (IOException e) {
            Log.e(LOGTAG, "Unable to close", e);
        }
    }
}
