package dts.display;

public class Location {

    private double lat;
    private double lng;


    public Location() {
    }

    /**
     * init location object for the use of digital item boundary
     *
     * @param lat - latitude
     * @param lng - longitude
     */
    public Location(double lat, double lng) {
        this.lat = lat;
        this.lng = lng;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    @Override
    public String toString() {
        return "location{" +
                "lat=" + lat +
                ", lng=" + lng +
                '}';
    }
}
