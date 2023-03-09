package net.r4geviperzz.questionmaster;

import android.content.Context;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class IndividualQuestionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    //This is the list that is used to populate all of the items in the RecyclerView
    private List<String> dbQuestionsList;
    private LayoutInflater itemLayoutInflater;
    //This class variable is used to store the character limits for the EditView views
    private int editViewCharLimit;
    //This class variable is used to store the character limit for the EditView that is used for the question
    private int questionEditViewCharLimit;
    //This Boolean is used to tell if the adapter is been used for editing a questions values or for adding a new question
    private Boolean editInstance;

    //This variable stores the integer that corresponds to a TextView view type
    private static final int textViewType = 1;
    //This variable stores the integer that corresponds to a EditText view type
    private static final int editTextViewType = 2;

    public IndividualQuestionAdapter(Context context, List<String> dbQuestionsList, int ansCharLimit, int questionCharLimit, Boolean forEditingQuestion) {
        this.itemLayoutInflater = LayoutInflater.from(context);
        this.dbQuestionsList = dbQuestionsList;
        this.editViewCharLimit = ansCharLimit;
        this.questionEditViewCharLimit = questionCharLimit;
        this.editInstance = forEditingQuestion;
    }

    // onCreateViewHolder is called when the RecyclerView needs a new view holder
    // viewType parameter is used to determine what type of view holder is being created
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == textViewType) {
            // If the viewType is textViewType, inflate the item_individual_question_text_view_layout layout and create a new TextViewHolder
            View view = itemLayoutInflater.inflate(R.layout.item_individual_question_text_view_layout, parent, false);
            return new TextViewHolder(view);
        } else {
            // If the viewType is editTextViewType, inflate the item_individual_question_edit_text_layout layout and create a new EditTextViewHolder
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_individual_question_edit_text_layout, parent, false);
            return new EditTextViewHolder((FilterEditView) view);
        }
    }

    // onBindViewHolder is called when the RecyclerView wants to update the contents of a view holder
    // holder parameter is the view holder being updated
    // position parameter is the position of the view holder in the list
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof TextViewHolder) {
            // If the view holder is a TextViewHolder, set the text of the questionTextView to the value at the current position in the list
            ((TextViewHolder) holder).questionTextView.setText(dbQuestionsList.get(position));
        } else if (holder instanceof EditTextViewHolder) {
            //This is needed as if the adapter is being used for adding a new question then the first EditView will need a different hint value and character limit
            if (editInstance == true) {
                // If the view holder is an EditTextViewHolder, set the text of the questionEditText to the value at the current position in the list
                ((EditTextViewHolder) holder).questionEditText.setText(dbQuestionsList.get(position));
            } else {
                //Checks if it is the first EditView to be added
                if (position == 0) {
                    //Changes the hint of the first EditView as it needs to be different for the question entry
                    ((EditTextViewHolder) holder).questionEditText.setHint("Enter a question");

                    // Create a new filter to limit the number of characters that can be entered into the EditText view
                    InputFilter newCharLimitFilter = new InputFilter.LengthFilter(questionEditViewCharLimit);
                    // Get the current filters applied to the EditText view
                    InputFilter[] currentFilters = ((EditTextViewHolder) holder).questionEditText.getFilters();

                    // Create a new array of filters with the new character limit filter
                    InputFilter[] newFilters = new InputFilter[currentFilters.length];
                    System.arraycopy(currentFilters, 0, newFilters, 0, currentFilters.length - 1);
                    newFilters[currentFilters.length - 1] = newCharLimitFilter;

                    // Set the new array of filters to the EditText view
                    ((EditTextViewHolder) holder).questionEditText.setFilters(newFilters);
                }

                // If the view holder is an EditTextViewHolder, set the text of the questionEditText to the value at the current position in the list
                ((EditTextViewHolder) holder).questionEditText.setText(dbQuestionsList.get(position));
            }

            // Update the value at the current position in the list with the current text in the EditText
            //This is needed so that the data in the adapter updates to the data that is in the EditViews
            dbQuestionsList.set(position, ((EditTextViewHolder) holder).questionEditText.getText().toString());
        }
    }


    // getItemCount returns the number of items in the list
    @Override
    public int getItemCount() {
        return dbQuestionsList.size();
    }

    // setList updates the list that the adapter is using
    public void setList(List<String> list){
        dbQuestionsList = list;
        notifyDataSetChanged();
    }

    // getItemViewType is used to determine what type of view holder should be created for a given position in the list
    // when in edit mode if it is the first element in the list then in should be a TextView as this is the
    // question, else it should be an EditText view
    @Override
    public int getItemViewType(int position) {
        //This if statement is used to change what views are displayed depending on what the adapter is been used for
        //If the adapter is been used for editing a question then it will return a view type
        // of TextView for the first element so that the actual question can't be changed
        //If the adapter is been used for creating a new question then it will always return a view
        //type of EditView as all the entries in the RecyclerView need to be editable
        if (editInstance == true) {
            if (position == 0) {
                // If the position is 0, return textViewType so that a TextViewHolder is created
                return textViewType;
            } else {
                // If the position is not 0, return editTextViewType so that an EditTextViewHolder is created
                return editTextViewType;
            }
        }else{
            return editTextViewType;
        }
    }

    // getItem is a helper method to get the question at a given position in the list
    public String getItem(int position) {
        return dbQuestionsList.get(position);
    }

    // TextViewHolder is the view holder for TextView views
    public class TextViewHolder extends RecyclerView.ViewHolder {
        private final TextView questionTextView;

        TextViewHolder(View itemView) {
            super(itemView);
            questionTextView = itemView.findViewById(R.id.questionTextView);
        }
    }

    // EditTextViewHolder is the view holder for EditText views
    private class EditTextViewHolder extends RecyclerView.ViewHolder {
        private final EditText questionEditText;

        EditTextViewHolder(View itemView) {
            super(itemView);
            questionEditText = itemView.findViewById(R.id.questionEditTextView);

            // Get the current filters for the EditText view
            InputFilter[] currentFilters = questionEditText.getFilters();

            // Create a new filter to limit the number of characters that can be entered into the EditText view
            InputFilter charLimitFilter = new InputFilter.LengthFilter(editViewCharLimit);

            // Create a new array to hold all the filters for the EditText view, with enough space for both the current and new filters
            InputFilter[] allFilters = new InputFilter[currentFilters.length + 1];

            // Copy the current filters into the new array
            System.arraycopy(currentFilters, 0, allFilters, 0, currentFilters.length);

            // Add the new character limit filter to the end of the new array
            allFilters[currentFilters.length] = charLimitFilter;

            // Set the new array of filters on the EditText view
            questionEditText.setFilters(allFilters);

            // Set a text change listener on the EditText view to update the corresponding value in the list when the user enters a new value
            questionEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    // Update the value at the current position in the list with the new value entered by the user
                    int viewPos = getAdapterPosition();

                    //Checks that the view position is a positive value as if a ViewHolder hasn't been bound then it will be -1
                    if (viewPos >= 0) {
                        dbQuestionsList.set(viewPos, s.toString());
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });
        }
    }
}




