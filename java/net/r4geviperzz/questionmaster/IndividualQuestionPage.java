package net.r4geviperzz.questionmaster;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;
import android.view.inputmethod.InputMethodSubtype;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class IndividualQuestionPage extends AppCompatActivity {
    private DBHelper dbHelper = new DBHelper(IndividualQuestionPage.this);
    private RecyclerView recyclerView;
    private IndividualQuestionAdapter adapter;
    private List<String> questionAndAnsList;

    private String questionCardColour;
    private String questionId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_individual_question_page);

        Intent intent = getIntent();
        questionCardColour = intent.getStringExtra("cardColour");
        questionId = intent.getStringExtra("questionId");

        //Calls the method that sets up the recycler view
        setupRecyclerView();

        Button saveChangesBtn = findViewById(R.id.individualQuestionSaveChangesBtn);

        saveChangesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveChanges();
            }
        });

        Button deleteQuestionBtn = findViewById(R.id.individualQuestionDeleteBtn);

        deleteQuestionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteQuestion();
            }
        });
    }


    //This method is responsible for checking if the answers to the question have
    //been change and then updating them in the database if they have
    private void saveChanges(){
        Boolean allValuesValid = true;
        TextView errorLabel = findViewById(R.id.individualQuestionErrorLabel);

        //The questionAndAnsList is constantly updated by the RecyclerView Adapter so it can be used to get the value that are currently in the RecyclerView
        //This line detects if there are any duplicate answers in the list
        Boolean areDuplicateAnswers = CheckAnsList.checkForListDuplicates(questionAndAnsList);

        //This if checks if there are were duplicate answer in the list
        if (areDuplicateAnswers == false) {
            Boolean anAnswerIsNull = false;

            //This for loop checks than none of the values in the questionAndAnsList are blank, and none of them are null
            for (String updatedValue : questionAndAnsList){
                if (updatedValue.length() == 0){
                    errorLabel.setText("None Of The Answers Can Be Blank");
                    allValuesValid = false;
                    break;
                }else if (updatedValue.equals("null") || updatedValue.equals("Null")){
                    anAnswerIsNull = true;
                    break;
                }
            }

            //This line gets the questionAndAnswer from the database for the passed cardColour and questionId
            List<String> oldQuestionAndAnsList = dbHelper.getQuestionByColour(questionCardColour, questionId);

            List<Integer> indexValOfEditedField = new ArrayList<>();
            List<String> charValOfEditedField = new ArrayList<>();

            if (allValuesValid == true) {
                if (anAnswerIsNull == false) {
                    //This line calls the method that will check if the new list is in alphabetical order
                    Boolean isUpdatedListInAlphabeticalOrder = CheckAnsList.checkAnswerInAlphabeticalOrder(questionAndAnsList);

                    //Checks if the new answers are in alphabetical order and it they aren't puts them in alphabetical order
                    if (isUpdatedListInAlphabeticalOrder == false) {
                        //Runs if the answers aren't in alphabetical order
                        //Updates the questionAndAnsList so that the answer are in alphabetical order
                        questionAndAnsList = CheckAnsList.orderAnswersByAscending(questionAndAnsList);
                        //Updates the data in the adapter so that it is in order
                        adapter.setList(questionAndAnsList);
                    }

                    //This line makes sure that all of the strings in the questionAndAnsList has no leading or trailing white spaces
                    questionAndAnsList = CheckAnsList.removeWhiteSpaceListString(questionAndAnsList);
                    //This line makes sure that all of the strings in the questionAndAnsList begin with a capital letter
                    questionAndAnsList = CheckAnsList.capitaliseListStrings(questionAndAnsList);
                    //Updates the data in the adapter so that all the items in the RecyclerView and List are capitalised and any white space is removed
                    adapter.setList(questionAndAnsList);
                    for (int i = 1; i < oldQuestionAndAnsList.size(); i++) {
                        String updatedValue = questionAndAnsList.get(i);
                        //Check if the old value matches the new value
                        if (!oldQuestionAndAnsList.get(i).equals(updatedValue)) {
                            //This runs if the old value doesn't match the new value, meaning the value needs updating
                            Log.e("valueChanged", "The value has changed in position = " + i);
                            indexValOfEditedField.add(i);
                            charValOfEditedField.add(questionAndAnsList.get(i));
                        }
                    }
                }else{
                    allValuesValid = false;
                    errorLabel.setText("None of The Answers Can Be Null");
                }
            }

            if (allValuesValid == true) {
                errorLabel.setText("");

                //This if statement checks the the answer need updating before trying to update the answer in the table
                if (indexValOfEditedField.size() > 0) {
                    //Called the method that will update the answer in the database
                    dbHelper.updateQuestionDetails(questionCardColour, questionId, indexValOfEditedField, charValOfEditedField);
                    Toast.makeText(IndividualQuestionPage.this, "Changes Saved", Toast.LENGTH_SHORT).show();
                }
            }
        }else{
            errorLabel.setText("These Is A Duplicate Answer, All Answers Must Be Unique");
        }

        //This code hides the keyboard
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager keyboard = (InputMethodManager) getSystemService(IndividualQuestionPage.this.INPUT_METHOD_SERVICE);
            keyboard.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        Log.e("AllValuesChecked", "All the values have been checked");

    }

    //This method is responsible for setting up the RecyclerView that is
    //used to display the question and answers on this page
    private void setupRecyclerView(){
        recyclerView = findViewById(R.id.individualQuestionRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //This line gets the questionAndAnswer from the database for the passed cardColour and questionId
        questionAndAnsList = dbHelper.getQuestionByColour(questionCardColour, questionId);

        //This line gets the character limit for the first answer in the questions table, this value is used
        //to limit the number of characters that can be entered into the RecyclerViews EditViews
        int charLimitNum = dbHelper.getAnswerCharLimit();

        //Creates the instance of the adapter that is used for the RecyclerView on this page
        adapter = new IndividualQuestionAdapter(IndividualQuestionPage.this, questionAndAnsList, charLimitNum, 0, true);
        recyclerView.setAdapter(adapter);
    }

    //This method is responsible for deleting the question from the database
    private void deleteQuestion(){
        AlertDialog.Builder builder = new AlertDialog.Builder(IndividualQuestionPage.this);

        builder.setMessage("Are you sure you want to delete the question?");
        builder.setTitle("Delete Question?");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.e("questionDelete", "The question should be deleted");
                Intent intent = new Intent(IndividualQuestionPage.this, QuestionsPage.class);
                startActivity(intent);

                //Calls the method that will delete the question from the database
                Boolean success = dbHelper.deleteQuestion(questionCardColour, questionId);

                //Checks that the delete method call returned true, if it didn't then that means there isn't more than one
                //question of the given colour in the table
                if (success == true) {
                    Toast.makeText(IndividualQuestionPage.this, "Question Deleted", Toast.LENGTH_SHORT).show();
                    //Nulls the questionOrder Blob in the gameBoards table as it needs
                    //recreating in order to account for the deleted question
                    dbHelper.nullQuestionOrderFromGameBoards();
                    //Nulls the questionOrderCount Blob in the gameBoards table as it needs
                    //recreating in order to account for the deleted question
                    dbHelper.nullQuestionOrderCountFromGameBoards();
                }else{
                    Toast.makeText(IndividualQuestionPage.this, "Unable to delete question, as it is the last card of such a colour in the database", Toast.LENGTH_LONG).show();
                }
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.e("questionDelete", "The question should not be deleted");
            }
        });

        // Create and show the AlertDialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
