package mc.sample.tasks.swrve;

import android.accounts.NetworkErrorException;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBarActivity;
import android.util.Patterns;
import android.view.View;
import android.widget.*;
import mc.sample.tasks.swrve.tasks.LoadBitmapTask;
import mc.sample.tasks.swrve.taskservice.*;

import static android.view.View.OnClickListener;

public class MainActivity extends ActionBarActivity {

    //Big image
    //private static final String DEFAULT_IMAGE_URL = "http://rickpdx.files.wordpress.com/2008/03/huge-ssc2006-17a.jpg";
    //Not an image
    //private static final String DEFAULT_IMAGE_URL = "http://martinfowler.com/apsupp/spec.pdf";
    //Not exists
    //private static final String DEFAULT_IMAGE_URL = "http://google.com/image.jpg";

    private static final String DEFAULT_IMAGE_URL = "http://1.bp.blogspot.com/-fGxCCVFj348/TiRpCPthL4I/AAAAAAAABIA/moAWhrHKDDg/s1600/fffffffffff.png";
    private static final String DEFAULT_ROTATE_ANGLE = "45";

    private static final String STATE_TASK_HOLDER_KEY = "state_task_holder_key";
    private static final String STATE_CURRENT_ANGLE = "state_current_angle";
    private static final String STATE_ROTATE_DIALOG = "state_rotate_dialog";
    private static final String STATE_LOAD_BUTTON = "state_load_button";

    private ProgressBar mLoadImageProgress;
    private Button mLoadImageButton;
    private EditText mAngleEdit;
    private EditText mUrlEdit;

    private RotateDialog mRotateDialog;
    private String mLoadTaskHolderKey;
    private int mCurrentAngle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAngleEdit = (EditText) findViewById(R.id.angleEdit);
        mAngleEdit.setText(DEFAULT_ROTATE_ANGLE);
        mUrlEdit = (EditText) findViewById(R.id.urlEdit);
        mUrlEdit.setText(DEFAULT_IMAGE_URL);
        mLoadImageButton = (Button) findViewById(R.id.loadImageButton);
        mLoadImageButton.setOnClickListener(mLoadImageListener);
        mLoadImageProgress = (ProgressBar) findViewById(R.id.loadImageProgress);
    }

    @Override
    public void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        state.putBoolean(STATE_LOAD_BUTTON, mLoadImageButton.isEnabled());
        state.putString(STATE_TASK_HOLDER_KEY, mLoadTaskHolderKey);
        if (mRotateDialog != null) {
            state.putBoolean(STATE_ROTATE_DIALOG, true);
            state.putInt(STATE_CURRENT_ANGLE, mRotateDialog.getCurrentAngle());
        }
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle state) {
        super.onRestoreInstanceState(state);

        mCurrentAngle = state.getInt(STATE_CURRENT_ANGLE, 0);
        boolean showDialog = state.getBoolean(STATE_ROTATE_DIALOG, false);
        boolean loadButtonEnabled = state.getBoolean(STATE_LOAD_BUTTON, true);
        if (!loadButtonEnabled) {
            mLoadTaskHolderKey = state.getString(STATE_TASK_HOLDER_KEY);
            Task task = TaskHolder.get(mLoadTaskHolderKey);
            if (task != null) {
                ((LoadBitmapTask) task).updateActivity(this);
            }
        } else if (showDialog) {
            mLoadImageListener.onClick(mLoadImageButton);
        }
        enableButtons(loadButtonEnabled);
    }

    public void onImageLoaded(Bitmap bitmap) {
        enableButtons(true);
        int angle = Integer.parseInt(mAngleEdit.getText().toString());
        mRotateDialog = new RotateDialog(this, bitmap, mCurrentAngle, angle);
        mRotateDialog.setOnDismissListener(mHideDialogListener);
        mRotateDialog.show();
    }

    public void onImageLoadError(Throwable exception) {
        enableButtons(true);
        int resId = exception instanceof NetworkErrorException ? R.string.no_network_error : R.string.unable_download_image;
        Toast.makeText(MainActivity.this, resId, Toast.LENGTH_SHORT).show();
    }

    private void enableButtons(boolean enable) {
        mLoadImageButton.setEnabled(enable);
        mLoadImageProgress.setVisibility(enable ? View.INVISIBLE : View.VISIBLE);
    }

    private final OnClickListener mLoadImageListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            String url = mUrlEdit.getText().toString();
            if (url != null && Patterns.WEB_URL.matcher(url).matches()) {
                enableButtons(false);
                mLoadTaskHolderKey = TaskService.execute(MainActivity.this, new LoadBitmapTask(MainActivity.this, url));
            } else {
                Toast.makeText(MainActivity.this, R.string.invalid_url, Toast.LENGTH_SHORT).show();
            }
        }
    };

    private final DialogInterface.OnDismissListener mHideDialogListener = new DialogInterface.OnDismissListener() {
        @Override
        public void onDismiss(DialogInterface dialog) {
            mRotateDialog = null;
            mCurrentAngle = 0;
        }
    };
}
