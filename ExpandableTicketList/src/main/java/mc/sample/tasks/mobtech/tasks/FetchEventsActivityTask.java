package mc.sample.tasks.mobtech.tasks;

import android.text.TextUtils;
import android.widget.Toast;
import mc.sample.tasks.mobtech.MainActivity;
import mc.sample.tasks.mobtech.R;
import mc.sample.tasks.mobtech.data.CategoryParser;
import mc.sample.tasks.mobtech.data.model.CategoryList;
import mc.sample.tasks.mobtech.util.NetworkHelper;
import mc.sample.tasks.mobtech.taskservice.ActivityTask;

public class FetchEventsActivityTask extends ActivityTask<MainActivity> {

    private volatile CategoryList[] mCategories;
    private volatile int mErrorMessage;
    private final String mUrl;

    public FetchEventsActivityTask(MainActivity activity) {
        super(activity);
        mUrl = activity.getString(R.string.data_url);
    }

    @Override
    protected void drive() throws Throwable {
        String eventsJson = NetworkHelper.getAsText(mUrl);
        if(TextUtils.isEmpty(eventsJson)){
            mErrorMessage = NetworkHelper.hasConnection() ? R.string.errmsg_get_data : R.string.errmsg_no_connection;
            throw new Exception("Cannot get categories");
        }
        mCategories = CategoryParser.parse(eventsJson);
    }

    @Override
    protected void driveUi(MainActivity activity) throws Throwable {
        activity.onEventsFetched(mCategories);
    }

    @Override
    protected void finish(MainActivity activity, Throwable exception) throws Throwable {
        Toast.makeText(activity, mErrorMessage, Toast.LENGTH_SHORT).show();
    }
}
