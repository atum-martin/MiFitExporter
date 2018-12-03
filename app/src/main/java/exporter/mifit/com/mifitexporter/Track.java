package exporter.mifit.com.mifitexporter;

import java.util.Date;

class Track {
    int type;
    int trackId;
    Date date;

    public Track(int type, int trackId){
        this.type = type;
        this.trackId = trackId;
        long timestamp = ((long) trackId) * 1000L;
        this.date = new Date(timestamp);
    }
}
