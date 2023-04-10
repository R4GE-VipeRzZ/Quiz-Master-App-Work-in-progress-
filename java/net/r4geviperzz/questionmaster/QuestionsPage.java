package net.r4geviperzz.questionmaster;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuestionsPage extends AppCompatActivity implements QuestionsRecyclerViewAdapter.ItemClickListener{
    //Instantiates an instance of the DBHelper class so that data can be read from the database
    private DBHelper dbHelper = new DBHelper(QuestionsPage.this);
    //This is used to keep a reference to the adapter instance for the RecyclerView
    private QuestionsRecyclerViewAdapter recyclerAdapter;
    //This multimap is used to store the card colour and question id of each question
    private Map<Integer, List<String>> questionsDetailsMultiMap = new HashMap<>();
    //This Boolean is used to stop the onItemSelectedMethod from running code when it is triggered due to the spinner first been loaded on the page
    private Boolean firstTimeCardColourSpinnerLoad;
    private Boolean firstTimeOrdBySpinnerLoad;
    private List<String> questionsDetailsList = new ArrayList<>();
    private List<String> questionsToDisplayList = new ArrayList<>();
    private List<String> questionColourNumList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questions_page);

        int widthInPX = DeviceSize.getDeviceWidthPX();


        this.firstTimeCardColourSpinnerLoad = true;
        this.firstTimeOrdBySpinnerLoad = true;

        List<String> cardColours = dbHelper.getAllCardTypeNames();
        cardColours.add(0, "All");
        String[] orderByValArray = {"Ascending", "Descending"};

        Float heightAdjustValue = TextScale.getFontAdjustHeightValue();
        Float widthAdjustValue = TextScale.getFontAdjustWidthValue();
        int spinnerTextSize = (int) (16 * heightAdjustValue);
        Float density = DeviceSize.getDeviceDensity();

        //Gets a reference to the card colour spinner
        Spinner cardColourDropdown = findViewById(R.id.questionsPageCardColourSpinner);
        CustomSpinnerStyle.setSpinnerStyle(QuestionsPage.this, cardColourDropdown, heightAdjustValue, cardColours, (int) ((75 * widthAdjustValue) * density), spinnerTextSize, 22, 0);

        //Gets a reference to the order by spinner
        Spinner orderByDropdown = findViewById(R.id.questionsPageOrderTypeSpinner);
        CustomSpinnerStyle.setSpinnerStyle(QuestionsPage.this, orderByDropdown, heightAdjustValue, orderByValArray, (int) ((100 * widthAdjustValue) * density), spinnerTextSize, 22, -60);


        cardColourDropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Handle the selection event here
                String selectedItem = parent.getItemAtPosition(position).toString();

                if (firstTimeCardColourSpinnerLoad == false) {
                    //Gets the item that is currently selected in the orderBy spinner
                    String orderByValue = orderByDropdown.getSelectedItem().toString();

                    getUpdatedQuestionDetailsList(selectedItem, orderByValue);
                }else{
                    firstTimeCardColourSpinnerLoad= false;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing if nothing is selected
            }
        });

        orderByDropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Handle the selection event here
                String selectedItem = parent.getItemAtPosition(position).toString();

                if (firstTimeOrdBySpinnerLoad == false) {
                    //Gets the item that is currently selected in the orderBy spinner
                    String cardColourValue = cardColourDropdown.getSelectedItem().toString();

                    getUpdatedQuestionDetailsList(cardColourValue, selectedItem);
                }else{
                    firstTimeOrdBySpinnerLoad= false;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing if nothing is selected
            }
        });

        //Calls the method that will setup the RecyclerView for the questions
        setupRecycler();

        //Gets a reference to the add new question btn
        Button addQuestionBtn = findViewById(R.id.questionPageAddQuestionBtn);
        //Sets the padding on the add new question button depending on the devices width
        int addQuestionBtnPaddingVal = (int) (widthInPX / 22);
        addQuestionBtn.setPadding(addQuestionBtnPaddingVal, addQuestionBtnPaddingVal, addQuestionBtnPaddingVal, addQuestionBtnPaddingVal);

        addQuestionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("addNewQuestion", "A new question should be added");
                Intent intent = new Intent(QuestionsPage.this, NewQuestionPage.class);
                startActivity(intent);
            }
        });
    }

    // This method is used to ensure that the back button will take the user back to the main menu
    public void onBackPressed(){
        Intent intent = new Intent(QuestionsPage.this, MainActivity.class);
        startActivity(intent);

        super.onBackPressed();
    }

    //This method is used to decide what questions should be read from the database when an item is selected in the dropdown filters
    private void getUpdatedQuestionDetailsList(String passedSelectedItem, String passedOrderByValue){
        if (passedSelectedItem == "All") {
            questionsDetailsList = dbHelper.getAllQuestionsNoAnswers(passedOrderByValue);
            //Calls the method that will setup the multimap for the new question that have been read from the database
            setupQuestionMultiMap(false, false);
        } else {
            //Calls the method that will get the question details from the database for the given question colour
            getQuestionsOfCardColour(passedSelectedItem, passedOrderByValue);
            //Calls the method that will setup the multimap for the new question that have been read from the database
            setupQuestionMultiMap(false, true);
        }
    }

    ///This method is used to get the questions of a specific card colour when an option other than All is selected in the card colour dropdown
    private void getQuestionsOfCardColour(String passedCardColourName, String passedOrderByValue){
        //Gets the card colour value using the passed card colour name
        String cardColourVal = dbHelper.getCardColourIntByCardColourName(passedCardColourName);
        //Gets all the questions that have the given card colour value
        questionsDetailsList = dbHelper.getAllQuestionsNoAnswersByCardColour(passedOrderByValue, cardColourVal);
    }

    //This method is used to handel a question been clicked in the RecyclerView
    @Override
    public void onItemClick(View view, int position) {
        // Handle item click here
        List<String> questionDetailsList = questionsDetailsMultiMap.get(position);
        String questionCardColour = questionDetailsList.get(0);
        String questionId = questionDetailsList.get(1);

        Intent intent = new Intent(QuestionsPage.this, IndividualQuestionPage.class);
        //Passes the id of the board that was selected to the BoardPage class
        intent.putExtra("cardColour", questionCardColour);
        intent.putExtra("questionId", questionId);
        startActivity(intent);
    }

    //This method is used to handel a delete button been clicked in the RecyclerView
    public void onDeleteClick(View view, int position) {
        // Create the dialog
        CustomQuestionDialog dialog;
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
            //This runs when on API 19 or lower
            dialog = new CustomQuestionDialog(QuestionsPage.this, R.style.MyDialogThemeAPI19);
        } else {
            //This runs when running on API 21 or higher

            //Checks if running on API 22 or less
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP_MR1) {
                dialog = new CustomQuestionDialog(QuestionsPage.this, R.style.MyDialogThemeAPI21And22);
            } else {
                //Runs if running on API 23 and up
                dialog = new CustomQuestionDialog(QuestionsPage.this);
            }
        }

        if (dialog != null) {
            Window dialogWindow = dialog.getWindow();

            if (dialogWindow != null) {
                // Inflate the dialog layout
                LayoutInflater inflater = LayoutInflater.from(QuestionsPage.this);
                View dialogView = inflater.inflate(R.layout.alert_dialog_layout, null);

                //Set the content view of the dialog
                dialog.setContentView(dialogView);

                //Alert dialog title
                TextView titleLabel = dialogView.findViewById(R.id.alertDialogTitle);
                //Alert dialog message
                TextView messageLabel = dialogView.findViewById(R.id.alertDialogMessage);

                titleLabel.setText("Delete Question?");
                messageLabel.setText("Are you sure you want to delete the question?");

                //Setup Yes button
                Button alertBtnYes = dialogView.findViewById(R.id.alertDialogBtnYes);
                alertBtnYes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Yes Click
                        dialog.dismiss();

                        Log.e("questionDelete", "The question should be deleted");
                        Log.e("position", "position = " + position);

                        List<String> questionDetailsList = questionsDetailsMultiMap.get(position);
                        String questionCardColour = questionDetailsList.get(0);
                        String questionId = questionDetailsList.get(1);

                        //Calls the method that will delete the question from the database
                        Boolean success = dbHelper.deleteQuestion(questionCardColour, questionId);

                        Log.e("questionDetails", "questionCardColour = " + questionCardColour + " questionId = " + questionId);

                        //Checks that the delete method call returned true, if it didn't then that means there isn't more than one
                        //question of the given colour in the table
                        if (success == true) {
                            //Removes the question from the questionToDisplayList
                            questionsToDisplayList.remove(position);

                            if (questionColourNumList.size() > 1){
                                //Removes the card colour number from the questionColourNumList
                                questionColourNumList.remove(position);
                            }

                            //Removes the question from the MultiMap
                            questionsDetailsMultiMap.remove(position);

                            //Renumbers the keys after the removed key in the MultiMap
                            //Iterates through the keys in the MultiMap starting at the position where the
                            //key pair was removed and continuing until the end of the MultiMap
                            for (int i = position + 1; i <= questionsDetailsMultiMap.keySet().size(); i++) {
                                // Get the values associated with the current key
                                List<String> values = questionsDetailsMultiMap.get(i);
                                // Remove the current key and its associated values from the MultiMap
                                questionsDetailsMultiMap.remove(i);
                                // Add the values back into the MultiMap with a new key that is one less than the current key
                                questionsDetailsMultiMap.put((i - 1), values);
                            }

                            recyclerAdapter.notifyDataSetChanged();

                            Toast.makeText(QuestionsPage.this, "Question Deleted", Toast.LENGTH_SHORT).show();
                            //Nulls the questionOrder Blob in the gameBoards table as it needs
                            //recreating in order to account for the deleted question
                            dbHelper.nullQuestionOrderFromGameBoards();
                            //Nulls the questionOrderCount Blob in the gameBoards table as it needs
                            //recreating in order to account for the deleted question
                            dbHelper.nullQuestionOrderCountFromGameBoards();
                        }else{
                            Toast.makeText(QuestionsPage.this, "Unable to delete question, as it is the last card of such a colour in the database", Toast.LENGTH_LONG).show();
                        }
                    }
                });

                //Setup No button
                Button alertBtnNo = dialogView.findViewById(R.id.alertDialogBtnNo);
                alertBtnNo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //No Click
                        dialog.dismiss();

                        Log.e("questionDelete", "The question should not be deleted");
                    }
                });


                dialogWindow.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, (int) (DeviceSize.getDeviceHeightPX() * 0.275));
                // Show the dialog
                dialog.show();
            } else {
                Log.e("alertDialogWindowError", "Failed to get the window off the alert dialog");
            }
        } else {
            Log.e("alertDialogError", "Failed to create alert dialog");
        }
    }

    //This method sets up the MultiMap that corresponds to the questions
    private List<String> setupQuestionMultiMap(Boolean initialSetup, Boolean singleColour){
        //Clears the questionsToDisplayList so that it doesn't have any old values in it
        questionsToDisplayList.clear();
        //Clears the questionColourNumList list so that it doesn't have any old values in it
        questionColourNumList.clear();
        List<String> tempList = new ArrayList<>();

        //This variable is used to control where the values in the questionsDetailsList are added
        int count = 0;
        //This variable sets the key that is used when adding a List of values to the questionsDetailsMultiMap
        int keyVal = 0;
        for (int x = 0; x < questionsDetailsList.size(); x++){
            if (count == 2) {
                //Adds the question to the questionsDetailsList
                questionsToDisplayList.add(questionsDetailsList.get(x));
                //Adds the card colour and question id for the question to the multimap
                questionsDetailsMultiMap.put(keyVal, new ArrayList<>(tempList));
                tempList.clear();
                keyVal++;
            }else if (count == 0){
                String cardColourNum = questionsDetailsList.get(x);

                //Checks that a single card colour isn't been displayed as if it is then only a single card colour number
                //need to be set in the List as the card colour number doesn't need to be tracked for each question
                if (singleColour == false) {
                    //Adds the question card colour number to the questionColourNumList
                    questionColourNumList.add(cardColourNum);
                }
                //Adds the card colour to the list
                tempList.add(cardColourNum);
            }else{
                //Adds the question id to the list
                tempList.add(questionsDetailsList.get(x));
            }

            count++;

            if(count == 3){
                count = 0;
            }
        }

        //Sets the single card colour value that is need if only one colour of card is been displayed
        if (singleColour == true){
            questionColourNumList.add(questionsDetailsList.get(0));
        }

        //This if statement checks if the method has been called during the initial creation of the RecyclerView or if
        //it has been called because the values that need to be displayed have been updated
        if (initialSetup == true) {
            return questionsToDisplayList;
        }else{
            //Lets the adapter know that it need to update the data in its items as the data has been changed
            recyclerAdapter.notifyDataSetChanged();
            return null;
        }
    }

    //This method sets up the RecyclerView for all of the questions
    private void setupRecycler(){
        //This line gets all the questions from the database along with the card colour value and question id
        questionsDetailsList = dbHelper.getAllQuestionsNoAnswers("Ascending");

        questionsToDisplayList = setupQuestionMultiMap(true, false);

        // Get a reference to the RecyclerView in your layout
        RecyclerView myRecyclerView = findViewById(R.id.questionsPageRecyclerView);

        // Create an instance of your adapter class
        recyclerAdapter = new QuestionsRecyclerViewAdapter(QuestionsPage.this, questionsToDisplayList, questionColourNumList);

        // Set the click listener for the adapter
        recyclerAdapter.setClickListener(QuestionsPage.this);

        // Set the adapter for the RecyclerView
        myRecyclerView.setAdapter(recyclerAdapter);

        // Create a LinearLayoutManager to display the items in a vertical list
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        myRecyclerView.setLayoutManager(layoutManager);
    }
}
