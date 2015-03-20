package mc.sample.tasks.mobtech.data;

import android.util.Log;
import mc.sample.tasks.mobtech.data.model.Category;
import mc.sample.tasks.mobtech.data.model.CategoryList;
import org.json.*;

import java.util.Arrays;

public class CategoryParser {
    private static final String LOGTAG = CategoryParser.class.getSimpleName();

    private static final String TAG_DATA = "data";
    private static final String TAG_NAME = "name";
    private static final String TAG_RESULT = "result";
    private static final String TAG_CHILDREN = "Children";

    private static final String CORRECT_RESULT_RESPONSE = "ok";

    private static final CategoryComparator sCategoryComparator = new CategoryComparator();

    public static CategoryList[] parse(String json) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            String result = jsonObject.getString(TAG_RESULT);
            if (!CORRECT_RESULT_RESPONSE.equals(result)) {
                Log.e(LOGTAG, "Incorrect result response: " + result);
                return null;
            }
            jsonObject = jsonObject.getJSONObject(TAG_DATA);
            return parseCategoryList(jsonObject);
        } catch (JSONException e) {
            Log.e(LOGTAG, "Can not parse json data", e);
        }
        return null;
    }

    private static CategoryList[] parseCategoryList(JSONObject categoryList) {
        try {
            int len = categoryList.length();
            CategoryList[] res = new CategoryList[len];
            JSONArray names = categoryList.names();
            for (int i = 0; i < len; i++) {
                String name = names.getString(i);
                JSONObject category = categoryList.getJSONObject(name);
                res[i] = parseCategoryList(category, name);
            }
            return res;
        } catch (JSONException e) {
            Log.e(LOGTAG, "Can not parse json data", e);
        }
        return null;
    }

    private static CategoryList parseCategoryList(JSONObject categoryList, String id) {
        try {
            String name = categoryList.getString(TAG_NAME);
            Category[] categories;
            if (categoryList.has(TAG_CHILDREN)) {
                JSONObject children = categoryList.getJSONObject(TAG_CHILDREN);
                categories = parseCategories(children);
            } else {
                categories = new CategoryList[0];
            }
            Arrays.sort(categories, sCategoryComparator);
            return new CategoryList(id, name, categories);
        } catch (JSONException e) {
            Log.e(LOGTAG, "Can not parse json data", e);
        }
        return null;
    }

    private static Category[] parseCategories(JSONObject events) {
        try {
            int len = events.length();
            Category[] res = new Category[len];
            JSONArray names = events.names();
            for (int i = 0; i < len; i++) {
                String id = names.getString(i);
                String name = events.getJSONObject(id).getString(TAG_NAME);
                res[i] = new Category(id, name);
            }
            return res;
        } catch (JSONException e) {
            Log.e(LOGTAG, "Can not parse json data", e);
        }
        return null;
    }
}
