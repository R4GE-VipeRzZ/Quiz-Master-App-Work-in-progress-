package net.r4geviperzz.questionmaster;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;

public class StoragePermissions {
    //This method is used that the device has external storage permissions
    public static Boolean checkStoragePermissions(Activity activity, int requestCode){
        Boolean permissionsGranted = true;

        // Check if the current API level 30 or higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // Check if the app has been granted the MANAGE_EXTERNAL_STORAGE permission
            if (Environment.isExternalStorageManager()) {
                // Storage permissions have been granted
            } else {
                permissionsGranted = false;
                // Storage permissions haven't been granted so need requesting
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
                intent.setData(uri);
                activity.startActivityForResult(intent, requestCode);
            }
        }
        // Check if the current API level 23 or higher
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Check if the app has been granted the WRITE_EXTERNAL_STORAGE permission
            if (activity.checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                // Storage permissions have been granted
            } else {
                // Storage permissions have not been granted so need requesting
                permissionsGranted = false;
                activity.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, requestCode);
            }
        } else {
            // Run if running on API level 23 or lower
            // Permissions are not needed as permission are automatically granted on API levels less than 23
        }

        return permissionsGranted;
    }
}
