package exporter.mifit.com.mifitexporter;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

public class StravaUploader {

    public StravaUploader(){
        CookieHandler.setDefault( new CookieManager( null, CookiePolicy.ACCEPT_ALL ) );
        List<String> output = openUrl("https://www.strava.com/login");
        for(String s : output){
            System.out.println(s);
        }
    }

    public List<String> openUrl(String url){
        List<String> output = new LinkedList<>();
        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setRequestProperty("accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
            conn.setRequestProperty("accept-encoding","");
            conn.setRequestProperty("accept-language","en-US,en;q=0.8");
            conn.setRequestProperty("referer","https://www.strava.com/");
            conn.setRequestProperty("user-agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.96 Safari/537.36");

            String line;
            int returnCode = conn.getResponseCode();
            Log.i(Constants.STRAVA_TAG,"return code: "+returnCode);
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            while((line = br.readLine()) != null){
                Log.i(Constants.STRAVA_TAG,line);
                output.add(line);
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return output;
    }
}
