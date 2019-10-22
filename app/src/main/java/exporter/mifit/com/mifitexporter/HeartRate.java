package exporter.mifit.com.mifitexporter;

public class HeartRate {

    private int hr;
    private long timestamp;

    public HeartRate(int hr, long timestamp){
        this.hr = hr;
        this.timestamp = timestamp;
    }

    public int getHR(){
        return hr;
    }

    public long getTimestamp(){
        return timestamp;
    }
}
