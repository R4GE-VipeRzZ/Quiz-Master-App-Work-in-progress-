package net.r4geviperzz.questionmaster;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuestionsPage extends Activity implements QuestionsRecyclerViewAdapter.ItemClickListener{
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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questions_page);

        this.firstTimeCardColourSpinnerLoad = true;
        this.firstTimeOrdBySpinnerLoad = true;

        List<String> cardColours = dbHelper.getAllCardTypeNames();
        cardColours.add(0, "All");
        String[] orderByValArray = {"Ascending", "Descending"};

        //Gets a reference to the card colour spinner
        Spinner cardColourDropdown = findViewById(R.id.questionsPageCardColourSpinner);

        //Creates the ArrayAdapter that is to be used in the card colour spinner
        ArrayAdapter<String> cardColourAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, cardColours);
        //Binds the ArrayAdapter to the card colour spinner
        cardColourDropdown.setAdapter(cardColourAdapter);

        //Gets a reference to the order by spinner
        Spinner orderByDropdown = findViewById(R.id.questionsPageOrderTypeSpinner);

        //Creates the ArrayAdapter that is to be used in the order by spinner
        ArrayAdapter<String> orderTyeValAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, orderByValArray);
        //Binds the ArrayAdapter to the order by spinner
        orderByDropdown.setAdapter(orderTyeValAdapter);

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

        addQuestionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("addNewQuestion", "A new question should be added");
                Intent intent = new Intent(QuestionsPage.this, AddNewQuestionPage.class);
                startActivity(intent);
            }
        });
    }

    //This method is used to decide what questions should be read from the database when an item is selected in the dropdown filters
    private void getUpdatedQuestionDetailsList(String passedSelectedItem, String passedOrderByValue){
        if (passedSelectedItem == "All") {
            questionsDetailsList = dbHelper.getAllQuestionsNoAnswers(passedOrderByValue);
            //Calls the method that will setup the multimap for the new question that have been read from the database
            setupQuestionMultiMap(false);
        } else {
            //Calls the method that will get the question details from the database for the given question colour
            getQuestionsOfCardColour(passedSelectedItem, passedOrderByValue);
            //Calls the method that will setup the multimap for the new question that have been read from the database
            setupQuestionMultiMap(false);
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

    //This method sets up the MultiMap that corresponds to the questions
    private List<String> setupQuestionMultiMap(Boolean initialSetup){
        //This List<String> contains all the questions that need to be displayed in the RecyclerView
        List<String> questionsToDisplayList = new ArrayList<>();
        List<String> tempList = new ArrayList<>();

        //This variable is used to control where the values in the questionsDetailsList are added
        int count = 0;
        //This variable sets the key that is used when adding a List of values to the questionsDetailsMultiMap
        int keyVal = 0;
        for (int x = 0; x < questionsDetailsList.size(); x++){
            if (count == 2){
                //Adds the question to the questionsDetailsList
                questionsToDisplayList.add(questionsDetailsList.get(x));
                //Adds the card colour and question id for the question to the multimap
                questionsDetailsMultiMap.put(keyVal, new ArrayList<>(tempList));
                tempList.clear();
                keyVal++;
            }else{
                //Adds ether the card colour of question id to the list depending on the x value
                tempList.add(questionsDetailsList.get(x));
            }

            count++;

            if(count == 3){
                count = 0;
            }
        }

        //This if statement checks if the method has been called during the initial creation of the RecyclerView or if
        //it has been called because the values that need to be displayed have been updated
        if (initialSetup == true) {
            return questionsToDisplayList;
        }else{
            //Calls the update method in the adapter to change the values that are displayed in the RecyclerView
            recyclerAdapter.updateList(questionsToDisplayList);
            //Lets the adapter know that it need to update the data in its items as the data has been changed
            recyclerAdapter.notifyDataSetChanged();
            return null;
        }
    }

    //This method sets up the RecyclerView for all of the questions
    private void setupRecycler(){
        //This line gets all the questions from the database along with thee card colour value and question id
        questionsDetailsList = dbHelper.getAllQuestionsNoAnswers("Ascending");

        List<String> questionsToDisplayList = setupQuestionMultiMap(true);

        // Get a reference to the RecyclerView in your layout
        RecyclerView myRecyclerView = findViewById(R.id.questionsPageRecyclerView);

        // Create an instance of your adapter class
        recyclerAdapter = new QuestionsRecyclerViewAdapter(QuestionsPage.this, questionsToDisplayList);

        // Set the click listener for the adapter
        recyclerAdapter.setClickListener(QuestionsPage.this);

        // Set the adapter for the RecyclerView
        myRecyclerView.setAdapter(recyclerAdapter);

        // Create a LinearLayoutManager to display the items in a vertical list
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        myRecyclerView.setLayoutManager(layoutManager);
    }
}
