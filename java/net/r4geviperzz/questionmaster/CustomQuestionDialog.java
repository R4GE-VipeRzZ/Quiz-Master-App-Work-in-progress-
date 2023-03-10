package net.r4geviperzz.questionmaster;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

//This class is used to create a custom dialog class that is used to override the default dialog class
//This needs to be done so that a user is unable to use the back button when been shown a question
public class CustomQuestionDialog extends Dialog {
    //Construct that is used when only the context is provided
    public CustomQuestionDialog(@NonNull Context context) {
        super(context);
    }

    //Constructor that is used when the context and layout resource is provided
    public CustomQuestionDialog(@NonNull Context context, int styleResId) {
        super(context, styleResId);
    }

    @Override
    public void onBackPressed() {
        //Do nothing so that the back button doesn't work whilst the dialog window is open
    }
}
