package mc.sample.tasks.fortegroup.cache;

import android.graphics.Bitmap;

public interface IBitmapCache {

    public Bitmap get(String key);

    public void put(String key, Bitmap bitmap);

    public void remove(String key);

    public boolean has(String key);

    public void clear();
}
