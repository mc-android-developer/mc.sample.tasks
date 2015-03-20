package mc.sample.tasks.fortegroup.taskservice;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TaskService extends Service {
    private static final String LOGTAG = TaskService.class.getSimpleName();
    private static final String TASK_KEY = "task_key";
    private static final int THREAD_POOL_SIZE = 5;

    public static String execute(Context context, Task task) {
        String key = TaskHolder.put(task);
        Intent in = new Intent(context, TaskService.class);
        in.putExtra(TASK_KEY, key);
        context.startService(in);
        return key;
    }

    private static final ExecutorService EXECUTOR = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
    private static final Handler HANDLER = new Handler();
    private int mTaskCounter;
    private long mTime;

    @Override
    public void onCreate() {
        super.onCreate();
        mTime = System.currentTimeMillis();
        Log.v(LOGTAG, "Service onCreate(). Time:" + mTime);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.v(LOGTAG, "Service onDestroy(). Work time:" + (System.currentTimeMillis() - mTime) + "; Time:" + System.currentTimeMillis());
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Log.v(LOGTAG, "Service onLowMemory(). Time:" + System.currentTimeMillis());
    }

    Handler getHandler() {
        return HANDLER;
    }

    synchronized void onNewTask() {
        mTaskCounter++;
        Log.v(LOGTAG, "TASK_TRACE Task started. Tasks num:" + mTaskCounter + "; Time:" + System.currentTimeMillis());
    }

    synchronized void onTaskComplete() {
        mTaskCounter--;
        Log.v(LOGTAG, "TASK_TRACE Task complete. Tasks num:" + mTaskCounter + "; Time:" + System.currentTimeMillis());
        if (mTaskCounter < 1) {
            Log.v(LOGTAG, "No tasks. Stop service. Time:" + System.currentTimeMillis());
            stopSelf();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String key = intent.getStringExtra(TASK_KEY);
        if (TextUtils.isEmpty(key)) {
            throw new IllegalArgumentException("No task key passed");
        }
        Task task = TaskHolder.get(key);
        if (task == null) {
            throw new IllegalArgumentException("No task found for key:" + key);
        }
        task.init(this);
        onNewTask();
        EXECUTOR.execute(task);
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Use TaskService.execute(.)");
    }
}
