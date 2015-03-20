package mc.sample.tasks.xing;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import mc.sample.tasks.xing.model.Project;

import static mc.sample.tasks.xing.data.ProjectsDataLoader.GSON;

public class ProjectDetails extends Activity {
    public static final String PROJECT = "project";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_details);

        Intent in = getIntent();
        if (!in.hasExtra(PROJECT)) {
            throw new RuntimeException("No projects passed");
        }

        Project project = GSON.fromJson(in.getStringExtra(PROJECT), Project.class);
        TextView tv = (TextView) findViewById(R.id.id);
        tv.setText("" + project.getId());
        tv = (TextView) findViewById(R.id.name);
        tv.setText(project.getName());
        tv = (TextView) findViewById(R.id.login);
        tv.setText(project.getLogin());
        tv = (TextView) findViewById(R.id.url);
        tv.setText(project.getUrl());
        tv = (TextView) findViewById(R.id.fork);
        tv.setText(project.isFork() ? R.string.yes : R.string.no);
        tv = (TextView) findViewById(R.id.description);
        tv.setText(project.getDescription());
    }
}