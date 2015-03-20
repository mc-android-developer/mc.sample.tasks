package mc.sample.tasks.swrve;

import android.app.Application;

import mc.sample.tasks.swrve.cache.BitmapCacheBase;
import mc.sample.tasks.swrve.cache.BitmapCache;

public class App extends Application {
    public static App app() {
        return sApp;
    }

    public static BitmapCache getBitmapCache() {
        return sBitmapCache;
    }

    private static BitmapCache sBitmapCache;
    private static App sApp;

    @Override
    public void onCreate() {
        super.onCreate();
        init();
    }

    private void init() {
        sApp = this;
        sBitmapCache = new BitmapCacheBase(this);
    }
}
