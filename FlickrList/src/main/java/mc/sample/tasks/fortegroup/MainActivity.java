package mc.sample.tasks.fortegroup;

import android.accounts.NetworkErrorException;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.*;
import android.widget.AdapterView;
import android.widget.ListView;
import mc.sample.tasks.fortegroup.adapter.FlickrItemAdapter;
import mc.sample.tasks.fortegroup.model.FlickrItem;
import mc.sample.tasks.fortegroup.network.state.ConnectionStateListener;
import mc.sample.tasks.fortegroup.network.state.NetworkBroadcastReceiver;
import mc.sample.tasks.fortegroup.tasks.LoadFlickrDataTask;
import mc.sample.tasks.fortegroup.taskservice.*;
import mc.sample.tasks.fortegroup.utils.ToastHelper;

public class MainActivity extends ActionBarActivity implements ConnectionStateListener {
    private static final String STATE_TASK_ID = "task_id";
    private static final String STATE_FLICKRS = "flickrs";

    private final NetworkBroadcastReceiver mReceiver = new NetworkBroadcastReceiver();
    private FlickrItem[] mItems;
    private ListView mList;
    private String mTaskId;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_main);
        initActionBar();

        mList = (ListView) findViewById(R.id.list);
        mList.setOnItemClickListener(mOnItemClickListener);

        if (state == null) {
            refreshData();
            return;
        }

        mTaskId = state.getString(STATE_TASK_ID);
        Task task = TaskHolder.get(mTaskId);
        if (task != null) {
            ((LoadFlickrDataTask) task).updateActivity(this);
        }

        mItems = (FlickrItem[]) state.getSerializable(STATE_FLICKRS);
        if (mItems != null && mItems.length > 0) {
            onFlickrDataLoaded(mItems);
        } else {
            refreshData();
        }
    }

    private void initActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setIcon(R.drawable.ic_launcher);
        Drawable background = new ColorDrawable(getResources().getColor(R.color.action_bar_color));
        actionBar.setBackgroundDrawable(background);
    }

    @Override
    public void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        state.putString(STATE_TASK_ID, mTaskId);
        state.putSerializable(STATE_FLICKRS, mItems);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                mList.setAdapter(null);
                refreshData();
                return true;
            default:
                return super.onOptionsItemSelected(item);
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

    @Override
    public void onNetworkConnected() {
        if (mList.getAdapter() == null) {
            refreshData();
        }
    }

    @Override
    public void onNetworkDisconnected() {
    }

    private void refreshData() {
        mTaskId = TaskService.execute(this, new LoadFlickrDataTask(this, getString(R.string.data_url)));
    }

    public void onFlickrDataLoaded(FlickrItem[] items) {
        mItems = items;
        mList.setAdapter(new FlickrItemAdapter(MainActivity.this, items));
    }

    public void onFlickrDataLoadError(Throwable e) {
        if (e instanceof NetworkErrorException) {
            ToastHelper.showToast(MainActivity.this, R.string.errmsg_no_network);
        } else {
            ToastHelper.showToast(MainActivity.this, R.string.errmsg_no_data);
        }
    }

    private final AdapterView.OnItemClickListener mOnItemClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
            FlickrItem item = (FlickrItem) v.getTag();
            Intent in = new Intent(Intent.ACTION_VIEW, Uri.parse(item.getLink()));
            startActivity(in);
        }
    };
}
