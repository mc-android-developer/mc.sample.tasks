package mc.sample.tasks.goeuro.location;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import java.io.Closeable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class LocationHelper implements Closeable
{
    private static final String LOGTAG = LocationHelper.class.getSimpleName();
    private static final float LOCATION_UPDATE_DISTANCE = 500;
    private static final long LOCATION_UPDATE_TIME = 5000;

    private final Map<String, LocationListenerImpl> mListeners = new HashMap<>();
    private final ReentrantReadWriteLock mLock = new ReentrantReadWriteLock();
    private OnLocationChangeListener mLocationChangeListener;
    private final LocationManager mLocationManager;
    private List<String> mProviders;
    private int mLastAccuracy = -1;
    private Location mLocation;
    private final boolean mFreeSourcesOnly;
    private boolean mEnabled;

    public LocationHelper(Context context, boolean freeSourcesOnly)
    {
        super();
        mFreeSourcesOnly = freeSourcesOnly;
        mLocationManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);

        Criteria c = new Criteria();
        c.setCostAllowed(!freeSourcesOnly);
        mProviders = mLocationManager.getProviders(c, false);
        Log.i(LOGTAG, "Location providers set " + mProviders);
        createListeners();
        setupListeners();

        for(String p : mProviders)
        {
            Location location = mLocationManager.getLastKnownLocation(p);
            if(location != null)
            {
                int accuracy = mLocationManager.getProvider(p).getAccuracy();
                if(isAccuracyBetterOrSame(accuracy))
                {
                    Log.i(LOGTAG, "New location set " + location);
                    mLocation = location;
                }
            }
        }
    }

    public boolean isLocationServicesEnabled()
    {
        if(!mEnabled)
        {
            return false;
        }

        Criteria c = new Criteria();
        c.setCostAllowed(!mFreeSourcesOnly);
        mProviders = mLocationManager.getProviders(c, false);
        if(mProviders != null)
        {
            for(String p : mProviders)
            {
                boolean enabled = mLocationManager.isProviderEnabled(p);
                Log.e("PROVIDER", p + "  " + enabled);
                if(enabled)
                    return true;
            }
        }
        return false;
    }

    public void setLocationServicesEnabled(boolean enable)
    {
        if(enable)
        {
            setupListeners();
        }
        else
        {
            removeListeners();
        }
    }

    public OnLocationChangeListener getLocationChangeListener()
    {
        return mLocationChangeListener;
    }

    public void setLocationChangeListener(OnLocationChangeListener mLocationChangeListener)
    {
        mLocationChangeListener = mLocationChangeListener;
        callLocationListener();
    }

    private void callLocationListener()
    {
        if(mLocation != null && mLocationChangeListener != null)
        {
            mLocationChangeListener.onLocationChanged(mLocation);
        }
    }

    private void setupListeners()
    {
        if(!mEnabled)
        {
            for(LocationListenerImpl listener : mListeners.values())
            {
                mLocationManager.requestLocationUpdates(listener.mLocationProviderName, LOCATION_UPDATE_TIME, LOCATION_UPDATE_DISTANCE, listener);
            }
            mEnabled = true;
        }
    }

    private void createListeners()
    {
        for(String provider : mProviders)
        {
            mListeners.put(provider, new LocationListenerImpl(provider));
        }
    }

    private void removeListeners()
    {
        if(mEnabled)
        {
            for(LocationListenerImpl listener : mListeners.values())
            {
                mLocationManager.removeUpdates(listener);
            }
            mEnabled = false;
        }
    }

    @Override
    public void close()
    {
        mLocationChangeListener = null;
        removeListeners();
    }

    public Location getLocation()
    {
        mLock.readLock().lock();
        try
        {
            return mLocation;
        }
        finally
        {
            mLock.readLock().unlock();
        }
    }

    private void setLocation(String locationProviderName, Location location)
    {
        if(location != null)
        {
            int accuracy = mLocationManager.getProvider(locationProviderName).getAccuracy();
            if(isAccuracyBetterOrSame(accuracy))
            {
                mLock.writeLock().lock();
                try
                {
                    Log.i(LOGTAG, "New mLocation set " + location);
                    mLocation = location;
                }
                finally
                {
                    mLock.writeLock().unlock();
                }
                callLocationListener();
            }
        }
    }

    private boolean isAccuracyBetterOrSame(int newAccuracy)
    {
        if(mLastAccuracy == newAccuracy)
        {
            return true;
        }
        if(mLastAccuracy == -1 && (newAccuracy == Criteria.ACCURACY_COARSE || newAccuracy == Criteria.ACCURACY_FINE))
        {
            Log.i(LOGTAG, "New accuracy set:" + newAccuracy);
            mLastAccuracy = newAccuracy;
            return true;
        }
        if(mLastAccuracy == Criteria.ACCURACY_COARSE && newAccuracy == Criteria.ACCURACY_FINE)
        {
            Log.i(LOGTAG, "New accuracy set:" + newAccuracy);
            mLastAccuracy = newAccuracy;
            return true;
        }
        return false;
    }

    private class LocationListenerImpl implements LocationListener
    {
        private final String mLocationProviderName;

        public LocationListenerImpl(String locationProviderName)
        {
            super();
            mLocationProviderName = locationProviderName;
        }

        @Override
        public void onLocationChanged(Location location)
        {
            setLocation(mLocationProviderName, location);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras)
        {
            Log.i(LOGTAG, "Provider " + provider + " status changed to " + status);
        }

        @Override
        public void onProviderEnabled(String provider)
        {
            Log.i(LOGTAG, "Provider " + provider + " enabled");
        }

        @Override
        public void onProviderDisabled(String provider)
        {
            Log.i(LOGTAG, "Provider " + provider + " disabled");

        }
    }
}
