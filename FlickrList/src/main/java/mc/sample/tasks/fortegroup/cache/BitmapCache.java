package mc.sample.tasks.fortegroup.cache;

import android.content.Context;
import android.graphics.Bitmap;

public class BitmapCache implements IBitmapCache {
    private final MemoryBitmapCache mMemoryCache;
    private final DiskBitmapCache mDiskCache;

    public BitmapCache(Context context) {
        this.mDiskCache = new DiskBitmapCache(context);
        this.mMemoryCache = new MemoryBitmapCache();
    }

    @Override
    public Bitmap get(String key) {
        Bitmap bitmap = mMemoryCache.get(key);
        if (bitmap == null) {
            bitmap = mDiskCache.get(key);
            if (bitmap != null) {
                mMemoryCache.put(key, bitmap);
            }
        }
        return bitmap;
    }

    @Override
    public void put(String key, Bitmap bitmap) {
        mMemoryCache.put(key, bitmap);
        mDiskCache.put(key, bitmap);
    }

    @Override
    public void remove(String key) {
        mMemoryCache.remove(key);
        mDiskCache.remove(key);
    }

    @Override
    public boolean has(String key) {
        return mMemoryCache.has(key) | mDiskCache.has(key);
    }

    @Override
    public void clear() {
        mMemoryCache.clear();
        mDiskCache.clear();
    }
}
