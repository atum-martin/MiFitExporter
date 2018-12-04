package exporter.mifit.com.mifitexporter;

import android.util.Log;

import com.sweetzpot.stravazpot.authenticaton.api.AuthenticationAPI;
import com.sweetzpot.stravazpot.authenticaton.model.AppCredentials;
import com.sweetzpot.stravazpot.authenticaton.model.LoginResult;
import com.sweetzpot.stravazpot.common.api.AuthenticationConfig;
import com.sweetzpot.stravazpot.common.api.StravaConfig;
import com.sweetzpot.stravazpot.upload.api.UploadAPI;
import com.sweetzpot.stravazpot.upload.model.DataType;
import com.sweetzpot.stravazpot.upload.model.UploadActivityType;

import java.io.File;

public class StravaUploader {

    public StravaUploader(String code, StravaData data, String secretKey){
        Log.i(Constants.STRAVA_TAG,"code "+code+" gpx: "+data.gpxPath+" type: "+data.type);
        UploadActivityType activityType = getType(data.type);
        AuthenticationConfig config = AuthenticationConfig.create()
                .debug()
                .build();
        AuthenticationAPI api = new AuthenticationAPI(config);
        LoginResult result = api.getTokenForApp(AppCredentials.with(30629, secretKey))
                .withCode(code)
                .execute();

        StravaConfig stravaConfig = StravaConfig.withToken(result.getToken())
                .debug()
                .build();

        UploadAPI uploadAPI = new UploadAPI(stravaConfig);
        uploadAPI.uploadFile(new File(data.gpxPath))
                .withDataType(DataType.GPX)
                .withActivityType(activityType)
                .withName(data.activityName)
                .withDescription("No description")
                .isPrivate(false)
                .hasTrainer(false)
                .isCommute(false)
                .withExternalID(data.timestamp+".gpx")
                .execute();
        Log.i(Constants.STRAVA_TAG,"code "+code+" gpx: "+data.gpxPath+" type: "+data.type);
    }

    private UploadActivityType getType(int type) {
        switch(type){
            case 1:
                return UploadActivityType.RUN;
            case 6:
                return UploadActivityType.WALK;
            case 7:
                return UploadActivityType.HIKE;
            case 9:
                return UploadActivityType.RIDE;
                default:
                    return UploadActivityType.RUN;
        }
    }
}
