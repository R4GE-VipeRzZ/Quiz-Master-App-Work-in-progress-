package net.r4geviperzz.questionmaster;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import java.util.Arrays;
import java.util.List;

public class CustomSpinnerStyle {
    private static Float widthAdjustValue = TextScale.getFontAdjustWidthValue();
    private static Float density = DeviceSize.getDeviceDensity();

    public static void setSpinnerBackground(Context passedContext, Spinner passedSpinner, float passedAdjustValue, int arrowAdjustValue, Boolean errorBackground){
        if (errorBackground == false){
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                //This code is ran if the API level is less than 23 / Marshmallow
                passedSpinner.setBackgroundColor(Color.TRANSPARENT);

                LayerDrawable layerDrawable = null;

                //This code is ran if the API level is 23 or more
                if (passedAdjustValue < 1.25) {
                    //26dp size
                    // Get the reference to the layer list drawable
                    layerDrawable = (LayerDrawable) ContextCompat.getDrawable(passedContext, R.drawable.spinner_background_26dp_png);
                } else if (passedAdjustValue > 1.25 && passedAdjustValue <= 1.5) {
                    //32dp size
                    layerDrawable = (LayerDrawable) ContextCompat.getDrawable(passedContext, R.drawable.spinner_background_32dp_png);
                } else if (passedAdjustValue > 1.5 && passedAdjustValue <= 1.75) {
                    //39dp size
                    layerDrawable = (LayerDrawable) ContextCompat.getDrawable(passedContext, R.drawable.spinner_background_39dp_png);
                } else if (passedAdjustValue > 1.75 && passedAdjustValue <= 2) {
                    //46dp size
                    layerDrawable = (LayerDrawable) ContextCompat.getDrawable(passedContext, R.drawable.spinner_background_46dp_png);
                } else if (passedAdjustValue > 2) {
                    //52dp size
                    layerDrawable = (LayerDrawable) ContextCompat.getDrawable(passedContext, R.drawable.spinner_background_52dp_png);
                }

                // Set the new right inset for the bitmap layer
                layerDrawable.setLayerInset(0, 0, 0, (int) ((arrowAdjustValue * widthAdjustValue) * density), 0);
                passedSpinner.setBackground(layerDrawable);
            }else {
                //This code is ran if the API level is 23 or more
                if (passedAdjustValue < 1.25) {
                    //26dp size
                    passedSpinner.setBackgroundResource(R.drawable.spinner_background_26dp);
                } else if (passedAdjustValue > 1.25 && passedAdjustValue <= 1.5) {
                    //32dp size
                    passedSpinner.setBackgroundResource(R.drawable.spinner_background_32dp);
                } else if (passedAdjustValue > 1.5 && passedAdjustValue <= 1.75) {
                    //39dp size
                    passedSpinner.setBackgroundResource(R.drawable.spinner_background_39dp);
                } else if (passedAdjustValue > 1.75 && passedAdjustValue <= 2) {
                    //46dp size
                    passedSpinner.setBackgroundResource(R.drawable.spinner_background_46dp);
                } else if (passedAdjustValue > 2) {
                    //52dp size
                    passedSpinner.setBackgroundResource(R.drawable.spinner_background_52dp);
                }
            }
        }else{
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                //This code is ran if the API level is less than 23 / Marshmallow
                passedSpinner.setBackgroundColor(Color.TRANSPARENT);

                LayerDrawable layerDrawable = null;

                //This code is ran if the API level is 23 or more
                if (passedAdjustValue < 1.25) {
                    //26dp size
                    // Get the reference to the layer list drawable
                    layerDrawable = (LayerDrawable) ContextCompat.getDrawable(passedContext, R.drawable.spinner_background_red_26dp_png);
                } else if (passedAdjustValue > 1.25 && passedAdjustValue <= 1.5) {
                    //32dp size
                    layerDrawable = (LayerDrawable) ContextCompat.getDrawable(passedContext, R.drawable.spinner_background_red_32dp_png);
                } else if (passedAdjustValue > 1.5 && passedAdjustValue <= 1.75) {
                    //39dp size
                    layerDrawable = (LayerDrawable) ContextCompat.getDrawable(passedContext, R.drawable.spinner_background_red_39dp_png);
                } else if (passedAdjustValue > 1.75 && passedAdjustValue <= 2) {
                    //46dp size
                    layerDrawable = (LayerDrawable) ContextCompat.getDrawable(passedContext, R.drawable.spinner_background_red_46dp_png);
                } else if (passedAdjustValue > 2) {
                    //52dp size
                    layerDrawable = (LayerDrawable) ContextCompat.getDrawable(passedContext, R.drawable.spinner_background_red_52dp_png);
                }

                // Set the new right inset for the bitmap layer
                layerDrawable.setLayerInset(1, 0, 0, (int) ((arrowAdjustValue * widthAdjustValue) * density), 0);
                passedSpinner.setBackground(layerDrawable);
            }else {
                //This code is ran if the API level is 23 or more
                if (passedAdjustValue < 1.25) {
                    //26dp size
                    passedSpinner.setBackgroundResource(R.drawable.spinner_background_red_26dp);
                } else if (passedAdjustValue > 1.25 && passedAdjustValue <= 1.5) {
                    //32dp size
                    passedSpinner.setBackgroundResource(R.drawable.spinner_background_red_32dp);
                } else if (passedAdjustValue > 1.5 && passedAdjustValue <= 1.75) {
                    //39dp size
                    passedSpinner.setBackgroundResource(R.drawable.spinner_background_red_39dp);
                } else if (passedAdjustValue > 1.75 && passedAdjustValue <= 2) {
                    //46dp size
                    passedSpinner.setBackgroundResource(R.drawable.spinner_background_red_46dp);
                } else if (passedAdjustValue > 2) {
                    //52dp size
                    passedSpinner.setBackgroundResource(R.drawable.spinner_background_red_52dp);
                }
            }
        }
    }
    public static SpinnerAdapter setSpinnerStyle(Context passedContext, Spinner passedSpinner, float passedAdjustValue, Object passedItemValues, int passedDropdownWidth, int spinnerTextSize, int passedPadding, int arrowAdjustValue) {
        //Calls the method that will set the background for the spinner
        setSpinnerBackground(passedContext, passedSpinner, passedAdjustValue, arrowAdjustValue, false);

        //Sets the width of the spinner dropdown list.
        passedSpinner.setDropDownWidth(passedDropdownWidth);

        //A subclass of ArrayAdapter that overrides the getView and getDropDownView methods.
        class OverwriteArrayAdapterMethods extends ArrayAdapter<String> {
            private int spinnerTextSize;
            private int passedPadding;

            //Constructor for creating an adapter from a List<String>
            public OverwriteArrayAdapterMethods(Context context, int resource, List<String> items, int spinnerTextSize, int passedPadding) {
                super(context, resource, items);
                this.spinnerTextSize = spinnerTextSize;
                this.passedPadding = passedPadding;
            }

            //Constructor for creating an adapter from a String[]
            public OverwriteArrayAdapterMethods(Context context, int resource, String[] items, int spinnerTextSize, int passedPadding) {
                super(context, resource, items);
                this.spinnerTextSize = spinnerTextSize;
                this.passedPadding = passedPadding;
            }

            //Overrides the getView method to set the text size and padding for the selected item.
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                // Inflate the default layout for the selected item
                View view = super.getView(position, convertView, parent);

                // Set the text size for the selected item
                TextView textView = (TextView) view.findViewById(android.R.id.text1);
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, spinnerTextSize);

                // Remove padding from the selected item
                textView.setPadding(0, 0, 0, 0);

                return view;
            }

            //Overrides the getDropDownView method to set the text size, padding, and custom layout for each dropdown item.
            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                // Inflate the custom layout for each dropdown item
                View view = LayoutInflater.from(getContext()).inflate(R.layout.custom_spinner_dropdown_item, parent, false);

                // Set the text size for each dropdown item
                TextView textView = (TextView) view.findViewById(android.R.id.text1);
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, spinnerTextSize);

                // Set the padding for the dropdown item
                textView.setPadding(passedPadding, passedPadding, 0, passedPadding);

                // Set the text for the dropdown item
                String item = getItem(position);
                textView.setText(item);

                return view;
            }
        }

        //Creates a custom adapter that inflates the custom layout for each dropdown item
        SpinnerAdapter adapter;
        if (passedItemValues instanceof List<?>) {
            //If the passedItemValues is a List, cast it to a List of Strings
            List<String> itemList = (List<String>) passedItemValues;
            //Create a new adapter with the List of items, custom layout, and other passed values
            adapter = new OverwriteArrayAdapterMethods(passedContext, R.layout.custom_spinner_dropdown_item, itemList, spinnerTextSize, passedPadding);
        } else if (passedItemValues instanceof String[]) {
            //If the passedItemValues is an array of Strings, convert it to a List of Strings
            String[] itemArray = (String[]) passedItemValues;
            List<String> itemList = Arrays.asList(itemArray);
            //Creates a new adapter with the list of items, custom layout, and other passed values
            adapter = new OverwriteArrayAdapterMethods(passedContext, R.layout.custom_spinner_dropdown_item, itemList, spinnerTextSize, passedPadding);
        } else {
            //If the passedItemValues is not a List or an Array of Strings, set the adapter to null
            adapter = null;
            Log.e("custSpinnerPassedValErr", "The list / array of values passed to the CustomSpinnerStyle for the spinners items in invalid");
        }

        // Binds the adapter to the spinner
        passedSpinner.setAdapter(adapter);

        return adapter;
    }
}
