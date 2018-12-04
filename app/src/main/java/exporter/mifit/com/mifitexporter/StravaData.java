package exporter.mifit.com.mifitexporter;

public class StravaData {

    protected String gpxPath, activityName;
    protected int type;
    protected long timestamp;

    public StravaData(String gpxPath, String activityName, int type, long timestamp) {
        this.gpxPath = gpxPath;
        this.activityName = activityName;
        this.type = type;
        this.timestamp = timestamp;
    }
}
