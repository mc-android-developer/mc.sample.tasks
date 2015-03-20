package mc.sample.tasks.treto.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
<<<<<<< HEAD

=======
>>>>>>> prepare
import mc.sample.tasks.treto.adapter.PictureAdapter;

public class SwipeControlViewPager extends ViewPager {
    private static final String LOGTAG = SwipeControlViewPager.class.getSimpleName();

    public SwipeControlViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return isSwipeEnabled() && super.onTouchEvent(event);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        try {
            if (isSwipeEnabled()) {
                return super.onInterceptTouchEvent(event);
            }
        } catch (IllegalArgumentException e) {
            Log.e(LOGTAG, "onInterceptTouchEvent:" + e);
        }
        return false;
    }

    boolean isSwipeEnabled() {
        PictureAdapter pa = (PictureAdapter)getAdapter();
        return pa != null && pa.isSwipeEnabled();
    }
}