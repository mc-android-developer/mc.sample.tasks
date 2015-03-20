package mc.sample.tasks.swrve.util;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Build;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ImageLoader {
    private static final String LOGTAG = ImageLoader.class.getSimpleName();
    private static final byte[] FILE_BUFFER = new byte[16 * 1024];
    private static final byte[] NET_BUFFER = new byte[8192];

    private final int[] mScreenSize;
    private final HttpClient mHttpClient = new DefaultHttpClient();

    public ImageLoader(Context context) {
        mScreenSize = getScreenSize(context);
    }

    public File loadUrlToFile(final String url, final File file) throws IOException {
        if (TextUtils.isEmpty(url)) {
            return null;
        }

        InputStream is = null;
        OutputStream os = null;
        HttpEntity entity = null;
        try {
            HttpUriRequest request = new HttpGet(url);
            HttpResponse response = mHttpClient.execute(request);
            entity = response.getEntity();
            int status = response.getStatusLine().getStatusCode();
            if (status != 200 || entity == null) {
                Log.e(LOGTAG, "Unable to load file from url: " + url + "\nBad HTTP response status: " + response.getStatusLine() + " or entity is null");
                return null;
            }
            is = entity.getContent();
            os = new FileOutputStream(file);
            copyStream(is, os);
            os.flush();
            return file;
        } finally {
            Util.close(is);
            Util.close(os);
            Util.close(entity);
        }
    }

    private void copyStream(InputStream in, OutputStream out) throws IOException {
        int c;
        while ((c = in.read(NET_BUFFER)) != -1) {
            out.write(NET_BUFFER, 0, c);
        }
    }

    public Bitmap loadFromFile(File file) throws FileNotFoundException {
        Bitmap bitmap = null;
        if (file.exists()) {
            BitmapFactory.Options options = getBitmapOptions(file);
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(file);
                bitmap = BitmapFactory.decodeStream(fis, null, options);
            } finally {
                Util.close(fis);
            }
        }
        return bitmap;
    }

    private BitmapFactory.Options getBitmapOptions(File file) throws FileNotFoundException {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(fis, null, options);
            options.inTempStorage = FILE_BUFFER;
            options.inJustDecodeBounds = false;
            options.inSampleSize = calculateInSampleSize(options.outWidth, options.outHeight, mScreenSize[0], mScreenSize[1]);
            return options;
        } finally {
            Util.close(fis);
        }
    }

    public static int calculateInSampleSize(int width, int height, int reqWidth, int reqHeight) {
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;
            while ((halfHeight / inSampleSize) > reqHeight && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public static int[] getScreenSize(Context context) {
        int[] wh = new int[2];
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            Point p = new Point();
            display.getSize(p);
            wh[0] = p.x;
            wh[1] = p.y;
        } else {
            wh[0] = display.getWidth();
            wh[1] = display.getHeight();
        }
        return wh;
    }

    public static int getBitmapHashCode(Bitmap bitmap) {
        if (bitmap == null) {
            return 0;
        }
        return bitmap.getPixel(0, 0) + bitmap.getRowBytes() + bitmap.getHeight() + bitmap.getWidth();
    }

    public static int dpToPx(Context context, int dp) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }
}
