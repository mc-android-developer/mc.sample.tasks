package mc.sample.tasks.fortegroup.taskservice;

import android.app.Activity;
import android.util.Log;

import java.lang.ref.WeakReference;

public abstract class ActivityTask<A extends Activity> extends Task {
    private static final String LOGTAG = ActivityTask.class.getSimpleName();

    private WeakReference<A> mActivityRef;
    private Throwable mException;
    private boolean mFinished;

    protected ActivityTask(A activity) {
        mActivityRef = new WeakReference<>(activity);
    }

    @Override
    protected synchronized void driveUi() throws Throwable {
        A activity = mActivityRef.get();
        if (activity != null && !activity.isFinishing()) {
            driveUi(activity);
        } else {
            Log.v(LOGTAG, "TASK_TRACE: Activity gone. Ignore driveUi(). Id:" + getId() + "; Execution time:" + (System.currentTimeMillis() - getTime()));
        }
        mFinished = true;
    }

    @SuppressWarnings("RedundantThrows")
    protected abstract void driveUi(A activity) throws Throwable;

    @Override
    protected synchronized void finish(Throwable exception) throws Throwable {
        A activity = mActivityRef.get();
        if (activity != null && !activity.isFinishing()) {
            finish(activity, exception);
        } else {
            Log.v(LOGTAG, "TASK_TRACE: Activity gone. Ignore finish(). Id:" + getId() + "; Execution time:" + (System.currentTimeMillis() - getTime()));
        }
        mException = exception;
        mFinished = true;
    }

    @SuppressWarnings({"RedundantThrows", "UnusedParameters"})
    protected void finish(A activity, Throwable exception) throws Throwable {
    }

    public synchronized void updateActivity(A activity) {
        if (mFinished && activity != null) {
            if (mException == null) {
                try {
                    driveUi(activity);
                    Log.v(LOGTAG, "TASK_TRACE: Task method driveUi() complete. Id:" + getId() + "; Execution time:" + (System.currentTimeMillis() - getTime()));
                } catch (Throwable exception) {
                    onFinish(activity, exception);
                } finally {
                    onFinish(activity, null);
                }
            } else {
                onFinish(activity, mException);
            }
        } else {
            mActivityRef = new WeakReference<>(activity);
        }
    }

    private void onFinish(A activity, Throwable throwable) {
        try {
            finish(activity, throwable);
            Log.v(LOGTAG, "TASK_TRACE: Task method finish() complete. Id:" + getId() + "; Execution time:" + (System.currentTimeMillis() - getTime()));
        } catch (Throwable t) {
            Log.e(LOGTAG, "TASK_EXCEPTION: Exception in task method finish(). Id:" + getId() + "; Execution time:" + (System.currentTimeMillis() - getTime()), t);
        }
    }
}

