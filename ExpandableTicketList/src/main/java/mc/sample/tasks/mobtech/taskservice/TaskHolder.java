package mc.sample.tasks.mobtech.taskservice;

import android.text.TextUtils;
import android.util.Log;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class TaskHolder {
    private static final String LOGTAG = Task.class.getSimpleName();

    private static final Map<String, Task> HOLDER = new ConcurrentHashMap<>();

    public static Task get(String key) {
        if (TextUtils.isEmpty(key)) {
            return null;
        }
        Task task = HOLDER.get(key);
        Log.v(LOGTAG, "Task get. Id:" + (task == null ? "null" : task.getId()) + "; Key:" + key + "; Size:" + HOLDER.size() + "; Time:" + System.currentTimeMillis());
        return task;
    }

    public static String put(Task task) {
        if (task == null) {
            return null;
        }
        String key = UUID.randomUUID().toString();
        task.setHolderKey(key);
        HOLDER.put(key, task);
        Log.v(LOGTAG, "Task put. Id:" + task.getId() + "; Key:" + key + "; Size:" + HOLDER.size() + "; Time:" + System.currentTimeMillis());
        return key;
    }

    static Task remove(String key) {
        if (TextUtils.isEmpty(key)) {
            return null;
        }
        Task task = HOLDER.remove(key);
        Log.v(LOGTAG, "Task remove. Id:" + (task == null ? "null" : task.getId()) + "; Key:" + key + "; Size:" + HOLDER.size() + "; Time:" + System.currentTimeMillis());
        return task;
    }
}
