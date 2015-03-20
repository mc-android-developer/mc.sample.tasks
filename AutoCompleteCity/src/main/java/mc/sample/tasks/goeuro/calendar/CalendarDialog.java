package mc.sample.tasks.goeuro.calendar;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.widget.CalendarView;
import android.widget.CalendarView.OnDateChangeListener;

import java.util.Date;

import mc.sample.tasks.goeuro.R;

public class CalendarDialog extends Dialog {
    private long mCurrentDate;

    public CalendarDialog(Context context, final OnDateChangeListener listener) {
        super(context, R.style.CalendarDialog);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        setContentView(inflater.inflate(R.layout.calendar, null));

        CalendarView cv = (CalendarView) findViewById(R.id.calendar);
        mCurrentDate = cv.getDate();
        cv.setOnDateChangeListener(new OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                if (view.getDate() != mCurrentDate)// workaround for calendar widget bug
                {
                    mCurrentDate = view.getDate();
                    CalendarDialog.this.hide();
                    if (listener != null) {
                        listener.onSelectedDayChange(view, year, month, dayOfMonth);
                    }
                }
            }
        });
    }

    public Date getDate() {
        return new Date(mCurrentDate);
    }
}
