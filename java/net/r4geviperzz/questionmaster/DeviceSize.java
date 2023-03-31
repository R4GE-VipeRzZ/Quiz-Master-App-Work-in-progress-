package net.r4geviperzz.questionmaster;

import android.app.Activity;
import android.util.DisplayMetrics;
import android.util.Log;

//This class is used to get the physical size of the device
public class DeviceSize {
    private static float deviceWidthInch = 0.0f;
    private static int deviceWidthPX = 0;
    private static float deviceHeightInch = 0.0f;
    private static int deviceHeightPX = 0;
    private static float density = 0.0f;

    //Gets the width and height of the device in inches
    public static void setDeviceSizeValues(Activity passedActivity){
        // Get the screen dimensions
        DisplayMetrics displayMetrics = new DisplayMetrics();
        passedActivity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        density = displayMetrics.density;
        //Gets the physical pixels per inch in the x / width dimension
        float widthDPI = displayMetrics.xdpi;
        //Gets the physical pixels per inch in the y / height dimension
        float heightDPI = displayMetrics.ydpi;

        //Gets the absolute width of the display in pixels
        deviceWidthPX = displayMetrics.widthPixels;
        //Calculates the width of the device in inches
        deviceWidthInch = deviceWidthPX / widthDPI;

        //Gets the absolute height of the display in pixels
        deviceHeightPX = displayMetrics.heightPixels;
        //Calculates the height of the device in inches
        deviceHeightInch = deviceHeightPX / heightDPI;
    }

    //Gets the device width value
    public static float getDeviceWidthInch(){
        //Checks that the value is greater than zero as if it isn't then the device size hasn't been calculated
        if (deviceWidthInch < 0.0){
            //Runs if the value is 0 or negative
            Log.e("deviceWidthError", "The device width value is 0 inches");
        }
        return deviceWidthInch;
    }

    //Gets the device DP width value
    public static int getDeviceWidthPX(){
        //Checks that the value is greater than zero as if it isn't then the device size hasn't been calculated
        if (deviceWidthPX < 0.0){
            //Runs if the value is 0 or negative
            Log.e("devicePXWidthError", "The device PX width value is 0px");
        }
        return deviceWidthPX;
    }

    //Gets the device height value
    public static float getDeviceHeightInch(){
        //Checks that the value is greater than zero as if it isn't then the device size hasn't been calculated
        if (deviceHeightInch < 0.0){
            //Runs if the value is 0 or negative
            Log.e("deviceHeightError", "The device height value is 0 inches");
        }
        return deviceHeightInch;
    }

    //Gets the device height PX value
    public static int getDeviceHeightPX(){
        //Checks that the value is greater than zero as if it isn't then the device size hasn't been calculated
        if (deviceHeightPX < 0.0){
            //Runs if the value is 0 or negative
            Log.e("devicePXHeightError", "The device PX height value is 0px");
        }
        return deviceHeightPX;
    }

    public static float getDeviceDensity(){
        //Checks that the value is greater than zero as if it isn't then the device size hasn't been calculated
        if (density < 0.0){
            //Runs if the value is 0 or negative
            Log.e("deviceDensityError", "The device density value is 0");
        }
        return density;
    }
}
