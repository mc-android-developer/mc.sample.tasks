package mc.sample.tasks.swrve.cache;

import android.graphics.Bitmap;

public interface BitmapCache {

    public Bitmap get(String key);

    public void put(String key, Bitmap bitmap);

    public void remove(String key);

    public boolean has(String key);

    public void clear();
}