package net.r4geviperzz.questionmaster;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import androidx.documentfile.provider.DocumentFile;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.zip.GZIPOutputStream;

//This class is responsible for backing up the database in the background when called
public class BackupDB extends AsyncTask<Void, Void, Boolean> {
    private Context passedContext;
    private SQLiteDatabase db;
    private DocumentFile backupDir;

    public BackupDB(Context context, SQLiteDatabase passedDB, DocumentFile passedBackupDir){
        this.passedContext = context;
        this.db = passedDB;
        this.backupDir = passedBackupDir;
    }

    // This method is executed when the execute() method is called on the class
    @Override
    protected Boolean doInBackground(Void... params) {
        try {
            String backupFileName = "question_master_backup.db.gz";
            // Get the SAF document file from the passed directory path
            DocumentFile directory = backupDir;
            // Get the backup file in the backup directory
            DocumentFile backupFile = directory.findFile(backupFileName);
            // If the backup file already exists, delete it
            if (backupFile != null) {
                backupFile.delete();
            }
            // Create a backup file in the backup directory
            backupFile = directory.createFile("application/gzip", backupFileName);

            // Create input stream for reading the database file
            FileInputStream inputStream = new FileInputStream(db.getPath());
            // Create an output stream to write the backup file to the specified Uri
            OutputStream outputStream = passedContext.getContentResolver().openOutputStream(backupFile.getUri());
            // Create a buffered output stream to improve performance of writing to the backup file
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream);
            // Create a GZIP output stream to compress the data being written to the backup file
            GZIPOutputStream gzipOutputStream = new GZIPOutputStream(bufferedOutputStream);
            // Create a buffer to store the data being transferred
            byte[] buffer = new byte[1024];
            int length;
            // Read data from the database file and write it to the backup file
            while ((length = inputStream.read(buffer)) > 0) {
                gzipOutputStream.write(buffer, 0, length);
            }
            // Close the input and output streams
            inputStream.close();
            gzipOutputStream.flush();
            gzipOutputStream.close();
            // Return a Boolean indicating success
            return true;
        } catch (Exception e) {
            // If an exception occurs, log an error and return a Boolean indicating failure
            Log.e("dbBackupFailed", "Error backing up database");
            Log.e("dbBackupError", e.toString());
            return false;
        }
    }

    // This method is called when the doInBackground method finishes
    @Override
    protected void onPostExecute(Boolean success) {
        if (success) {
            String backupDirString = backupDir.getUri().toString();
            String dirLocationForUser = backupDirString.replace("/tree/", "");
            // If the backup was successful, show a toast message indicating success
            Toast.makeText(passedContext, "Database backup completed, file located at " + dirLocationForUser, Toast.LENGTH_LONG).show();
        } else {
            // If the backup failed, show a toast message indicating failure
            Toast.makeText(passedContext, "Database backup failed", Toast.LENGTH_LONG).show();
        }
    }
}
