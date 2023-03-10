package net.r4geviperzz.questionmaster;

import android.content.Context;
import android.text.InputFilter;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;

import androidx.appcompat.widget.AppCompatEditText;

// This class is used to create an EditText view that filters out invalid none alphanumeric characters and emoji's
public class FilterEditView extends AppCompatEditText {
    public FilterEditView(Context context) {
        super(context);
        // Calls the init() method to set up the input filter
        init();
    }

    public FilterEditView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // Calls the init() method to set up the input filter
        init();
    }

    public FilterEditView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // Calls the init() method to set up the input filter
        init();
    }

    private void init() {
        // Create an InputFilter that ensure the input is alphanumeric and isn't an emoji
        InputFilter alphanumericAndEmojiFilter = new InputFilter() {
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                StringBuilder filtered = new StringBuilder();
                // Iterate over the input text
                for (int i = start; i < end; i++) {
                    char c = source.charAt(i);
                    // Check if the character is alphanumeric or special characters (excluding ℹ)
                    if ((Character.isLetterOrDigit(c) || c == ' ' || c == '_' || c == '-' || c == '.') && c != '\u2139') {
                        // Add the valid character to the filtered text
                        filtered.append(c);
                    } else if (Character.isHighSurrogate(c) || Character.isLowSurrogate(c)) {
                        // Reject surrogate pairs by returning an empty CharSequence
                        return "";
                    }
                }
                // Return the filtered text as a CharSequence
                if (source instanceof Spanned) {
                    SpannableString sp = new SpannableString(filtered);
                    TextUtils.copySpansFrom((Spanned) source, start, filtered.length(), null, sp, 0);
                    return sp;
                } else {
                    return filtered.toString();
                }
            }
        };


        // Set the EditText's filters to include the alphanumericAndEmojiFilter
        setFilters(new InputFilter[]{alphanumericAndEmojiFilter});
    }
}


