package mc.sample.tasks.xing.data;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.SimpleCursorAdapter;
import mc.sample.tasks.xing.R;
import mc.sample.tasks.xing.model.Project;

import static mc.sample.tasks.xing.data.ProjectsDataProvider.*;

public class CursorAdapter extends SimpleCursorAdapter {
    private static final String[] PROJECTION = new String[]{REPO_NAME, DESCRIPTION, OWNER_LOGIN};
    private static final int[] VIEWS = new int[]{R.id.repo_name, R.id.description, R.id.login};

    public CursorAdapter(Context context) {
        super(context.getApplicationContext(), R.layout.project_item, null, PROJECTION, VIEWS, 0);
    }

    @Override
    public void bindView(@NonNull View view, Context context, @NonNull Cursor cursor) {
        super.bindView(view, context, cursor);
        Project project = getProjectFromCursor(cursor);
        view.setBackgroundResource(project.isFork() ? R.color.light_green : android.R.color.white);
        view.setTag(project);
    }

    Project getProjectFromCursor(Cursor cursor) {
        int index = cursor.getColumnIndex(ID);
        int id = cursor.getInt(index);
        index = cursor.getColumnIndex(FORK);
        boolean fork = cursor.getInt(index) == 1;
        index = cursor.getColumnIndex(HTML_URL);
        String htmlUrl = cursor.getString(index);
        index = cursor.getColumnIndex(REPO_NAME);
        String repoName = cursor.getString(index);
        index = cursor.getColumnIndex(DESCRIPTION);
        String description = cursor.getString(index);
        index = cursor.getColumnIndex(OWNER_LOGIN);
        String login = cursor.getString(index);
        return new Project(id, repoName, description, htmlUrl, login, fork);
    }
}
