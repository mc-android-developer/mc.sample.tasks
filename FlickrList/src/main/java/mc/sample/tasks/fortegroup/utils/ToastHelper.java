package mc.sample.tasks.fortegroup.utils;

import android.content.Context;
import android.widget.Toast;

public class ToastHelper {
    public static Toast showToast(Context context, int strResId) {
        Toast t = Toast.makeText(context, strResId, Toast.LENGTH_LONG);
        t.show();
        return t;
    }
}
