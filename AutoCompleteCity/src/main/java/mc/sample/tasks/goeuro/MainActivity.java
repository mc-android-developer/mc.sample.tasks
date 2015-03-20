package mc.sample.tasks.goeuro;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.*;
import android.widget.CalendarView.OnDateChangeListener;
import android.widget.TextView.OnEditorActionListener;
import mc.sample.tasks.goeuro.adapter.AutoCompleteAdapter;
import mc.sample.tasks.goeuro.calendar.CalendarDialog;
import mc.sample.tasks.goeuro.location.LocationHelper;
import mc.sample.tasks.goeuro.location.OnLocationChangeListener;
import org.androidannotations.annotations.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@EActivity(R.layout.activity_main)
public class MainActivity extends Activity {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    private static final String DIALOG_STATE = "dialog_state";

    @ViewById
    EditText date;
    @ViewById
    ImageView setDate;
    @ViewById
    TextView search;
    @ViewById
    AutoCompleteTextView fromPlace;
    @ViewById
    AutoCompleteTextView toPlace;

    private CalendarDialog mCalendarDialog;
    private LocationHelper mLocationHelper;
    private AutoCompleteAdapter mToPlaceAdapter;
    private AutoCompleteAdapter mFromPlaceAdapter;

    @AfterViews
    void afterViews() {
        mCalendarDialog = new CalendarDialog(this, mDateChangeListener);
        date.setText(DATE_FORMAT.format(mCalendarDialog.getDate()));

        mToPlaceAdapter = new AutoCompleteAdapter(this);
        toPlace.setAdapter(mToPlaceAdapter);
        mFromPlaceAdapter = new AutoCompleteAdapter(this);
        fromPlace.setAdapter(mFromPlaceAdapter);
        toPlace.addTextChangedListener(mTextChangedListener);
        toPlace.setOnEditorActionListener(mEditorActionListener);
        fromPlace.addTextChangedListener(mTextChangedListener);
        fromPlace.setOnEditorActionListener(mEditorActionListener);
        setDate.setOnClickListener(mSetDateClickListener);
        date.setOnClickListener(mSetDateClickListener);
        search.setOnClickListener(mSearchClickListener);

        mLocationHelper = new LocationHelper(this, true);
        mLocationHelper.setLocationChangeListener(mLocationChangeListener);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle state) {
        super.onSaveInstanceState(state);
        state.putBoolean(DIALOG_STATE, mCalendarDialog.isShowing());
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle state) {
        super.onRestoreInstanceState(state);
        if (state.getBoolean(DIALOG_STATE)) {
            mCalendarDialog.show();
        }
    }

    private boolean enableSearchIfRequired() {
        boolean enable = toPlace.getText().length() > 0 && fromPlace.getText().length() > 0 && date.getText().length() > 0;
        search.setBackgroundResource(enable ? R.drawable.sbutton : R.color.grey);
        search.setEnabled(enable);
        return enable;
    }

    private final OnLocationChangeListener mLocationChangeListener = new OnLocationChangeListener() {
        @Override
        public void onLocationChanged(Location location) {
            if (location != null) {
                mToPlaceAdapter.onNewLocation(location);
                mFromPlaceAdapter.onNewLocation(location);
                mLocationHelper.close();
            }
        }
    };

    private final OnEditorActionListener mEditorActionListener = new OnEditorActionListener() {
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (actionId == EditorInfo.IME_ACTION_SEARCH || event == null || event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                if (enableSearchIfRequired()) {
                    mSearchClickListener.onClick(v);
                }
                return true;
            }
            return false;
        }
    };

    private final TextWatcher mTextChangedListener = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            enableSearchIfRequired();
        }
    };

    private final OnClickListener mSetDateClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            mCalendarDialog.show();
        }
    };

    private final OnDateChangeListener mDateChangeListener = new OnDateChangeListener() {
        @Override
        public void onSelectedDayChange(CalendarView view, int year, int month, int day) {
            date.setText(DATE_FORMAT.format(new Date(view.getDate())));
            enableSearchIfRequired();
            mCalendarDialog.dismiss();
        }
    };

    private final OnClickListener mSearchClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            Builder ad = new AlertDialog.Builder(MainActivity.this);
            ad.setMessage(R.string.searchNotImplemented);
            ad.setNeutralButton(R.string.ok, null);
            ad.setTitle(R.string.appName);
            ad.show();
        }
    };
}