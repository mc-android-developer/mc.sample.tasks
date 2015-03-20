package mc.sample.tasks.treto.network.state;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.concurrent.LinkedBlockingQueue;

import mc.sample.tasks.treto.network.NetworkHelper;

public class NetworkBroadcastReceiver extends BroadcastReceiver {
    private static final String LOGTAG = NetworkBroadcastReceiver.class.getSimpleName();
    private static final ConnectionThread CONNECTION_THREAD = new ConnectionThread();

    public void onReceive(final Context context, final Intent intent) {
        if (context instanceof ConnectionStateListener) {
            CONNECTION_THREAD.post((ConnectionStateListener) context);
        } else {
            throw new IllegalArgumentException("Context does not implements ConnectionStateListener");
        }
    }

    private static final class ConnectionThread extends Thread {
        private final LinkedBlockingQueue<WeakReference<ConnectionStateListener>> mQueue = new LinkedBlockingQueue<>();
        private final Handler mUiHandler = new Handler();

        public ConnectionThread() {
            super("NetworkBroadcastReceiver#ConnectionThread");
            Log.v(LOGTAG, "ConnectionThread started");
            setDaemon(true);
            start();
        }

        public void post(ConnectionStateListener listener) {
            mQueue.add(new WeakReference<>(listener));
        }

        @Override
        @SuppressWarnings("InfiniteLoopStatement")
        public void run() {
            try {
                boolean lastNetworkState = NetworkHelper.hasConnection();
                while (true) {
                    final WeakReference<ConnectionStateListener> ref = mQueue.take();
                    if (ref.get() == null) {
                        continue;
                    }

                    final boolean newNetworkState = NetworkHelper.hasConnection();
                    if (lastNetworkState == newNetworkState) {
                        continue;
                    }

                    lastNetworkState = newNetworkState;
                    mUiHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            ConnectionStateListener listener = ref.get();
                            if (listener == null) {
                                return;
                            }

                            if (newNetworkState) {
                                listener.onNetworkConnected();
                            } else {
                                listener.onNetworkDisconnected();
                            }
                        }
                    });
                }
            } catch (Exception e) {
                Log.v(LOGTAG, "ConnectionThread exception", e);
            }
        }
    }
}
