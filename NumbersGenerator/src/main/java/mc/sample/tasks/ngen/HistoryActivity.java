package mc.sample.tasks.ngen;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class HistoryActivity extends ActionBarActivity {
    private static final String SEPARATOR = "   ";
    private TextView mHistoryText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        TextView actionBarTitle = (TextView) findViewById(R.id.action_bar_title);
        actionBarTitle.setText(R.string.title_history);

        mHistoryText = (TextView) findViewById(R.id.history);
        Button buttonClear = (Button) findViewById(R.id.button_clear);
        buttonClear.setOnClickListener(mClearClickListener);

        printHistory();
    }

    private void printHistory() {
        StringBuilder builder = new StringBuilder();
        for (Integer i : HistoryKeeper.iterator()) {
            builder.append(i).append(SEPARATOR);
        }
        mHistoryText.setText(builder.toString());
    }

    private final OnClickListener mClearClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            HistoryKeeper.clear();
            printHistory();
        }
    };
}