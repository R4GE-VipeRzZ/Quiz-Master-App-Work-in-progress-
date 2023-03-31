package net.r4geviperzz.questionmaster;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import androidx.documentfile.provider.DocumentFile;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.zip.GZIPInputStream;

//This class is responsible for loading the backup file in the background, decompressing the file and updating the SQLite database
public class LoadDB extends AsyncTask<Void, Void, Boolean> {
    private Context passedContext;
    private DocumentFile backupFile;
    private Boolean fromResources;
    private SQLiteDatabase db;
    private List<String> gameSessionDataList;

    public LoadDB(Context context, SQLiteDatabase passedDB, DocumentFile passedBackupFile, Boolean loadFromResource, List<String> gameSessionData) {
        this.passedContext = context;
        this.db = passedDB;
        this.backupFile = passedBackupFile;
        this.fromResources = loadFromResource;
        this.gameSessionDataList = gameSessionData;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        try {
            InputStream inputStream = null;
            Boolean fileValid = true;

            if (this.fromResources == false) {
                if (this.backupFile != null) {
                    // Open the backup file from the passed DocumentFile and create input stream for reading the database file
                    inputStream = new GZIPInputStream(this.passedContext.getContentResolver().openInputStream(this.backupFile.getUri()));
                }else{
                    Log.e("nullBackupFile", "No valid backup file has been selected");
                    fileValid = false;
                }
            } else {
                int resourceId = this.passedContext.getResources().getIdentifier("question_master_backup", "raw", this.passedContext.getPackageName());
                InputStream standardInputStream = this.passedContext.getResources().openRawResource(resourceId);
                inputStream = new GZIPInputStream(standardInputStream);
            }

            if (fileValid == true) {
                // Create the output stream for writing to the database
                OutputStream outputStream = new FileOutputStream(db.getPath());
                // Create a buffer to store the data being transferred
                byte[] buffer = new byte[1024];
                int length;
                // Read data from the backup file and write it to the database file
                while ((length = inputStream.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, length);
                }
                // Close the input, output streams and db connection
                inputStream.close();
                outputStream.flush();
                outputStream.close();
                // Return a Boolean indicating success
                return true;
            }else{
                return false;
            }
        } catch (Exception e) {
            // If an exception occurs, log an error and return a Boolean indicating failure
            Log.e("dbRestoreFailed", "Error restoring database");
            Log.e("dbRestoreError", e.toString());
            return false;
        }
    }

    // This method is called when the doInBackground method finishes
    @Override
    protected void onPostExecute(Boolean success) {
        if (success) {
            // If the restore was successful, show a toast message indicating success
            Toast.makeText(passedContext, "Database load completed", Toast.LENGTH_SHORT).show();

            DBHelper dbHelper= new DBHelper(passedContext);

            //Checks that the list that contains the gameSession tables original data isn't empty
            if (gameSessionDataList != null) {
                //Calls the method that will write the data in the list to the gameSession table
                //this needs to be done so that the values in the gameSessions table aren't changed when
                //a db file is loaded
                dbHelper.setGameSessionData(gameSessionDataList);
            }
        } else {
            // If the restore failed, show a toast message indicating failure
            Toast.makeText(passedContext, "Database load failed", Toast.LENGTH_SHORT).show();
        }
    }
}



