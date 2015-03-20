package mc.sample.tasks.fortegroup.parser;

import android.text.TextUtils;
import android.util.Log;
import mc.sample.tasks.fortegroup.model.FlickrItem;
import org.json.*;


public class Parser {
    private static final String LOGTAG = Parser.class.getSimpleName();
    private static final String TRASH = "jsonFlickrFeed(";

    public static FlickrItem[] parse(String json) {
        try {
            if (json.startsWith(TRASH)) {
                json = json.substring(TRASH.length(), json.length() - 1);
            }

            JSONArray items = new JSONObject(json).getJSONArray("items");
            FlickrItem[] res = new FlickrItem[items.length()];
            for (int i = 0; i < items.length(); i++) {
                FlickrItem item = parseItem(items.getJSONObject(i));
                if (item != null) {
                    res[i] = item;
                }
            }
            return res;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static FlickrItem parseItem(JSONObject item) {
        try {
            FlickrItem flickrItem = new FlickrItem();
            flickrItem.setLink(item.getString("link"));
            flickrItem.setImageUrl(item.getJSONObject("media").getString("m"));
            flickrItem.setDescription(item.getString("description"));
            flickrItem.setPublished(item.getString("published"));
            if (validate(flickrItem)) {
                return flickrItem;
            }
        } catch (JSONException e) {
            Log.e(LOGTAG, "Json parsing error:" + item, e);
        }
        return null;
    }

    private static boolean validate(FlickrItem entry) {
        if (entry == null) {
            Log.e(LOGTAG, "Entry is null");
            return false;
        } else if (TextUtils.isEmpty(entry.getLink())) {
            Log.e(LOGTAG, "No link " + entry);
            return false;
        } else if (TextUtils.isEmpty(entry.getImageUrl())) {
            Log.e(LOGTAG, "No image url " + entry);
            return false;
        } else if (TextUtils.isEmpty(entry.getDescription())) {
            Log.e(LOGTAG, "No description " + entry);
            return false;
        } else if (entry.getPublished() == null) {
            Log.e(LOGTAG, "No publication date " + entry);
            return false;
        }
        return true;
    }
}
