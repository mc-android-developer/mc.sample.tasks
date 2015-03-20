package mc.sample.tasks.swrve.tasks;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.util.Log;
import mc.sample.tasks.swrve.App;
import mc.sample.tasks.swrve.RotateDialog;
import mc.sample.tasks.swrve.taskservice.Task;
import mc.sample.tasks.swrve.util.ImageLoader;

import java.lang.ref.WeakReference;

public class RotateBitmapTask extends Task {
    private static final String LOGTAG = RotateBitmapTask.class.getSimpleName();

    private final WeakReference<RotateDialog> mRotateDialogRef;
    private volatile Bitmap mBitmap;
    private final int mAngle;

    public RotateBitmapTask(RotateDialog dialog, Bitmap bitmap, int angle) {
        super();
        mRotateDialogRef = new WeakReference<>(dialog);
        mBitmap = bitmap;
        mAngle = getAngle(angle);
    }

    private int getAngle(int angle) {
        int newAngle = angle % 360;
        if (newAngle < 0) {
            newAngle = 360 + newAngle;
        }
        return newAngle;
    }

    @Override
    public void drive() throws Exception {
        if (mAngle == 0) {
            return;
        }

        float div = mAngle / 30f;
        if (div == Math.round(div)) {
            rotateWithCache(mAngle);
        } else {
            rotate(mAngle);
        }
    }

    private void rotateWithCache(int angle) {
        String key = "rt" + angle + "_" + ImageLoader.getBitmapHashCode(mBitmap);
        Bitmap bitmap = App.getBitmapCache().get(key);
        if (bitmap == null) {
            rotate(angle);
            App.getBitmapCache().put(key, mBitmap);
        } else {
            mBitmap = bitmap;
        }
    }

    private void rotate(int angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        Log.v(LOGTAG, "Rotate on angle:" + angle);
        Bitmap bitmap = Bitmap.createBitmap(mBitmap, 0, 0, mBitmap.getWidth(), mBitmap.getHeight(), matrix, true);
        if (bitmap == null) {
            throw new NullPointerException("Rotation fail. Bitmap is null");
        }
        mBitmap = bitmap;
    }

    @Override
    public void driveUi() throws Exception {
        RotateDialog dialog = mRotateDialogRef.get();
        if (dialog != null && dialog.isShowing()) {
            dialog.setRotatedBitmap(mAngle, mBitmap);
        }
    }

    @Override
    public void finish(Throwable exception) {
        if (exception == null) {
            return;
        }

        RotateDialog dialog = mRotateDialogRef.get();
        if (dialog != null && dialog.isShowing()) {
            dialog.onRotationError(exception);
        }
    }
}
