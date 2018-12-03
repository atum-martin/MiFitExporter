package exporter.mifit.com.mifitexporter;

public class GpsLocation {
    float latitude;
    float longitude;
    float altitude;


    public GpsLocation(float latitude, float longitude, float altitude) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;
    }

    public float getAltitude() {
        return altitude;
    }

    public float getLatitude() {
        return latitude;
    }

    public float getLongitude() {
        return longitude;
    }
}
