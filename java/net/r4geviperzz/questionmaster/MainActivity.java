package net.r4geviperzz.questionmaster;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.documentfile.provider.DocumentFile;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;


public class MainActivity extends AppCompatActivity{
    //private DBHelper dbHelper = new DBHelper(MainActivity.this);
    private DBHelper dbHelper;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Checks that the device size values have been set
        if (DeviceSize.getDeviceWidthInch() == 0.0){
            DeviceSize.setDeviceSizeValues(MainActivity.this);
        }

        //Checks that the fontAdjustValue has been set in the TextScale class
        if (TextScale.getFontAdjustWidthValue() == 0.0){
            TextScale.setFontAdjustValue(MainActivity.this);
        }

        dbHelper = new DBHelper(this);

        if (dbHelper.checkDBExists(MainActivity.this)){
            dbHelper.loadDBFile(MainActivity.this, null, true);
        }

        //Calls the method that will setup the buttons and onClickListeners for the menu buttons
        setupBtns();
    }

    //This method is responsible for setting up the buttons and onClickListeners for the menu buttons
    private void setupBtns(){
        Float heightAdjustValue = TextScale.getFontAdjustHeightValue();
        int padding = (int) ((14 * heightAdjustValue) * DeviceSize.getDeviceDensity());

        Button playBtn = (Button) findViewById(R.id.menuPlayBtn);
        playBtn.setPadding(padding, padding, padding, padding);

        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SetupPage.class);
                startActivity(intent);
            }
        });

        Button questionsBtn =(Button) findViewById(R.id.menuQuestionsBtn);
        questionsBtn.setPadding(padding, padding, padding, padding);

        questionsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, QuestionsPage.class);
                startActivity(intent);
            }
        });

        Button settingsBtn = (Button) findViewById(R.id.menuSettingsBtn);
        settingsBtn.setPadding(padding, padding, padding, padding);

        settingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SettingsPage.class);
                startActivity(intent);
            }
        });

        Button exitBtn = (Button) findViewById(R.id.menuExitBtn);
        exitBtn.setPadding(padding, padding, padding, padding);

        exitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Exit
            }
        });
    }
}