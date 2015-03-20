package mc.sample.tasks.fortegroup.taskservice;

import android.text.TextUtils;
import android.util.Log;

import java.util.concurrent.atomic.AtomicLong;

public abstract class Task implements Runnable {
    private static final String LOGTAG = Task.class.getSimpleName();
    private static final AtomicLong sTaskCounter = new AtomicLong(100L);

    private final long mId = sTaskCounter.incrementAndGet();
    private TaskService mService;
    private volatile long mTime;
    private String mHolderKey;

    public Task() {
        mTime = System.currentTimeMillis();
        Log.v(LOGTAG, "TASK_TRACE: New task created. Id:" + mId + "; Time:" + mTime);
    }

    void init(TaskService service) {
        mService = service;
    }

    long getId() {
        return mId;
    }

    long getTime() {
        return mTime;
    }

    String getHolderKey() {
        return mHolderKey;
    }

    void setHolderKey(String key) {
        if (!TextUtils.isEmpty(mHolderKey)) {
            throw new RuntimeException("Holder key was already set.");
        }
        this.mHolderKey = key;
    }

    @Override
    public void run() {
        Log.v(LOGTAG, "TASK_TRACE: Task started. Id:" + mId + "; Start timeout:" + (System.currentTimeMillis() - mTime));
        mTime = System.currentTimeMillis();
        try {
            if (mService == null) {
                throw new IllegalStateException("You must call init() before you run this task");
            }
            drive();
            Log.v(LOGTAG, "TASK_TRACE: Task method drive() complete. Id:" + mId + "; Execution time:" + (System.currentTimeMillis() - mTime));
            mService.getHandler().post(new Runnable() {
                @Override
                public void run() {
                    Throwable throwable = null;
                    try {
                        driveUi();
                        Log.v(LOGTAG, "TASK_TRACE: Task method driveUi() complete. Id:" + mId + "; Execution time:" + (System.currentTimeMillis() - mTime));
                    } catch (Throwable t) {
                        Log.e(LOGTAG, "TASK_EXCEPTION: Exception in task method driveUi(). Id:" + mId + "; Execution time:" + (System.currentTimeMillis() - mTime), t);
                        throwable = t;
                    } finally {
                        onFinish(throwable);
                    }
                }
            });
        } catch (final Throwable t) {
            Log.e(LOGTAG, "TASK_EXCEPTION: Exception in task method drive(). Id:" + mId + "; Execution time:" + (System.currentTimeMillis() - mTime), t);
            mService.getHandler().post(new Runnable() {
                @Override
                public void run() {
                    onFinish(t);
                }
            });
        }
    }

    private void onFinish(Throwable throwable) {
        try {
            finish(throwable);
            Log.v(LOGTAG, "TASK_TRACE: Task method finish() complete. Id:" + mId + "; Execution time:" + (System.currentTimeMillis() - mTime));
        } catch (Throwable t) {
            Log.e(LOGTAG, "TASK_EXCEPTION: Exception in task method finish(). Id:" + mId + "; Execution time:" + (System.currentTimeMillis() - mTime), t);
        }
        mService.onTaskComplete();
        TaskHolder.remove(mHolderKey);
    }

    /**
     * Called first and executed in non-UI thread.
     * If any exception happened then finish() called, otherwise driveUi() called.
     *
     * @throws Throwable
     */
    protected abstract void drive() throws Throwable;

    /**
     * Called in UI thread in case of successful completion of drive().
     * If any exception happened then finish() called.
     *
     * @throws Throwable
     */
    protected abstract void driveUi() throws Throwable;

    /**
     * Called in UI thread after driveUi() and/or drive() completion.
     * This method is always called, no matter if there were any exceptions in drive() or driveUi().
     *
     * @param exception is null if both drive() and driveUi() executed without exceptions.
     * @throws Throwable
     */
    protected void finish(Throwable exception) throws Throwable {
    }
}
