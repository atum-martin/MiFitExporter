package exporter.mifit.com.mifitexporter;

import android.app.Activity;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import java.io.File;

import eu.chainfire.libsuperuser.Shell.SU;

public class RootTools {

    private void requestPermission(Activity activity) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, "android.permission.WRITE_EXTERNAL_STORAGE")) {
            ActivityCompat.requestPermissions(activity, new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, 1);
        }
    }

    private boolean checkPermission(Activity activity) {
        if (ContextCompat.checkSelfPermission(activity, "android.permission.WRITE_EXTERNAL_STORAGE") == 0) {
            return true;
        }
        return false;
    }

    public void copyMiFitDB(Activity activity, String folder, String pathTo){
        if (Build.VERSION.SDK_INT >= 23 && !checkPermission(activity)) {
            requestPermission(activity);
        }
        File dir = new File(folder);
        if(!dir.exists()){
            dir.mkdirs();
        }
        if(SU.available()) {
            SU.run("cp " + ((String) SU.run("ls /data/data/com.xiaomi.hm.health/databases/origin_db_* | grep -v journal").get(0)) + " " + pathTo);
        }
    }

}
