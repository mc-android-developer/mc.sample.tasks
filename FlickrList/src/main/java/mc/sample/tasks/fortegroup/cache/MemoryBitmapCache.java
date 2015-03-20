package mc.sample.tasks.fortegroup.cache;

import android.graphics.Bitmap;

import java.lang.ref.SoftReference;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

class MemoryBitmapCache implements IBitmapCache {

    private final Map<String, SoftReference<Bitmap>> mCache = new ConcurrentHashMap<>();

    @Override
    public Bitmap get(String key) {
        SoftReference ref = mCache.get(key);
        if (ref == null) {
            return null;
        }
        return (Bitmap) ref.get();
    }

    @Override
    public void put(String key, Bitmap bitmap) {
        if (mCache.containsKey(key)) {
            return;
        }
        mCache.put(key, new SoftReference(bitmap));
    }

    public void remove(String key) {
        mCache.remove(key);
    }

    @Override
    public boolean has(String key) {
        return get(key) != null;
    }

    @Override
    public void clear() {
        mCache.clear();
    }
}
