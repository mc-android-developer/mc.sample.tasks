package mc.sample.tasks.mobtech;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;
import mc.sample.tasks.mobtech.adapter.CategoryAdapter;
import mc.sample.tasks.mobtech.data.model.Category;
import mc.sample.tasks.mobtech.data.model.CategoryList;
import mc.sample.tasks.mobtech.tasks.FetchEventsActivityTask;
import mc.sample.tasks.mobtech.taskservice.TaskService;

public class MainActivity extends ActionBarActivity {
    private TypefaceKeeper mTypefaceKeeper;
    private ExpandableListView mExpandList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        mTypefaceKeeper = new TypefaceKeeper(this);
        initActionBar();

        mExpandList = (ExpandableListView) findViewById(R.id.category_list);
        TaskService.execute(this, new FetchEventsActivityTask(this));
    }

    private void initActionBar() {
        Toolbar actionBar = (Toolbar) findViewById(R.id.actionBar);
        setSupportActionBar(actionBar);
        ImageView backButton = (ImageView) actionBar.findViewById(R.id.back_button);
        backButton.setOnClickListener(mBackClickListener);
        TextView title = (TextView) this.findViewById(R.id.title);
        title.setTypeface(mTypefaceKeeper.getRobotoBold());
        TextView subtitle = (TextView) this.findViewById(R.id.subtitle);
        subtitle.setTypeface(mTypefaceKeeper.getRoboto());
    }

    public void onEventsFetched(CategoryList[] categories) {
        final CategoryAdapter adapter = new CategoryAdapter(this, categories, mTypefaceKeeper, mListItemClickListener);
        mExpandList.setAdapter(adapter);
    }

    private final OnClickListener mBackClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            onBackPressed();
        }
    };

    private final OnClickListener mListItemClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            Category category = (Category) v.getTag();
            Toast.makeText(MainActivity.this, getString(R.string.pick_category) + category.getId(), Toast.LENGTH_SHORT).show();
        }
    };
}
