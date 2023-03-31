package net.r4geviperzz.questionmaster;

import android.app.Activity;
import android.util.DisplayMetrics;
import android.util.Log;

import java.lang.reflect.AccessibleObject;

//This class is used to calculate a value for adjusting the font size of text depending on the screen physical width
public class TextScale {
    private static float fontAdjustValueWidth = 0.0f;
    private static float fontAdjustValueHeight = 0.0f;

    //This method is used to calculate the adjustment value for text size depending on the device size
    //this value is used for scaling the text in layouts such as dialog windows
    public static void setFontAdjustValue(Activity passedActivity){
        //2.51 is the screen width in inches of the reference device
        fontAdjustValueWidth =  (float) (DeviceSize.getDeviceWidthInch() / 2.51);
        fontAdjustValueHeight = (float) (DeviceSize.getDeviceHeightInch() / 4.85);
    }

    //Gets the fontAdjustValue
    public static float getFontAdjustWidthValue(){
        //Checks that the value is greater than zero as if it isn't then the text will be invisible
        if (fontAdjustValueWidth < 0.0){
            //Runs if the value is 0 or negative
            fontAdjustValueWidth = (float) 1.0;
        }
        return fontAdjustValueWidth;
    }

    //Gets the fontAdjustHeightValue
    public static float getFontAdjustHeightValue(){
        //Checks that the value is greater than zero as if it isn't then the text will be invisible
        if (fontAdjustValueHeight < 0.0){
            //Runs if the value is 0 or negative
            fontAdjustValueHeight = (float) 1.0;
        }
        return fontAdjustValueHeight;
    }
}
