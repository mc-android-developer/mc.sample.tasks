package mc.sample.tasks.treto;

import android.app.Activity;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;
import mc.sample.task.treto.R;
import mc.sample.tasks.treto.adapter.PictureAdapter;
import mc.sample.tasks.treto.network.NetworkHelper;
import mc.sample.tasks.treto.network.state.ConnectionStateListener;
import mc.sample.tasks.treto.network.state.NetworkBroadcastReceiver;
import mc.sample.tasks.treto.view.SwipeControlViewPager;

import java.util.Arrays;

public class MainActivity extends Activity implements ConnectionStateListener {
    private NetworkBroadcastReceiver mReceiver;
    private PictureAdapter mPictureAdapter;
    private SwipeControlViewPager mPager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        mReceiver = new NetworkBroadcastReceiver();
        mPager = (SwipeControlViewPager) findViewById(R.id.pager);
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                return NetworkHelper.hasConnection();
            }

            @Override
            protected void onPostExecute(Boolean connected) {
                if (connected) {
                    setAdapter();
                } else {
                    Toast.makeText(MainActivity.this, R.string.errmsg_no_connection, Toast.LENGTH_SHORT).show();
                }
            }
        }.execute((Void[]) null);
    }

    private void setAdapter() {
        if (mPictureAdapter == null) {
            String[] images = getResources().getStringArray(R.array.images);
            mPictureAdapter = new PictureAdapter(MainActivity.this, Arrays.asList(images));
            mPager.setAdapter(mPictureAdapter);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        registerReceiver(mReceiver, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);
    }

    public void onNetworkConnected() {
        setAdapter();
    }

    @Override
    public void onNetworkDisconnected() {
    }
}