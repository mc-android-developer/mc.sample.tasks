package mc.sample.tasks.swrve.tasks;

import android.accounts.NetworkErrorException;
import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.os.Build;
import mc.sample.tasks.swrve.App;
import mc.sample.tasks.swrve.MainActivity;
import mc.sample.tasks.swrve.cache.BitmapCache;
import mc.sample.tasks.swrve.taskservice.ActivityTask;
import mc.sample.tasks.swrve.util.*;

import java.io.File;

public class LoadBitmapTask extends ActivityTask<MainActivity> {
    private final ImageLoader mLoader;
    private volatile Bitmap mBitmap;
    private final String mUrl;

    public LoadBitmapTask(MainActivity activity, String url) {
        super(activity);
        mUrl = url;
        mLoader = new ImageLoader(activity);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    @Override
    public void drive() throws Exception {
        BitmapCache cache = App.getBitmapCache();
        mBitmap = cache.get(mUrl);
        if (mBitmap == null) {
            File file = null;
            try {
                file = File.createTempFile("tmp", "", Util.getCacheDir(App.app()));
                file = mLoader.loadUrlToFile(mUrl, file);
                if (file == null) {
                    throw new Exception("Fail to load bitmap from url:" + mUrl);
                }
                mBitmap = mLoader.loadFromFile(file);
                if (mBitmap == null) {
                    throw new Exception("Fail to load bitmap from cache");
                }
                cache.put(mUrl, mBitmap);
            } catch (Exception exception) {
                if (NetworkHelper.hasConnection(App.app())) {
                    throw exception;
                } else {
                    throw new NetworkErrorException(exception);
                }
            } finally {
                if (file != null) {
                    //noinspection ResultOfMethodCallIgnored
                    file.delete();
                }
            }
        }
    }

    @Override
    protected void driveUi(MainActivity activity) throws Throwable {
        activity.onImageLoaded(mBitmap);
    }

    @Override
    protected void finish(MainActivity activity, Throwable exception) throws Throwable {
        if (exception == null) {
            return;
        }

        activity.onImageLoadError(exception);
    }
}
