package mc.sample.tasks.fortegroup.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.Html;
import android.view.*;
import android.widget.*;
import mc.sample.tasks.fortegroup.R;
import mc.sample.tasks.fortegroup.cache.BitmapCache;
import mc.sample.tasks.fortegroup.cache.IBitmapCache;
import mc.sample.tasks.fortegroup.model.FlickrItem;
import mc.sample.tasks.fortegroup.tasks.LoadBitmapTask;
import mc.sample.tasks.fortegroup.taskservice.TaskService;

public class FlickrItemAdapter extends BaseAdapter {
    private final LayoutInflater mInflater;
    private final IBitmapCache mBitmapCache;
    private final FlickrItem[] mEntries;

    public FlickrItemAdapter(Context context, FlickrItem[] entries) {
        super();
        mEntries = entries;
        mBitmapCache = new BitmapCache(context);
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView text;
        ImageView image;
        FlickrItem item = getItem(position);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.flickr_item, parent, false);
            text = (TextView) convertView.findViewById(R.id.textDescription);
            image = (ImageView) convertView.findViewById(R.id.image);
            convertView.setTag(R.id.holder_tag_key, new ViewHolder(text, image));
        } else {
            ViewHolder holder = (ViewHolder) convertView.getTag(R.id.holder_tag_key);
            text = holder.textView;
            image = holder.imageView;
        }

        text.setText(Html.fromHtml(item.getDescription()));
        text = (TextView) convertView.findViewById(R.id.textDate);
        text.setText(item.getPublished());
        setImage(image, item.getImageUrl());

        convertView.setTag(item);
        return convertView;
    }

    private void setImage(ImageView image, String url) {
        Bitmap bitmap = mBitmapCache.get(url);
        if (bitmap == null) {
            image.setImageResource(R.drawable.ic_launcher);
            TaskService.execute(image.getContext(), new LoadBitmapTask(url, image, mBitmapCache));
        } else {
            image.setImageBitmap(bitmap);
        }
    }

    @Override
    public int getCount() {
        if (mEntries == null) {
            return 0;
        }

        return mEntries.length;
    }

    @Override
    public FlickrItem getItem(int position) {
        if (position >= getCount() || position < 0) {
            return null;
        }

        return mEntries[position];
    }

    @Override
    public long getItemId(int position) {
        if (position >= getCount() || position < 0) {
            return -1;
        }

        return position;
    }
}
