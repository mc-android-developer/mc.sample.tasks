package mc.sample.tasks.goeuro.adapter;

import android.location.Location;

import java.util.Comparator;

import mc.sample.tasks.goeuro.model.City;

class CityLocationComparator implements Comparator<City> {
    private static final double EARTH_RADIUS = 6371; //km
    private final double mLongitude;
    private final double mLatitude;

    public CityLocationComparator(Location location) {
        mLatitude = location.getLatitude();
        mLongitude = location.getLongitude();
        if (mLatitude == 0 || mLongitude == 0) {
            throw new IllegalArgumentException("Longitude or Latitude is 0");
        }
    }

    @Override
    public int compare(City a, City b) {
        if (hasNoLocation(a) || hasNoLocation(b)) {
            return 0;
        }
        return (int) (getDistanceToCity(a) - getDistanceToCity(b));
    }

    private double getDistanceToCity(City city) {
        double dLat = Math.toRadians(mLatitude - city.getLatitude());
        double dLng = Math.toRadians(mLongitude - city.getLongitude());
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(city.getLatitude())) * Math.cos(Math.toRadians(mLatitude)) *
                        Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS * c;
    }

    private boolean hasNoLocation(City city) {
        return city.getLatitude() == 0 && city.getLongitude() == 0;
    }
}