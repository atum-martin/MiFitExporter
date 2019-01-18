package exporter.mifit.com.mifitexporter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

public class WeatherApi {

    private static final String APIKEY = "c53b1d4229bccdc8";
    private static final String API_ENDPOINT = "http://api.wunderground.com/api";

    private static String getInputForUrl(String url) throws IOException {
        HttpURLConnection conn = (HttpURLConnection)(new URL(url)).openConnection();
        InputStream io = conn.getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(io));
        StringBuilder output = new StringBuilder();
        String line;
        while((line = br.readLine()) != null){
            output.append(line);
        }
        br.close();
        return output.toString();
    }

    public static double getAirTempForTimestamp(long timestamp, double lat, double lon){
        ArrayList<WeatherData> dataPoints = getWeatherForLoc(timestamp, lat, lon);
        WeatherData bestPoint = null;
        long timeDelta = Long.MAX_VALUE;
        for(int i = 0; i < dataPoints.size(); i++){
            long delta = timestamp - dataPoints.get(i).unixtime;
            if(delta < timeDelta){
                timeDelta = delta;
                bestPoint = dataPoints.get(i);
            }
        }
        System.out.println("air temp for pos: "+lat+":"+lon+" C: "+bestPoint.airTemp+" delta: "+timeDelta+" unixtime:"+bestPoint.unixtime+" len: "+dataPoints.size());
        return bestPoint.airTemp;
    }

    private static ArrayList<WeatherData> getWeatherForLoc(long timestamp, double lat, double lon){
        ArrayList<WeatherData> cachedDataPoints = null;
        if((cachedDataPoints = checkCache(timestamp,lat,lon)) != null){
            System.out.println("cache hit for: "+timestamp+" "+lat+" "+lon);
            return cachedDataPoints;
        }
        String url = API_ENDPOINT+"/"+APIKEY+"/history_"+timestampToDate(timestamp)+"/q/"+lat+","+lon+".json";
        try {
            String json = getInputForUrl(url);
            JSONObject obj = new JSONObject(json).getJSONObject("history");
            JSONArray arr = obj.getJSONArray("observations");

            ArrayList<WeatherData> dataPoints = new ArrayList(arr.length());
            for(int i = 0; i < arr.length(); i++) {
                JSONObject sample1 = arr.getJSONObject(i);
                long unixtime = parseDate(sample1.getJSONObject("utcdate"));
                double airTemp = sample1.getDouble("tempm");
                double windChill = sample1.getDouble("windchillm");
                if(windChill == -999)
                    windChill = 0;
                dataPoints.add(new WeatherData(unixtime, airTemp+windChill));
            }
            putCache(timestamp,lat,lon, dataPoints);
            return dataPoints;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String timestampToDate(long timestamp) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        System.out.println("date format: "+formatter.format(new Date(timestamp)));
        return formatter.format(new Date(timestamp));

    }

    private static long parseDate(JSONObject utcdate) throws JSONException {
        int year = utcdate.getInt("year");
        int month = utcdate.getInt("mon")-1;//GregorianCalendar starts month field at 0 not 1.
        int day = utcdate.getInt("mday");
        int hour = utcdate.getInt("hour");
        int min = utcdate.getInt("min");
        GregorianCalendar calendar = new GregorianCalendar(year, month, day, hour, min);
        return calendar.getTimeInMillis();
    }

    private static Map<Long, ArrayList<WeatherData>> weatherCache = new HashMap<>();

    private static void putCache(long timestamp, double lat, double lon, ArrayList<WeatherData> dataPoints) {
        long posHash = calculatePositionHash(timestamp, lat, lon);
        weatherCache.put(posHash, dataPoints);
    }

    private static ArrayList<WeatherData> checkCache(long timestamp, double lat, double lon) {
        long posHash = calculatePositionHash(timestamp, lat, lon);
        return weatherCache.get(posHash);
    }

    private static long midnightTimestamp(long timestamp) {
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTimeInMillis(timestamp);
        GregorianCalendar simplifiedCalendar = new GregorianCalendar(
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        return simplifiedCalendar.getTimeInMillis();
    }

    private static long calculatePositionHash(long timestamp, double lat, double lon){
        timestamp = midnightTimestamp(timestamp);
        //todo implement hash that includes lat & lon for 20km accuracy.
        return timestamp;
    }

    static class WeatherData {
        private long unixtime;
        private double airTemp;

        public WeatherData(long unixtime, double airTemp){
            this.unixtime = unixtime;
            this.airTemp = airTemp;
        }
    }
}
