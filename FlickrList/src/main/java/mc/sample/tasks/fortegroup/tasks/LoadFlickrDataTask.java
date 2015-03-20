package mc.sample.tasks.fortegroup.tasks;

import android.accounts.NetworkErrorException;
import android.text.TextUtils;

import mc.sample.tasks.fortegroup.MainActivity;
import mc.sample.tasks.fortegroup.model.FlickrItem;
import mc.sample.tasks.fortegroup.parser.Parser;
import mc.sample.tasks.fortegroup.taskservice.ActivityTask;
import mc.sample.tasks.fortegroup.utils.NetworkHelper;

public class LoadFlickrDataTask extends ActivityTask<MainActivity> {

    private volatile FlickrItem[] mItems;
    private final String mUrl;

    public LoadFlickrDataTask(MainActivity activity, String url) {
        super(activity);
        mUrl = url;
    }

    @Override
    protected void drive() throws Throwable {
        if (!NetworkHelper.hasConnection()) {
            throw new NetworkErrorException("No network detected");
        }

        String data = NetworkHelper.getAsText(mUrl, "utf-8");
        if (TextUtils.isEmpty(data)) {
            throw new Exception("Can not load data from url:" + mUrl);
        }

        mItems = Parser.parse(data);
        if (mItems == null || mItems.length == 0) {
            throw new Exception("Can not parse loaded data:" + data);
        }
    }

    @Override
    protected void driveUi(MainActivity activity) {
        activity.onFlickrDataLoaded(mItems);
    }

    @Override
    protected synchronized void finish(MainActivity activity, Throwable exception) {
        if (exception == null) {
            return;
        }
        activity.onFlickrDataLoadError(exception);
    }
}
