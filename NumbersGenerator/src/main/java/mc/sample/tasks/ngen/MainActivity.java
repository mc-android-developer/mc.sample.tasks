package mc.sample.tasks.ngen;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import mc.sample.tasks.ngen.util.NumberToWordsEng;
import mc.sample.tasks.ngen.view.NumberInput;

import java.util.Random;

public class MainActivity extends ActionBarActivity {
    private static final int TOP_LIMIT = 1000000000;
    private static final int BOTTOM_LIMIT = 10;

    private static final String STATE_NUMBER = "state_number";

    private TextView mText;
    private TextView mNumber;
    private NumberInput mGenLimitInput;

    private final Random mRandom = new Random(System.currentTimeMillis());
    private int mGenLimit = 10000;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_main);

        mNumber = (TextView) findViewById(R.id.number);
        mText = (TextView) findViewById(R.id.number_text);
        Button buttonNext = (Button) findViewById(R.id.button_next);
        buttonNext.setOnClickListener(mNextClickListener);
        Button buttonHistory = (Button) findViewById(R.id.button_history);
        buttonHistory.setOnClickListener(mHistoryClickListener);
        mGenLimitInput = (NumberInput) findViewById(R.id.gen_limit);
        mGenLimitInput.setNumber(mGenLimit);
        mGenLimitInput.setOnChangeListener(mLimitChangeListener);

        if (state != null) {
            int number = state.getInt(STATE_NUMBER, -1);
            if (number != -1) {
                setNumber(number);
                return;
            }
        }

        setNumber();
    }

    private void setNumber() {
        setNumber(mRandom.nextInt(mGenLimit));
    }

    private void setNumber(int number) {
        HistoryKeeper.add(number);
        mNumber.setText("" + number);
        mText.setText(NumberToWordsEng.convert(number));
    }

    @Override
    protected void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        try {
            state.putInt(STATE_NUMBER, Integer.parseInt(mNumber.getText().toString()));
        } catch (NumberFormatException ignored) {
            // It's ok
        }
    }

    private final NumberInput.ChangeListener mLimitChangeListener = new NumberInput.ChangeListener() {
        @Override
        public void numberIncremented(int number) {
            if (mGenLimit < TOP_LIMIT) {
                mGenLimit *= 10;
            }
            mGenLimitInput.setNumber(mGenLimit);
        }

        @Override
        public void numberDecremented(int number) {
            if (mGenLimit > BOTTOM_LIMIT) {
                mGenLimit /= 10;
            }
            mGenLimitInput.setNumber(mGenLimit);
        }
    };

    private final OnClickListener mNextClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            setNumber();
        }
    };

    private final OnClickListener mHistoryClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            startActivity(new Intent(MainActivity.this, HistoryActivity.class));
        }
    };
}