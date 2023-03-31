package net.r4geviperzz.questionmaster;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;

import androidx.appcompat.app.AlertDialog;
import androidx.documentfile.provider.DocumentFile;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import androidx.documentfile.provider.DocumentFile;

import java.io.File;

public class UserSelectDirectory {

    public static final int REQUEST_CODE_OPEN_DIRECTORY = 1;
    private Activity activity;
    public UserSelectDirectory(Activity activity) {
        this.activity = activity;
    }

    // This method starts an intent that opens the file explorer to allow the user to select a directory.
    public Boolean selectDirectory() {
        Boolean selectDirectorySupported = true;
        Intent intent;
        //Checks that the API is greater than 19 as below API 21 ACTION_OPEN_DOCUMENT_TREE is not supported
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // Use ACTION_OPEN_DOCUMENT_TREE for API 21 and above
            try {
                intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                activity.startActivityForResult(intent, REQUEST_CODE_OPEN_DIRECTORY);
            }catch (ActivityNotFoundException e){
                Log.e("fileExplorerIssue", "Error when trying to create a file explorer that can select a directory");
                Log.e("fileExplorerError", e.toString());
                selectDirectorySupported = false;
            }
        } else {
            //Returns false as on API 19 and lower the Android Storage Access Framework doesn't support selecting a directory
            selectDirectorySupported = false;
        }

        return selectDirectorySupported;
    }

    // This method gets the path of the directory that the user selected
    public DocumentFile getSelectedDirectory(Intent data) {
        // Get the URI of the selected directory from the intent data
        Uri uri = data.getData();
        DocumentFile documentFile = DocumentFile.fromTreeUri(activity, uri);
        return documentFile;
    }
}






