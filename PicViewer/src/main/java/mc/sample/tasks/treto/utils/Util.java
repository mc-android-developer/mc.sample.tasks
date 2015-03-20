package mc.sample.tasks.treto.utils;

import android.content.Context;
import android.content.res.Configuration;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import java.io.IOException;
import java.net.Socket;

public class Util {
    private static final String LOGTAG = Util.class.getSimpleName();

    public static boolean isPortrait(Context context) {
        return context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
    }

    public static int[] getScreenSize(Context context) {
        int[] wh = new int[2];
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display d = wm.getDefaultDisplay();
        DisplayMetrics dm = new DisplayMetrics();
        d.getMetrics(dm);
        if (dm.widthPixels < dm.heightPixels) {
            wh[0] = dm.widthPixels;
            wh[1] = dm.heightPixels;
        } else {
            wh[1] = dm.widthPixels;
            wh[0] = dm.heightPixels;
        }
        return wh;
    }

    public static void close(Socket s) {
        try {
            if (s != null) {
                s.close();
            }
        } catch (IOException e) {
            Log.e(LOGTAG, "Error on close socket " + s, e);
        }
    }
}
