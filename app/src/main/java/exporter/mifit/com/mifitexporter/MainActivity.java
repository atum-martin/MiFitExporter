package exporter.mifit.com.mifitexporter;

import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.sweetzpot.stravazpot.authenticaton.api.AccessScope;
import com.sweetzpot.stravazpot.authenticaton.api.ApprovalPrompt;
import com.sweetzpot.stravazpot.authenticaton.api.StravaLogin;
import com.sweetzpot.stravazpot.authenticaton.ui.StravaLoginActivity;

import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String dbFolder = Environment.getExternalStorageDirectory().getPath() + "/MiFitExporter";
    private static final String dbPath = dbFolder+"/mifitdb.db";
    private static final int RQ_LOGIN = 500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        (new RootTools()).copyMiFitDB(this, dbFolder, dbPath);
        //MiFitDbUtils db = new MiFitDbUtils(dbPath);
        //R.layout.activity_main;
        //test();
        /*new Thread(){
            public void run(){
                new StravaUploader();
            }
        }.start();*/
        Intent intent = StravaLogin.withContext(this)
                .withClientID(30629)
                  .withRedirectURI("atum-martin.com")
                  .withApprovalPrompt(ApprovalPrompt.AUTO)
                .withAccessScope(AccessScope.VIEW_PRIVATE_WRITE)
                .makeIntent();
        startActivityForResult(intent, RQ_LOGIN);

        //setContentView(R.layout.activity_main);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == RQ_LOGIN && resultCode == RESULT_OK && data != null) {
            String code = data.getStringExtra(StravaLoginActivity.RESULT_CODE);
            // Use code to obtain token
            System.out.println("strava code: "+code);
        }
    }

    private void test(){
        MiFitDbUtils db = new MiFitDbUtils(dbPath);
        Track t = db.getTracksByRecent().get(0);
        List<GpsLocation> locations = db.getGpsLocataionsForTrack(t.trackId);
        List<Integer> heartrates = db.getHeartRateForTrack(t.trackId);
        List<Date> dates = db.getTimestampsForTrack(t.trackId);

        String outputFile = GpxWriter.writeGpxFile(dbFolder, dates, locations, heartrates);


    }
}
