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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

public class WeatherApi {

    private static final String APIKEY = "ZGVlNTE1M2QtZDU2MC00MWEzLWI1MTUtNzNhYzAyZWU2ZjNiOkNRbkh1TGdPZjQ=";
    private static final String API_ENDPOINT = "https://twcse"+"rvice.eu-gb.myblue"+"mix.net/api/"+"weather/v1";

    private static String getInputForUrl(String url) throws IOException {
        HttpURLConnection conn = (HttpURLConnection)(new URL(url)).openConnection();
        conn.setRequestProperty(("Autho"+"rization"),("Bas"+"ic "+APIKEY));
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
        if(bestPoint == null)
            return Integer.MIN_VALUE;
        System.out.println("air temp for pos: "+lat+":"+lon+" C: "+bestPoint.airTemp+" delta: "+timeDelta+" unixtime:"+bestPoint.unixtime+" len: "+dataPoints.size());
        return bestPoint.airTemp;
    }

    private static ArrayList<WeatherData> getWeatherForLoc(long timestamp, double lat, double lon){
        ArrayList<WeatherData> cachedDataPoints = null;
        if((cachedDataPoints = checkCache(timestamp,lat,lon)) != null){
            System.out.println("cache hit for: "+timestamp+" "+lat+" "+lon);
            return cachedDataPoints;
        }
        String url = API_ENDPOINT+"/geocode/"+lat+"/"+lon+"/observations/timeseries.json?hours=23&units=m";
        try {
            String json = getInputForUrl(url);
            JSONArray arr = new JSONObject(json).getJSONArray("observations");

            ArrayList<WeatherData> dataPoints = new ArrayList(arr.length());
            for(int i = 0; i < arr.length(); i++) {
                JSONObject sample1 = arr.getJSONObject(i);
                long unixtime = (sample1.getLong("valid_time_gmt")*1000);
                double temp = sample1.getDouble("temp");
                //double windChill = sample1.getDouble("wc");
                //double feels_like = sample1.getDouble("feels_like");
                dataPoints.add(new WeatherData(unixtime, temp));
            }
            putCache(timestamp,lat,lon, dataPoints);
            return dataPoints;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new ArrayList<WeatherData>();
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
        System.out.println("rounded lat: "+lat+"/"+floor(lat, 1)+" lon: "+lon+"/"+floor(lon, 1));
        //round position to the nearest 11.132km*2 (due to floor func)
        long latRound = (long)(floor(lat, 1) * 10);
        long lonRound = (long)(floor(lon, 1) * 10);
        return timestamp+latRound+lonRound;
    }

    private static double floor (double value, int precision) {
        int scale = (int) Math.pow(10, precision);
        return (double) Math.floor(value * scale) / scale;
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
