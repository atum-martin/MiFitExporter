package exporter.mifit.com.mifitexporter;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MiFitDbUtils {

    private SQLiteDatabase connection;

    MiFitDbUtils(String databaseFile){
        connection = SQLiteDatabase.openOrCreateDatabase(databaseFile, null);
    }

    public List<Track> getTracksByRecent(){
        ArrayList<Track> output = new ArrayList<>();
        Cursor resultSet = connection.rawQuery("select * from TRACKDATA order by TRACKID DESC",null);
        while(resultSet.moveToNext()){
            int trackId = resultSet.getInt(resultSet.getColumnIndex("TRACKID"));
            int type = resultSet.getInt(resultSet.getColumnIndex("TYPE"));
            output.add(new Track(type, trackId));
        }
        resultSet.close();
        return output;
    }

    public List<GpsLocation> getGpsLocataionsForTrack(int trackId){
        ArrayList<GpsLocation> output = new ArrayList<>();
        Cursor resultSet = connection.rawQuery("select BULKLL, BULKAL from TRACKDATA where TRACKID = " +trackId,null);
        while(resultSet.moveToNext()){
            String[] positions = resultSet.getString(resultSet.getColumnIndex("BULKLL")).split(";");
            String[] altitudesStr = resultSet.getString(resultSet.getColumnIndex("BULKAL")).split(";");
            float[] altitudes = new float[altitudesStr.length];
            for(int i = 0; i < altitudes.length; i++){
                altitudes[i] = Float.parseFloat(altitudesStr[i]) / 10.0f;
            }
            float latitude = 0f;
            float longitude = 0f;
            for(int i = 0; i < positions.length; i++){
                String[] position = positions[i].split(",");
                latitude += (Float.parseFloat(position[0]) / 100000000);
                longitude += (Float.parseFloat(position[1]) / 100000000);
                output.add(new GpsLocation(latitude, longitude, altitudes[i]));
            }
            Log.i(Constants.DB_TAG,"Gps locations added: "+positions.length);

        }
        resultSet.close();
        return output;
    }

    public List<HeartRate> getHeartRateForTrack(int trackId){
        ArrayList<HeartRate> output = new ArrayList<>();
        Cursor resultSet = connection.rawQuery("select BULKHR from TRACKDATA where TRACKID = " +trackId,null);
        int heartRate = 0;
        long timestamp = ((long) trackId) * 1000L;
        while(resultSet.moveToNext()){
            String[] heartRateStr = resultSet.getString(resultSet.getColumnIndex("BULKHR")).split(";");
            for(String heartRateS : heartRateStr){
                String[] heartRatePointStr = heartRateS.split(",");
                System.out.println("hr str: "+heartRateS);
                if(heartRatePointStr.length > 1) {

                    heartRate += Integer.parseInt(heartRatePointStr[1]);
                    timestamp += (Long.parseLong(heartRatePointStr[0]) * 1000L);
                    HeartRate hr = new HeartRate(heartRate, timestamp);
                    output.add(hr);
                }
            }
        }
        resultSet.close();
        return output;
    }

    public List<Date> getTimestampsForTrack(int trackId){
        ArrayList<Date> output = new ArrayList<>();
        Cursor resultSet = connection.rawQuery("select BULKTIME from TRACKDATA where TRACKID = " +trackId,null);
        long timestamp = ((long) trackId) * 1000L;
        while(resultSet.moveToNext()){
            String[] timestampsStr = resultSet.getString(resultSet.getColumnIndex("BULKTIME")).split(";");
            for(String time : timestampsStr){
                timestamp += (Long.parseLong(time) * 1000L);
                output.add(new Date(timestamp));
            }
        }
        resultSet.close();
        return output;
    }
}
