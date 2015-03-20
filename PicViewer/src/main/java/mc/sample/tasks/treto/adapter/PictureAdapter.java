package mc.sample.tasks.treto.adapter;

import android.content.Context;
import android.graphics.RectF;
import android.support.v4.view.PagerAdapter;
import android.view.*;
import android.widget.*;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import mc.sample.task.treto.R;
import mc.sample.tasks.treto.utils.Util;
import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;
import uk.co.senab.photoview.PhotoViewAttacher.OnPhotoTapListener;

import java.util.*;

public class PictureAdapter extends PagerAdapter {
    private static final int VIEW_POOL_SIZE = 3;

    private RectF mCurrRect;
    private final Context mContext;
    private final int[] mScreenSize;
    private final List<String> mImgUrls;
    private final LayoutInflater mInflater;
    private final Queue<FrameLayout> mReuseViewPool = new LinkedList<>();

    public PictureAdapter(Context context, List<String> imgUrls) {
        super();
        this.mImgUrls = imgUrls;
        this.mContext = context.getApplicationContext();
        this.mScreenSize = Util.getScreenSize(context);
        this.mInflater = LayoutInflater.from(context);
    }

    public boolean isSwipeEnabled() {
        if (mCurrRect == null) {
            return false;
        }

        if (Util.isPortrait(mContext)) {
            return Math.abs(mCurrRect.right) + Math.abs(mCurrRect.left) <= mScreenSize[0] + 1;// +1 - workaround for a size measure bug
        } else {
            return Math.abs(mCurrRect.bottom) + Math.abs(mCurrRect.top) <= mScreenSize[0] + 1;
        }
    }

    @Override
    public int getCount() {
        return mImgUrls.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ViewHolder holder;
        FrameLayout layout = mReuseViewPool.poll();
        if (layout != null) {
            holder = (ViewHolder) layout.getTag();
        } else {
            layout = (FrameLayout) mInflater.inflate(R.layout.item, container, false);
            ProgressBar pb = (ProgressBar) layout.findViewById(R.id.progress);
            PhotoView pv = (PhotoView) layout.findViewById(R.id.image);
            pv.setOnMatrixChangeListener(mMatrixChangedListener);
            pv.setOnPhotoTapListener(mPhotoTapListener);
            holder = new ViewHolder(pv, pb);
            layout.setTag(holder);
        }
        holder.photoView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        holder.progressBar.setVisibility(View.VISIBLE);
        final ProgressBar progressBar = holder.progressBar;
        Ion.with(mContext).load(mImgUrls.get(position)).progressBar(holder.progressBar).intoImageView(holder.photoView).setCallback(new FutureCallback<ImageView>() {
            @Override
            public void onCompleted(Exception e, ImageView result) {
                // Ion does not change visibility
                progressBar.setVisibility(View.GONE);
            }
        });
        container.addView(layout, 0);
        return layout;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        FrameLayout layout = (FrameLayout) object;
        container.removeView(layout);
        if (mReuseViewPool.size() <= VIEW_POOL_SIZE) {
            mReuseViewPool.add(layout);
        }
    }

    private final OnPhotoTapListener mPhotoTapListener = new OnPhotoTapListener() {
        @Override
        public void onPhotoTap(View v, float arg1, float arg2) {
            ((PhotoView) v).setScale(1);
        }
    };

    private final PhotoViewAttacher.OnMatrixChangedListener mMatrixChangedListener = new PhotoViewAttacher.OnMatrixChangedListener() {
        @Override
        public void onMatrixChanged(RectF rect) {
            mCurrRect = rect;
        }
    };
}
