package mc.sample.tasks.swrve;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import mc.sample.tasks.swrve.taskservice.TaskService;
import mc.sample.tasks.swrve.tasks.RotateBitmapTask;
import mc.sample.tasks.swrve.util.ImageLoader;

import static android.view.ViewGroup.LayoutParams;

public class RotateDialog extends Dialog {
    private final ProgressBar mImageProgress;
    private final ImageButton mRotateRight;
    private final ImageButton mRotateLeft;
    private final ImageView mImageView;
    private final FrameLayout mFrame;

    private final Bitmap mBitmap;
    private final Context mContext;
    private final int mAngle;
    private int mCurrentAngle;

    @SuppressLint("InflateParams")
    public RotateDialog(Context context, Bitmap bitmap, int initAngle, int rotateAngle) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        mContext = context;
        mBitmap = bitmap;
        mAngle = rotateAngle;
        mCurrentAngle = initAngle;
        final View dialog = LayoutInflater.from(mContext).inflate(R.layout.dialog, null);
        mImageProgress = (ProgressBar) dialog.findViewById(R.id.imageRotateProgress);
        mRotateLeft = (ImageButton) dialog.findViewById(R.id.rotateLeft);
        mRotateLeft.setOnClickListener(mClickListener);
        mRotateRight = (ImageButton) dialog.findViewById(R.id.rotateRight);
        mRotateRight.setOnClickListener(mClickListener);
        mFrame = (FrameLayout) dialog.findViewById(R.id.frame);
        mImageView = (ImageView) dialog.findViewById(R.id.image);
        mImageView.setImageBitmap(mBitmap);
        setImageHeight();
        setContentView(dialog);

        if (mCurrentAngle != 0) {
            TaskService.execute(mContext, new RotateBitmapTask(RotateDialog.this, mBitmap, mCurrentAngle));
        }
    }

    private void setImageHeight() {
        int[] wh = ImageLoader.getScreenSize(App.app());
        int maxHeight = wh[1] - ImageLoader.dpToPx(mContext, 160);
        int desirableHeight = (int) (Math.max(mBitmap.getHeight(), mBitmap.getWidth()) * 1.5);
        int height = desirableHeight > maxHeight ? maxHeight : desirableHeight;
        LayoutParams lp = mFrame.getLayoutParams();
        if (lp == null) {
            lp = new LayoutParams(height, height);
        } else {
            lp.height = lp.width = height;
        }
        mFrame.setLayoutParams(lp);
    }

    private final View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            enableButtons(false);
            switch (v.getId()) {
                case R.id.rotateLeft:
                    TaskService.execute(mContext, new RotateBitmapTask(RotateDialog.this, mBitmap, mCurrentAngle - mAngle));
                    break;
                case R.id.rotateRight:
                    TaskService.execute(mContext, new RotateBitmapTask(RotateDialog.this, mBitmap, mCurrentAngle + mAngle));
                    break;
            }
        }
    };

    private void enableButtons(boolean enable) {
        mRotateRight.setEnabled(enable);
        mRotateLeft.setEnabled(enable);
        mImageProgress.setVisibility(enable ? View.INVISIBLE : View.VISIBLE);
    }

    public void setRotatedBitmap(int angle, Bitmap bitmap) {
        mImageView.setImageBitmap(bitmap);
        mCurrentAngle = angle;
        enableButtons(true);
    }

    public void onRotationError(Throwable exception) {
        Toast.makeText(mContext, R.string.rotate_error, Toast.LENGTH_SHORT).show();
        enableButtons(true);
    }

    public int getCurrentAngle() {
        return mCurrentAngle;
    }
}
