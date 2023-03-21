package net.r4geviperzz.questionmaster;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AddNewQuestionPage extends Activity {
    private DBHelper dbHelper = new DBHelper(AddNewQuestionPage.this);
    private RecyclerView recyclerView;
    private IndividualQuestionAdapter adapter;
    private List<String> questionAndAnsList;
    private Spinner cardColourDropdown;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_question_page);

        List<String> cardColours = dbHelper.getAllCardTypeNames();
        cardColours.add(0, "None");

        //Gets a reference to the card colour spinner
        cardColourDropdown = findViewById(R.id.newQuestionCardColourSpinner);

        //Creates the ArrayAdapter that is to be used in the card colour spinner
        ArrayAdapter<String> cardColourAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, cardColours);
        //Binds the ArrayAdapter to the card colour spinner
        cardColourDropdown.setAdapter(cardColourAdapter);

        //Calls the method that sets up the recycler view
        setupRecyclerView();

        Button addQuestionBtn = findViewById(R.id.newQuestionAddNewQuestionBtn);

        addQuestionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("NewQuestionValues", questionAndAnsList.toString());
                checkQuestionValid();
            }
        });
    }

    //This method is responsible for checking that the entered values for the new question are acceptable
    private void checkQuestionValid(){
        TextView errorLabel = findViewById(R.id.newQuestionErrorLabel);
        errorLabel.setText("");

        if(questionAndAnsList.get(0).equals("")){
            //This runs if the question EditView is blank
            errorLabel.setText("The question can't be blank");
        }else {
            Boolean anAnswerIsBlank = false;

            //This for loop is used to iterate through all the answers in the list
            for (int x = 1; x < questionAndAnsList.size();x++){
                //This if statement is used to check if any of the answers are blank
                if (questionAndAnsList.get(x).equals("")){
                    anAnswerIsBlank = true;
                    break;
                }
            }

            //This if statement checks that none of the answer are blank before making any more checks
            if (anAnswerIsBlank == false) {
                //This line makes sure that all of the strings in the questionAndAnsList begin with a capital letter
                questionAndAnsList = CheckAnsList.capitaliseListStrings(questionAndAnsList);
                //Updates the data in the adapter so that all the items in the RecyclerView and List are capitalised
                adapter.setList(questionAndAnsList);

                //This line detects if there are any duplicate answers in the list
                Boolean areDuplicateAnswers = CheckAnsList.checkForListDuplicates(questionAndAnsList);

                if (areDuplicateAnswers == false) {
                    Boolean isListInAlphabeticalOrder = CheckAnsList.checkAnswerInAlphabeticalOrder(questionAndAnsList);

                    //Checks if the new answers are in alphabetical order and it they aren't puts them in alphabetical order
                    if (isListInAlphabeticalOrder == false) {
                        //Runs if the answers aren't in alphabetical order
                        //Updates the questionAndAnsList so that the answer are in alphabetical order
                        questionAndAnsList = CheckAnsList.orderAnswersByAscending(questionAndAnsList);
                        //Updates the data in the adapter so that it is in order
                        adapter.setList(questionAndAnsList);
                    }

                    //This line checks if an identical question with identical answers already exists in the questions table
                    Boolean exactQuestionAlreadyExists = dbHelper.checkExactQuestionExists(questionAndAnsList);
                    if (exactQuestionAlreadyExists == true) {
                        //This runs if an identical question with identical answers already exists in the question table
                        errorLabel.setText("The New Question Is Identical To An Already Existing Question");
                    } else {
                        //This line gets the value that is selected in the question colour dropdown
                        String questionColour = cardColourDropdown.getSelectedItem().toString();

                        //This if statement checks if the selected item in the question colour dropdown is the default value
                        if (questionColour == "None") {
                            //This runs if it is the default value as the default value isn't valid
                            errorLabel.setText("None Is Not A Valid Question Colour");
                        } else {
                            Boolean questionAlreadyExists = dbHelper.checkQuestionExists(questionAndAnsList.get(0));

                            if (questionAlreadyExists == true) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(AddNewQuestionPage.this);

                                builder.setMessage("The Same Question Already Exists But With Different Answers, Do You Still Want To Add The Question?");
                                builder.setTitle("Question Already Exists");

                                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Log.e("addingNewQuestion", "Add the question to the database");
                                        //Calls the method that will add the new question to the database
                                        addNewQuestionToDB(questionColour);
                                    }
                                });

                                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //Do nothing
                                    }
                                });

                                // Create and show the AlertDialog
                                AlertDialog dialog = builder.create();
                                dialog.show();
                            } else {
                                //Calls the method that will add the new question to the database
                                addNewQuestionToDB(questionColour);
                            }
                        }
                    }
                }else{
                    errorLabel.setText("These Is A Duplicate Answer, All Answers Must Be Unique");
                }
            }else{
                errorLabel.setText("None Of The Answers Can Be Blank");
            }
        }
    }

    //This method is responsible for adding a question to the database that is made
    //up of values in the questionAndAnsList
    public void addNewQuestionToDB(String passedQuestionColour){
        //This line gets the card colour int that corresponds to the passed card colour name
        String cardColourVal = dbHelper.getCardColourIntByCardColourName(passedQuestionColour);

        //Get the next available questionID value for the given card colour in the question table
        String questionIDVal = dbHelper.getNextQuestionId(cardColourVal);

        //Calls the method that adds the new question to the questions table
        dbHelper.setNewQuestion(cardColourVal, questionIDVal, questionAndAnsList);
        //Shows a toast message to tell the user the new question has been added
        Toast.makeText(AddNewQuestionPage.this, "New Question Added", Toast.LENGTH_SHORT).show();
        //Changes the page back to the questions page
        Intent intent = new Intent(AddNewQuestionPage.this, QuestionsPage.class);
        startActivity(intent);
    }


    //This method is responsible for setting up the RecyclerView that is
    //used to display the question and answers on this page
    private void setupRecyclerView(){
        recyclerView = findViewById(R.id.newQuestionRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //Gets the number of fields in the questions table
        int numAnswers = dbHelper.getNumFieldsInQuestionsTable();
        //Takes 2 off to account for the cardColour and questionID field
        numAnswers = numAnswers - 2;

        //This List is used to store the value that are entered into the adapter for the RecyclerView
        questionAndAnsList = new ArrayList<>();

        //Populates the list with a number of empty strings to match the number of answers plus the question in the question table
        for (int n = 0; n < numAnswers; n++){
            questionAndAnsList.add("");
        }

        //This line gets the character limit for the first answer in the questions table, this value is used
        //to limit the number of characters that can be entered into the RecyclerViews EditViews
        int charLimitNum = dbHelper.getAnswerCharLimit();
        int questionLimitNum = dbHelper.getQuestionCharLimit();

        //Creates the instance of the adapter that is used for the RecyclerView on this page
        adapter = new IndividualQuestionAdapter(AddNewQuestionPage.this, questionAndAnsList, charLimitNum, questionLimitNum, false);
        recyclerView.setAdapter(adapter);
    }

}