package mc.sample.tasks.fortegroup.network.state;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import mc.sample.tasks.fortegroup.utils.NetworkHelper;

public class NetworkBroadcastReceiver extends BroadcastReceiver {
    private static volatile boolean mLastNetworkState;

    public void onReceive(final Context context, final Intent intent) {
        if (context instanceof ConnectionStateListener) {
            new ConnectionCheckAsyncTask().execute((ConnectionStateListener) context);
        }
    }

    private class ConnectionCheckAsyncTask extends AsyncTask<ConnectionStateListener, Object, ConnectionStateListener> {
        @Override
        protected ConnectionStateListener doInBackground(ConnectionStateListener... listeners) {
            boolean newNetworkState = NetworkHelper.hasConnection();
            if (mLastNetworkState == newNetworkState) {
                return null;
            }
            mLastNetworkState = newNetworkState;
            return listeners[0];
        }

        @Override
        protected void onPostExecute(ConnectionStateListener listener) {
            if (listener == null) {
                return;
            }

            if (mLastNetworkState) {
                listener.onNetworkConnected();
            } else {
                listener.onNetworkDisconnected();
            }
        }
    }
}
