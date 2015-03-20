package mc.sample.tasks.goeuro.model;

public class City {
    private String name;
    private GeoPosition geo_position;

    public String getName() {
        return name;
    }

    public double getLatitude() {
        if (geo_position != null) {
            return geo_position.getLatitude();
        }
        return 0;
    }

    public double getLongitude() {
        if (geo_position != null) {
            return geo_position.getLongitude();
        }
        return 0;
    }
}
