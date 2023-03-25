package net.r4geviperzz.questionmaster;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;

import java.lang.reflect.AccessibleObject;

//This class is used to calculate a value for adjusting the font size of text depending on the screen physical width
public class TextScale {
    private static float fontAdjustValue = 0.0f;

    public static void setFontAdjustValue(Activity passedActivity){
        // Get the screen dimensions
        DisplayMetrics displayMetrics = new DisplayMetrics();
        passedActivity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        float widthDPI = displayMetrics.xdpi;
        int screenWidth = displayMetrics.widthPixels;
        float screenWidthInches = screenWidth / widthDPI;
        fontAdjustValue =  (float) (screenWidthInches / 2.51);
        Log.e("test", Float.toString(fontAdjustValue));
    }

    public static float getFontAdjustValue(){
        //Checks that the value is greater than zero as if it isn't then the text will be invisible
        if (fontAdjustValue < 0.0){
            //Runs if the value is 0 or negative
            fontAdjustValue = (float) 1.0;
        }
        return fontAdjustValue;
    }
}
