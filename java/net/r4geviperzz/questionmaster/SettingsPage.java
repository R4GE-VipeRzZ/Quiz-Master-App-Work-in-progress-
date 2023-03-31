package net.r4geviperzz.questionmaster;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.documentfile.provider.DocumentFile;

import java.io.File;

public class SettingsPage extends AppCompatActivity {
    private DBHelper dbHelper = new DBHelper(SettingsPage.this);
    private static final int STORAGE_PERMISSION_REQUEST_CODE = 1;
    private Boolean loadRequested;
    private Boolean grantedStoragePerms = false;
    private UserSelectDirectory userSelectDirectory;
    private UserSelectFile userSelectFile;
    private DocumentFile returnedBackupDir;
    private DocumentFile returnedBackupFile;

    private int permissionRequestCode = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_page);

        userSelectDirectory = new UserSelectDirectory(this);
        userSelectFile = new UserSelectFile(this);
        Button backupBtn = findViewById(R.id.settingsBackupBtn);

        backupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                permissionRequestCode = 1;
                grantedStoragePerms = StoragePermissions.checkStoragePermissions(SettingsPage.this, STORAGE_PERMISSION_REQUEST_CODE); // call the checkStoragePermissions method
                if (grantedStoragePerms == false) {
                    loadRequested = false;
                }else{
                    //callDBBackup();
                    Boolean apiSupportsDirectorySelector = userSelectDirectory.selectDirectory();

                    if (apiSupportsDirectorySelector == false){
                        Log.e("dirSelectNotSupported", "Directory selection isn't supported on this API");
                        File externalStorageDir = Environment.getExternalStorageDirectory();
                        // Save the selected directory for use
                        returnedBackupDir = DocumentFile.fromFile(externalStorageDir);
                        // Calls the method that will backup the database to the selected directory
                        callDBBackup();
                    }
                }
            }
        });

        Button loadBackupBtn = findViewById(R.id.settingsLoadBackupBtn);

        loadBackupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                permissionRequestCode = 2;
                grantedStoragePerms = StoragePermissions.checkStoragePermissions(SettingsPage.this, STORAGE_PERMISSION_REQUEST_CODE); // call the checkStoragePermissions method
                if (grantedStoragePerms == false){
                    loadRequested = true;
                }else{
                    Boolean apiSupportsFileSelector = userSelectFile.selectFile();

                    if (apiSupportsFileSelector == false){
                        Log.e("fileSelectNotSupported", "File selection isn't supported on this API");

                        String backupFileName = "question_master_backup.db.gz";
                        // Retrieve the root DocumentFile for the external storage directory
                        DocumentFile externalStorageRoot = DocumentFile.fromFile(Environment.getExternalStorageDirectory());

                        // Find the "question_master_backup.db.gz" file in the external storage directory
                        DocumentFile backupFile = externalStorageRoot.findFile(backupFileName);

                        if (backupFile != null) {
                            // Save the selected file for use
                            returnedBackupFile = backupFile;
                        } else {
                            // File not found, display an error message
                            TextView errorLabel = findViewById(R.id.settingsPgErrorLabel);
                            errorLabel.setText("No file named " + backupFileName + "found in " + externalStorageRoot);
                        }

                        callDBLoad();
                    }
                }
            }
        });

        Button loadDefaultBtn = findViewById(R.id.settingsLoadDefaultBtn);

        loadDefaultBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Creates an alert message to ensure the a user doesn't accidentally reset the database to default
                AlertDialog.Builder builder = new AlertDialog.Builder(SettingsPage.this);

                builder.setMessage("Are you sure you want to load the default database? All of the questions will be set back to there default values," +
                        "and any new questions that have been added will be removed.");
                builder.setTitle("Restore Defaults?");

                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        TextView errorLabel = findViewById(R.id.settingsPgErrorLabel);
                        errorLabel.setText("");

                        dbHelper.loadDBFile(SettingsPage.this, returnedBackupFile, true);
                    }
                });

                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Do nothing
                    }
                });

                // Create and show the AlertDialog
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    private void callDBLoad(){
        TextView errorLabel = findViewById(R.id.settingsPgErrorLabel);

        // This if statement checks that the selected file exists
        if (returnedBackupFile.exists()) {
            //This runs if the file exists

            String mimeType ="";
            if (android.os.Build.VERSION.SDK_INT < 29){
                // Runs if the API level is less than API 29
                // This if checks if the file ends with the file extension .gz as .getType doesn't work
                // correctly on .gz files on API 28 and lower
                if (returnedBackupFile.getName().endsWith(".gz")){
                    mimeType = "application/gzip";
                }
            }else if (Build.VERSION.SDK_INT > 28) {
                // Runs if the API level is greater than API 28
                mimeType = returnedBackupFile.getType();
            }

            // This if statement checks that the file has a file type and is a gzip file
            if (mimeType != null && mimeType.equals("application/gzip")) {
                errorLabel.setText("");

                dbHelper.loadDBFile(SettingsPage.this,  returnedBackupFile, false);
            }else{
                errorLabel.setText("Invalid file type, the file needs to be a GZIP file with the extension .gz");
            }
        }else{
            errorLabel.setText("Does you backup file exists? No file named " + returnedBackupFile.getName() + " can be found");
        }
    }

    private void callDBBackup(){
        TextView errorLabel = findViewById(R.id.settingsPgErrorLabel);
        errorLabel.setText("");

        dbHelper.backupDBFile(SettingsPage.this,  returnedBackupDir);
    }

    // Add the onRequestPermissionsResult method to handle permission requests
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == STORAGE_PERMISSION_REQUEST_CODE) {
            TextView errorLabel = findViewById(R.id.settingsPgErrorLabel);

            //Checks if the user has approved storage permissions
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
                errorLabel.setText("");

                if (loadRequested == true){
                    //Runs if the permissions have been requested from a db load request
                    callDBLoad();
                }else{
                    //Runs if the permissions have been requested from a db backup request
                    userSelectDirectory.selectDirectory();
                }
            } else {
                // Permission denied
                Log.e("StoragePermission", "Storage permission not granted");
                errorLabel.setText("Storage permissions are required, please grant them");
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Check if the result is from the user selecting a directory and if the result is OK
        if (requestCode == UserSelectDirectory.REQUEST_CODE_OPEN_DIRECTORY && resultCode == RESULT_OK) {
            // Get the selected directory as a DocumentFile object using the getSelectedDirectory method of the UserSelectDirectory class
            DocumentFile selectedDirectory = new UserSelectDirectory(this).getSelectedDirectory(data);

            // Save the selected directory for use
            returnedBackupDir = selectedDirectory;
            // Calls the method that will backup the database to the selected directory
            callDBBackup();
        }else if (requestCode == UserSelectFile.REQUEST_CODE_OPEN_FILE && resultCode == RESULT_OK) {
            DocumentFile selectedFile = new UserSelectFile(this).getSelectedFile(data);

            //Log.e("selectedFile", selectedFile.toString());
            returnedBackupFile = selectedFile;

            callDBLoad();
        }
    }

}
