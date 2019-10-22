package exporter.mifit.com.mifitexporter;

import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import com.sweetzpot.stravazpot.authenticaton.api.AccessScope;
import com.sweetzpot.stravazpot.authenticaton.api.ApprovalPrompt;
import com.sweetzpot.stravazpot.authenticaton.api.StravaLogin;
import com.sweetzpot.stravazpot.authenticaton.ui.StravaLoginActivity;

import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class MainActivity extends AppCompatActivity {

    private static final String dbFolder = Environment.getExternalStorageDirectory().getPath() + "/MiFitExporter";
    private static final String dbPath = dbFolder+"/mifitdb.db";
    private static final int RQ_LOGIN = 1001;
    private StravaData stravaData = null;
    private MiFitDbUtils db;
    private AtomicBoolean stravaLock = new AtomicBoolean(false);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        (new RootTools()).copyMiFitDB(this, dbFolder, dbPath);

        setContentView(R.layout.activity_main);
        initDB();
    }

    private void stravaUploader(String gpxPath, int type, String activityName, long timestamp){
        Intent intent = StravaLogin.withContext(this)
                .withClientID(30629)
                .withRedirectURI("http://atum-martin.github.io/token_exchange")
                .withApprovalPrompt(ApprovalPrompt.AUTO)
                .withAccessScope(AccessScope.VIEW_PRIVATE_WRITE)
                .makeIntent();
        stravaData = new StravaData(gpxPath, activityName, type, timestamp);
        startActivityForResult(intent, RQ_LOGIN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == RQ_LOGIN && resultCode == RESULT_OK && data != null) {
            final String code = data.getStringExtra(StravaLoginActivity.RESULT_CODE);
            System.out.println("strava code: "+code);

            new Thread(){
                public void run(){
                    //ensure lock is released.
                    try {
                        new StravaUploader(code, stravaData, getString(R.string.client_secret));
                    } catch(Exception e){
                        e.printStackTrace();
                    }
                    stravaLock.set(false);
                }
            }.start();
        }
    }

    private void initDB(){
        db = new MiFitDbUtils(dbPath);
        List<Track> tracks = db.getTracksByRecent();
        ((ListView) this.findViewById(R.id.list_view)).setAdapter(new TrackListAdpater(this, tracks));
    }

    public void dumpTrackToStrava(int trackId, int type){
        if(stravaLock.getAndSet(true)){
            //lock in use;
            return;
        }
        try {
            List<GpsLocation> locations = db.getGpsLocataionsForTrack(trackId);
            List<HeartRate> heartrates = db.getHeartRateForTrack(trackId);
            List<Date> dates = db.getTimestampsForTrack(trackId);
            GpxWriter writer = new GpxWriter(getResources().getConfiguration().locale);
            String outputFile = writer.writeGpxFile(dbFolder, dates, locations, heartrates);

            stravaUploader(outputFile, type, generateActivityName(trackId, type), (trackId * 1000L));
        } catch(Exception e){
            e.printStackTrace();
            stravaLock.set(false);
            throw e;
        }
    }

    private String generateActivityName(long timestamp, int type){
        String time = getTimeOfDay((timestamp * 1000L));
        String activityType = Track.getStringType(type);
        return time+" "+activityType+".";
    }

    private String getTimeOfDay(long l) {
        Date d = new Date(l);
        int hours = d.getHours();
        if(hours >= 3 && hours <= 12){
            return "Morning";
        } else if(hours >= 13 && hours <= 17){
            return "Afternoon";
        } else if(hours >= 18 && hours <= 21){
            return "Evening";
        } else {
            return "Night";
        }
    }
}
