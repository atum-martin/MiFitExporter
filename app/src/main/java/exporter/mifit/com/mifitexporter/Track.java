package exporter.mifit.com.mifitexporter;

import java.util.Date;

class Track {
    int type;
    int trackId;
    Date date;

    Track(int type, int trackId){
        this.type = type;
        this.trackId = trackId;
        long timestamp = ((long) trackId) * 1000L;
        this.date = new Date(timestamp);
    }

    @Override
    public String toString() {
        return getStringType(type)+" : "+date.toString();
    }

    public static String getStringType(int type) {
        switch(type){
            case 6:
                return "walk";
            case 7:
                return "hike";
            case 9:
                return "cycle";
            case 1:
            default:
                return "run";
        }
    }
}
