package exporter.mifit.com.mifitexporter;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class GpxWriter {

    private final SimpleDateFormat dateFormat;
    private final SimpleDateFormat timeFormat;

    GpxWriter(Locale locale){
        dateFormat = new SimpleDateFormat("yyyy-MM-dd", locale);
        timeFormat = new SimpleDateFormat("HH:mm:ss", locale);
    }

    public String writeGpxFile(String writePath, List<Date> timestamps, List<GpsLocation> locationList, List<Integer> heartRates){
        String fileName;
        try {
            Date initalTimestamp = timestamps.get(0);
            String timeStr = dateFormat.format(initalTimestamp)+"T"+timeFormat.format(initalTimestamp);
            fileName = writePath +"/" + initalTimestamp.toString() + ".gpx";
            PrintWriter printWriter = new PrintWriter(fileName, "UTF-8");
            printWriter.println("<?xml version='1.0' encoding='UTF-8'?>");
            printWriter.println("<gpx version='1.1' creator='Amazfit_export by dvd_ath' xsi:schemaLocation='http://www.topografix.com/GPX/1/1");
            printWriter.println("                                 http://www.topografix.com/GPX/1/1/gpx.xsd");
            printWriter.println("                                 http://www.garmin.com/xmlschemas/GpxExtensions/v3");
            printWriter.println("                                 http://www.garmin.com/xmlschemas/GpxExtensionsv3.xsd");
            printWriter.println("                                 http://www.garmin.com/xmlschemas/TrackPointExtension/v1");
            printWriter.println("                                 http://www.garmin.com/xmlschemas/TrackPointExtensionv1.xsd' xmlns='http://www.topografix.com/GPX/1/1' xmlns:gpxtpx='http://www.garmin.com/xmlschemas/TrackPointExtension/v1' xmlns:gpxx='http://www.garmin.com/xmlschemas/GpxExtensions/v3' xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'>");
            printWriter.println(" <metadata>");
            printWriter.println("  <link href='https://github.com/botmakerdvd/amazfit_exporter'>");
            printWriter.println("    <text>Amazfit exporter</text>");
            printWriter.println("  </link>");
            printWriter.println("  <link href='https://github.com/atum-martin/MiFitExporter'>");
            printWriter.println("    <text>MiFit exporter</text>");
            printWriter.println("  </link>");
            printWriter.println("  <time>" + timeStr + ".000Z</time>");
            printWriter.println(" </metadata>");
            printWriter.println(" <trk>");
            printWriter.println("  <trkseg>");
            for (int i = 0; i < timestamps.size(); i++) {
                Date dataPointTimestamp = timestamps.get(i);
                GpsLocation location = locationList.get(i);
                timeStr = dateFormat.format(dataPointTimestamp)+"T"+timeFormat.format(dataPointTimestamp);

                printWriter.println("   <trkpt lon='" + location.getLongitude() + "' lat='" + location.getLatitude() + "'>");
                if (((double) location.getAltitude()) != -200000.0d) {
                    printWriter.println("    <ele>" + (location.getAltitude() / 10.0f) + "</ele>");
                }
                printWriter.println("    <time>" + timeStr + ".000Z</time>");
                printWriter.println("    <extensions>");
                printWriter.println("     <gpxtpx:TrackPointExtension>");
                if (i < heartRates.size()) {
                    printWriter.println("      <gpxtpx:hr>" + heartRates.get(i) + "</gpxtpx:hr>");
                }
                printWriter.println("     </gpxtpx:TrackPointExtension>");
                printWriter.println("    </extensions>");
                printWriter.println("   </trkpt>");
            }
            printWriter.println("  </trkseg>");
            printWriter.println(" </trk>");
            printWriter.println("</gpx>");
            printWriter.close();
            return fileName;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
