package mc.sample.tasks.fortegroup.model;

import android.util.Log;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class FlickrItem implements Serializable {
    private static final String LOGTAG = FlickrItem.class.getSimpleName();
    private static final long serialVersionUID = 1L;

    private static final SimpleDateFormat INPUT_DATE = new SimpleDateFormat("yyyy-MM-dd'T'kk:mm:ssZ", Locale.getDefault());
    private static final SimpleDateFormat OUTPUT_DATE = new SimpleDateFormat("dd/MM/yyyy kk:mm:ss", Locale.getDefault());

    private String mDescription;
    private String mImageUrl;
    private Date mPublished;
    private String mLink;

    public String getDescription() {
        return mDescription;
    }

    public String getLink() {
        return mLink;
    }

    public String getPublished() {
        return OUTPUT_DATE.format(mPublished);
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    public void setDescription(String description) {
        this.mDescription = description.trim();
    }

    public void setLink(String link) {
        this.mLink = link;
    }

    public void setPublished(String published) {
        try {
            published = published.replace("Z", "+00:00");
            this.mPublished = INPUT_DATE.parse(published);
        } catch (ParseException e) {
            Log.e(LOGTAG, "Cannot parse date string " + published);
        }
    }

    public void setImageUrl(String imageUrl) {
        this.mImageUrl = imageUrl;
    }

    @Override
    public String toString() {
        return "FlickrItem{" +
                "mDescription='" + mDescription + '\'' +
                ", mImageUrl='" + mImageUrl + '\'' +
                ", mPublished=" + mPublished +
                ", mLink='" + mLink + '\'' +
                '}';
    }
}
