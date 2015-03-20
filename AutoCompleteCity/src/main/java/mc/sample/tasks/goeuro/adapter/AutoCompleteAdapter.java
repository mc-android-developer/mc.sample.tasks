package mc.sample.tasks.goeuro.adapter;

import android.content.Context;
import android.location.Location;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import java.util.Arrays;

import mc.sample.tasks.goeuro.R;
import mc.sample.tasks.goeuro.model.City;

public class AutoCompleteAdapter extends BaseAdapter implements Filterable {
    private static final String LOGTAG = AutoCompleteAdapter.class.getSimpleName();

    private final LayoutInflater mInflater;
    private final City[] mDefaultPrompts = new City[0];
    private CityLocationComparator mComparator;
    private final RequestQueue mRequestQueue;
    private final Gson mGson = new Gson();
    private final Context mContext;
    private City[] mPrompts;

    public AutoCompleteAdapter(Context context) {
        super();
        mContext = context;
        mPrompts = mDefaultPrompts;
        mInflater = LayoutInflater.from(context);
        mRequestQueue = Volley.newRequestQueue(context);
    }

    public void onNewLocation(Location location) {
        if(location.getLatitude() == 0 || location.getLongitude() == 0){
            return;
        }
        mComparator = new CityLocationComparator(location);
    }

    @Override
    public int getCount() {
        return mPrompts.length;
    }

    @Override
    public String getItem(int position) {
        return mPrompts[position].getName();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView tv;
        if (convertView == null) {
            tv = (TextView) mInflater.inflate(R.layout.city_item, parent, false);
        } else {
            tv = (TextView) convertView;
        }
        tv.setText(getItem(position));
        return tv;
    }

    @Override
    public Filter getFilter() {
        return mFilter;
    }

    private final Filter mFilter = new Filter() {
        final FilterResults filterResults = new FilterResults();
        CharSequence lastKey;

        @Override
        protected FilterResults performFiltering(CharSequence key) {
            if (key == null || key.length() < 2) {
                filterResults.count = mDefaultPrompts.length;
                filterResults.values = mDefaultPrompts;
            } else if (!key.equals(lastKey)) {
                try {
                    String json = fetchCitySuggestions(key);
                    City[] cities = mGson.fromJson(json, City[].class);
                    if (mComparator != null) {
                        Arrays.sort(cities, mComparator);
                    }
                    filterResults.count = cities.length;
                    filterResults.values = cities;
                    lastKey = key;
                    Log.i(LOGTAG, "Auto complete data set updated. New size:" + filterResults.count);
                } catch (Exception e) {
                    Log.e(LOGTAG, e.getMessage(), e);
                    filterResults.count = mDefaultPrompts.length;
                    filterResults.values = mDefaultPrompts;
                }
            }
            return filterResults;
        }

        private String fetchCitySuggestions(CharSequence key) throws Exception {
            RequestFuture<String> request = RequestFuture.newFuture();
            String url = mContext.getString(R.string.api_url) + key;
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url, request, request);
            mRequestQueue.add(stringRequest);
            return request.get();
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            mPrompts = (City[]) results.values;
            notifyDataSetChanged();
        }
    };
}
