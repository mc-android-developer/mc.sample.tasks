package mc.sample.tasks.fortegroup.tasks;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import java.io.File;
import java.lang.ref.WeakReference;

import mc.sample.tasks.fortegroup.R;
import mc.sample.tasks.fortegroup.cache.IBitmapCache;
import mc.sample.tasks.fortegroup.taskservice.Task;
import mc.sample.tasks.fortegroup.utils.ImageLoader;

public class LoadBitmapTask extends Task {
    private final WeakReference<ImageView> mImageViewRef;
    private final ImageLoader mImageLoader;
    private final IBitmapCache mCache;
    private volatile Bitmap mBitmap;
    private final String mUrl;

    public LoadBitmapTask(String url, ImageView imageView, IBitmapCache cache) {
        super();
        imageView.setTag(url);

        Context context = imageView.getContext();
        int width = context.getResources().getDimensionPixelSize(R.dimen.img_preview_width);
        int height = context.getResources().getDimensionPixelSize(R.dimen.img_preview_height);
        mImageLoader = new ImageLoader(width, height);
        mImageViewRef = new WeakReference<>(imageView);
        mCache = cache;
        mUrl = url;
    }

    @Override
    public void drive() throws Exception {
        File tmpFile = null;
        try {
            tmpFile = File.createTempFile("img", "tmp");
            mImageLoader.loadUrlToFile(mUrl, tmpFile);
            mBitmap = mImageLoader.loadFromFile(tmpFile);
            if (mBitmap == null) {
                throw new Exception("Can't get bitmap. Tmp file:" + tmpFile.getAbsolutePath());
            }
            mCache.put(mUrl, mBitmap);
        } finally {
            if (tmpFile != null) {
                tmpFile.delete();
            }
        }
    }

    @Override
    public void driveUi() {
        ImageView imageView = mImageViewRef.get();
        if (imageView != null) {
            if (mUrl.equals(imageView.getTag())) {
                imageView.setImageBitmap(mBitmap);
            }
        }
    }
}
