package mc.sample.tasks.xing;

import android.app.*;
import android.content.*;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import mc.sample.tasks.xing.data.CursorAdapter;
import mc.sample.tasks.xing.data.ProjectsDataLoader;
import mc.sample.tasks.xing.model.Project;

import static mc.sample.tasks.xing.data.ProjectsDataLoader.*;

public class MainActivity extends ListActivity {
    private static final String LOGTAG = MainActivity.class.getSimpleName();
    private static final int LOAD_PAGE_TRIGGER = 5;
    private static final int LOADER_ID = 1;

    private ProjectsDataLoader mDataLoader;
    private SimpleCursorAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAdapter = new CursorAdapter(this);
        setListAdapter(mAdapter);
        getListView().setOnScrollListener(mScrollListener);
        getListView().setOnItemClickListener(mClickListener);
        getListView().setOnItemLongClickListener(mLongClickListener);

        mDataLoader = (ProjectsDataLoader) getLoaderManager().<Cursor>getLoader(LOADER_ID);
        if (mDataLoader == null) {
            mDataLoader = new ProjectsDataLoader(this);
        }
        getLoaderManager().initLoader(LOADER_ID, null, mLoaderCallback);
    }

    private void addItems() {
        Log.d(LOGTAG, "addItems");
        mDataLoader.nextPage();
    }

    private final AdapterView.OnItemClickListener mClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Log.d(LOGTAG, "onClick");
            Intent in = new Intent(MainActivity.this, ProjectDetails.class);
            in.putExtra(ProjectDetails.PROJECT, GSON.toJson(view.getTag()));
            startActivity(in);
        }
    };

    private final AdapterView.OnItemLongClickListener mLongClickListener = new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            Log.d(LOGTAG, "onLongClick");
            final Project project = (Project) view.getTag();
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle(getString(R.string.go_to_repo));
            builder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent in = new Intent(Intent.ACTION_VIEW, Uri.parse(project.getUrl()));
                    startActivity(in);
                }
            });
            builder.setNegativeButton(getString(R.string.no), null);
            builder.create().show();
            return true;
        }
    };

    private final LoaderManager.LoaderCallbacks<Cursor> mLoaderCallback = new LoaderManager.LoaderCallbacks<Cursor>() {
        @Override
        public ProjectsDataLoader onCreateLoader(int id, Bundle args) {
            Log.d(LOGTAG, "onCreateLoader");
            return mDataLoader;
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            Log.d(LOGTAG, "onLoadFinished");
            mAdapter.changeCursor(data);
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            Log.d(LOGTAG, "onLoaderReset");
            mAdapter.changeCursor(null);
        }
    };

    private final AbsListView.OnScrollListener mScrollListener = new AbsListView.OnScrollListener() {
        private int mFirstVisibleItem;
        private int mVisibleItemCount;
        private int mTotalItemCount;

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            if (stateSame(firstVisibleItem, visibleItemCount, totalItemCount)) {
                return;
            }

            if (firstVisibleItem + visibleItemCount >= (totalItemCount - LOAD_PAGE_TRIGGER) && totalItemCount != 0) {
                addItems();
            }
        }

        private boolean stateSame(int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            if (mFirstVisibleItem == firstVisibleItem && mVisibleItemCount == visibleItemCount && mTotalItemCount == totalItemCount) {
                return true;
            }
            mFirstVisibleItem = firstVisibleItem;
            mVisibleItemCount = visibleItemCount;
            mTotalItemCount = totalItemCount;
            return false;
        }
    };
}