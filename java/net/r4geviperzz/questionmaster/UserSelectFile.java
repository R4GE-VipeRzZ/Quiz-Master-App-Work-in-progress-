package net.r4geviperzz.questionmaster;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import androidx.documentfile.provider.DocumentFile;

public class UserSelectFile {

    public static final int REQUEST_CODE_OPEN_FILE = 2;
    private Activity activity;

    public UserSelectFile(Activity activity) {
        this.activity = activity;
    }

    // This method starts an intent that opens the file explorer to allow the user to select a file.
    public Boolean selectFile() {
        Boolean selectFileSupported = true;
        Intent intent = null;
        //Checks that the API is greater than 19 as below API 21 ACTION_OPEN_DOCUMENT is not supported
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // Use ACTION_OPEN_DOCUMENT for API 21 and above
            try {
                // Create an Intent to open the system file explorer
                intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                // Set the type of files to display in the file explorer to all types
                if (Build.VERSION.SDK_INT > 28) {
                    // Runs if the API level is greater than API 28
                    //Limits the file types that are displayed in the file explorer to gzip files
                    intent.setType("application/gzip");
                }else{
                    //Allows all file types to be displayed in the file explorer as on API 28 and lower
                    //the file explorer is not able to get the file type of .gz files
                    intent.setType("*/*");
                }
                //intent.setType("application/gzip");
                // Add a category to the intent indicating that it should only display files that can be opened
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                // Start the activity for selecting a file and pass in a request code to identify the result
                activity.startActivityForResult(intent, REQUEST_CODE_OPEN_FILE);
            } catch (ActivityNotFoundException e) {
                // If the system file explorer cannot be opened, log an error and set a flag to indicate that file selection is not supported
                Log.e("fileExplorerIssue", "Error when trying to create a file explorer that can select a file");
                Log.e("fileExplorerError", e.toString());
                selectFileSupported = false;
            }
        } else {
            //Returns false as on API 19 and lower the Android Storage Access Framework doesn't support selecting a file
            selectFileSupported = false;
        }

        return selectFileSupported;
    }

    // This method gets the path of the file that the user selected
    public DocumentFile getSelectedFile(Intent data) {
        // Get the URI of the selected file from the intent data
        Uri uri = data.getData();
        DocumentFile documentFile = DocumentFile.fromSingleUri(activity, uri);
        return documentFile;
    }
}

