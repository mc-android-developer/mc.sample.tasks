package mc.sample.tasks.xing.data;

import android.annotation.TargetApi;
import android.content.*;
import android.database.Cursor;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import com.google.gson.Gson;
import mc.sample.tasks.xing.R;
import mc.sample.tasks.xing.model.Project;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.atomic.AtomicBoolean;

import static mc.sample.tasks.xing.data.ProjectsDataProvider.*;
import static mc.sample.tasks.xing.util.Util.close;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class ProjectsDataLoader extends CursorLoader {
    private static final String LOGTAG = ProjectsDataLoader.class.getSimpleName();
    private static final int PROJECTS_PER_REQUEST = 20;
    public static final Gson GSON = new Gson();

    private final AtomicBoolean mFetchNewPage = new AtomicBoolean(false);
    private boolean mNoMoreProjects;
    private int mCurrentPage = -1;

    public ProjectsDataLoader(Context context) {
        super(context, URI, null, null, null, ID);
    }

    public synchronized void nextPage() {
        if (!mFetchNewPage.getAndSet(true)) {
            forceLoad();
        }
    }

    @Override
    public synchronized Cursor loadInBackground() {
        Log.d(LOGTAG, "loadInBackground");
        fetchNewPage();

        Cursor cursor = super.loadInBackground();
        if (cursor.getCount() == 0) {
            mFetchNewPage.set(true);
            fetchNewPage();
            cursor = super.loadInBackground();
        }
        if (cursor.getCount() != 0) {
            mCurrentPage = getLastPage(cursor);
        }
        return cursor;
    }

    private void fetchNewPage() {
        if (mFetchNewPage.get()) {
            if (!mNoMoreProjects) {
                Project[] projects = loadFromNetwork();
                if (projects == null || projects.length == 0) {
                    mNoMoreProjects = true;
                } else {
                    Log.d(LOGTAG, "fetchNewPage:" + projects.length);
                    saveData(projects);
                }
            }
            mFetchNewPage.set(false);
        }
    }

    private int getLastPage(Cursor cursor) {
        cursor.moveToLast();
        int index = cursor.getColumnIndex(PAGE);
        int page = cursor.getInt(index);
        cursor.moveToFirst();
        return page;
    }

    private Project[] loadFromNetwork() {
        String url = String.format(getContext().getString(R.string.projects_url), ++mCurrentPage, PROJECTS_PER_REQUEST);
        if (TextUtils.isEmpty(url)) {
            return null;
        }

        Log.d(LOGTAG, url);
        InputStreamReader isr = null;
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestProperty("Connection", "Keep-Alive");
            int status = connection.getResponseCode();
            if (status != 200) {
                Log.e(LOGTAG, "Unable to load from url: " + url);
                return null;
            }
            isr = new InputStreamReader(connection.getInputStream());
            return GSON.fromJson(isr, Project[].class);
        } catch (Exception e) {
            Log.e(LOGTAG, "Loading exception", e);
        } finally {
            close(isr);
            close(connection);
        }
        return null;
    }

    private void saveData(Project[] projects) {
        ContentValues values = new ContentValues();
        for (Project p : projects) {
            values.put(PROJECT_ID, p.getId());
            values.put(REPO_NAME, p.getName());
            values.put(DESCRIPTION, p.getDescription());
            values.put(OWNER_LOGIN, p.getLogin());
            values.put(HTML_URL, p.getUrl());
            values.put(PAGE, mCurrentPage);
            values.put(FORK, p.isFork() ? 1 : 0);
            getContext().getContentResolver().insert(URI, values);
        }
    }
}