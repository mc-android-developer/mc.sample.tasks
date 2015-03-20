package mc.sample.tasks.swrve.cache;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.util.Log;
import mc.sample.tasks.swrve.util.ImageLoader;
import mc.sample.tasks.swrve.util.Util;

import java.io.*;

class DiskBitmapCache implements BitmapCache {
    private static final String LOGTAG = DiskBitmapCache.class.getSimpleName();
    private static final String BITMAP_CACHE_DIR = "image_cache";
    private static final int QUALITY = 70;

    private final ImageLoader mImageLoader;
    private final Context mContext;

    public DiskBitmapCache(Context context) {
        mContext = context.getApplicationContext();
        mImageLoader = new ImageLoader(mContext);
    }

    @Override
    public Bitmap get(String key) {
        File file = getFile(key);
        if (file.length() == 0) {
            return null;
        }
        try {
            return mImageLoader.loadFromFile(file);
        } catch (FileNotFoundException e) {
            return null;
        }
    }

    private File getFile(String url) {
        return new File(getCacheDir(), Util.urlToFileName(url));
    }

    // Cache dir have to be created any time because sdcard status may change
    private File getCacheDir() {
        File cacheDir = new File(Util.getCacheDir(mContext), BITMAP_CACHE_DIR);
        cacheDir.mkdirs();
        if (!cacheDir.exists()) {
            throw new RuntimeException("Cannot create image cache dir:" + cacheDir.getAbsolutePath());
        }
        return cacheDir;
    }

    @Override
    public void put(String key, Bitmap bitmap) {
        if (bitmap == null) {
            remove(key);
            return;
        }
        File file = getFile(key);
        if (file.length() > 0) {
            return;
        }
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            bitmap.compress(bitmap.hasAlpha() ? CompressFormat.PNG : CompressFormat.JPEG, QUALITY, fos);
            fos.close();
            fos = null;
        } catch (IOException e) {
            Log.e(LOGTAG, "Error on saving bitmap", e);
        } finally {
            Util.close(fos);
        }
    }

    @Override
    public void remove(String key) {
        File file = getFile(key);
        if (file.exists()) {
            if (!file.delete()) {
                Log.e(LOGTAG, "Cannot delete file " + file);
            }
        }
    }

    @Override
    public boolean has(String key) {
        File file = getFile(key);
        return file.length() <= 0;
    }

    @Override
    public void clear() {
        File[] files = getCacheDir().listFiles();
        if (files != null) {
            for (File f : files) {
                if (!f.isDirectory()) {
                    if (!f.delete()) {
                        Log.e(LOGTAG, "Cannot delete file " + f);
                    }
                }
            }
        }
    }
}
