package net.r4geviperzz.questionmaster;

import android.content.Context;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.AttributeSet;
import androidx.appcompat.widget.AppCompatEditText;

// This class is used to create an EditText view that filters out invalid UTF-8 and UTF-16 characters
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
        // Create an InputFilter that removes invalid UTF-8 and UTF-16 characters from the input text
        InputFilter utf8_16_Emoji_Filter = new InputFilter() {
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                StringBuilder filtered = new StringBuilder(end - start);
                // Iterate over the input text
                for (int i = start; i < end; i++) {
                    char c = source.charAt(i);
                    // Check if the character is an emoji
                    if (Character.getType(c) == Character.SURROGATE || Character.getType(c) == Character.OTHER_SYMBOL ||
                            c == '↔' || c == '⤴' || c == '⤵' || c == '〽' || c == '‼' || c == '⁉' || c == '〰' ||
                            c == '#' || c == '*' || c == '0' || c == '1' || c == '2' || c == '3' || c == '4' ||
                            c == '5' || c == '6' || c == '7' || c == '8' || c == '9' || c == 'ℹ' ||
                            c == '◻' || c == '◼' || c == '◽' || c == '◾') {
                        // Reject the emoji by returning an empty CharSequence
                        return "";
                    }
                    // Check if the character is a valid UTF-8 or UTF-16 character
                    else if (c >= '\u0000' && c <= '\uD7FF' || c >= '\uE000' && c <= '\uFFFF') {
                        // Add the valid character to the filtered text
                        filtered.append(c);
                    }
                    else {
                        // Reject the character by returning an empty CharSequence
                        return "";
                    }
                }
                // Return the filtered text as a CharSequence
                return filtered.toString();
            }
        };
        // Set the EditText's filters to include the utf8Filter
        setFilters(new InputFilter[]{utf8_16_Emoji_Filter});
    }
}


