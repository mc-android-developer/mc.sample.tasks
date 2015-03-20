package mc.sample.tasks.ngen.view;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import mc.sample.tasks.ngen.R;

@SuppressWarnings("UnusedParameters,EmptyMethod,unused")
public class NumberInput extends LinearLayout {

    public static interface ChangeListener {
        void numberIncremented(int number);

        void numberDecremented(int number);
    }

    private TextView mNumberText;

    private ChangeListener mChangeListener;
    private int mStep = 1;
    private int mNumber;

    public NumberInput(Context context) {
        super(context);
        createView(context);
    }

    public NumberInput(Context context, AttributeSet attrs) {
        super(context, attrs);
        createView(context);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public NumberInput(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        createView(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public NumberInput(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        createView(context);
    }

    public ChangeListener getOnChangeListener() {
        return mChangeListener;
    }

    public void setOnChangeListener(ChangeListener listener) {
        if (listener != null && !listener.equals(mChangeListener)) {
            mChangeListener = listener;
        }
    }

    @SuppressLint("InflateParams")
    private void createView(Context context) {
        View v = LayoutInflater.from(context).inflate(R.layout.number_input, null);
        addView(v);

        mNumberText = (TextView) findViewById(R.id.number_input_number);
        mNumberText.setText(String.valueOf(mNumber));
        findViewById(R.id.number_input_up).setOnClickListener(mButtonUpClickListener);
        findViewById(R.id.number_input_down).setOnClickListener(mButtonDownClickListener);
    }

    public int getNumber() {
        return mNumber;
    }

    public void setNumber(int number) {
        mNumber = number;
        mNumberText.setText(String.valueOf(number));
    }

    public int getStep() {
        return mStep;
    }

    public void setStep(int step) {
        mStep = step;
    }

    private OnClickListener mButtonUpClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            mNumber += mStep;
            mNumberText.setText(String.valueOf(mNumber));
            if (mChangeListener != null) {
                mChangeListener.numberIncremented(mNumber);
            }
        }
    };

    private OnClickListener mButtonDownClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            mNumber -= mStep;
            mNumberText.setText(String.valueOf(mNumber));
            if (mChangeListener != null) {
                mChangeListener.numberDecremented(mNumber);
            }
        }
    };
}
