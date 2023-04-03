package net.r4geviperzz.questionmaster;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.os.Handler;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

public class SetupPage extends AppCompatActivity {
    //Instantiates an instance of the DBHelper class so that data can be read from the database
    private DBHelper dbHelper = new DBHelper(SetupPage.this);
    private Question quest = new Question();
    private Boolean isUserInput = true;
    private final Handler handler = new Handler();
    private String team1DropdownPrevVal = null;
    private String team2DropdownPrevVal = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_page);

        Float heightAdjustValue = TextScale.getFontAdjustHeightValue();
        TextView team1Label = findViewById(R.id.setupPgTeam1Label);
        TextView team2Label = findViewById(R.id.setupPgTeam2Label);

        //Adjusts the size of the team spinner labels
        team1Label.setTextSize((int) (21 * heightAdjustValue));
        team2Label.setTextSize((int) (21 * heightAdjustValue));

        //Get a reference to the board spinner
        Spinner boardDropdown = findViewById(R.id.setupPgBoardDropdown);

        //Get a reference to the team 1 spinner
        Spinner team1Dropdown = findViewById(R.id.setupPgTeam1Dropdown);

        //Get a reference to the team 2 spinner
        Spinner team2Dropdown = findViewById(R.id.setupPgTeam2Dropdown);


        teamSpinners(team1Dropdown, team2Dropdown);
        boardSpinner(boardDropdown);
        submitBtnFunc(boardDropdown, team1Dropdown, team2Dropdown);
    }

    //This method is called to change the activity to the boardPage activity
    private void changeToBoardPg(String selectedBoardId){
        Intent intent = new Intent(SetupPage.this, BoardPage.class);
        //Passes the id of the board that was selected to the BoardPage class
        intent.putExtra("boardId", selectedBoardId);
        startActivity(intent);
    }

    //This method is responsible for setting the background images on the spinners
    private void setSpinnerBackground(Boolean passedValidValue, Spinner passedSpinner){
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M){
            //This code is ran if the API level is less than 23 / Marshmallow

            if(passedValidValue == false) {
                passedSpinner.setBackgroundResource(R.drawable.spinner_background_red_api_22_or_less);
            }else{
                passedSpinner.setBackgroundColor(Color.TRANSPARENT);
            }

            TextView textView = (TextView) (passedSpinner.getSelectedView());
            Drawable drawable = ContextCompat.getDrawable(this, R.drawable.dropdown_arrow);
            textView.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, drawable, null);
        }else{
            //This code is ran if the API level is 23 or more

            if (passedValidValue == false){
                passedSpinner.setBackgroundResource(R.drawable.spinner_background_red);
            }else{
                passedSpinner.setBackgroundResource(R.drawable.spinner_background_26dp);
            }
        }
    }

    //Remove background and add dropdown arrow for spinners when running on API 22 or lower
    private void setStandardSpinner(Spinner passedSpinner){
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            //This code is ran if the API level is than 23 / Marshmallow
            passedSpinner.setBackgroundColor(Color.TRANSPARENT);
            TextView textView = (TextView) (passedSpinner.getSelectedView());
            Drawable drawable = ContextCompat.getDrawable(SetupPage.this, R.drawable.dropdown_arrow);
            textView.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, drawable, null);
        }
    }

    //This method is responsible for checking if the value in the dropdown boxes are valid and if they aren't calling the method to change there background
    //so that they are highlighted red, if the values are valid then it also checks if a game session already exists for the current board, and if it does
    //gives a user the option of starting a new game or continuing the game
    private void submitBtnFunc(Spinner passedBoardDropdown, Spinner passedTeam1Dropdown, Spinner passedTeam2Dropdown){
        Button submitBtn = findViewById(R.id.setupPgStartBtn);

        Float heightAdjustValue = TextScale.getFontAdjustHeightValue();
        int padding = (int) ((14 * heightAdjustValue) * DeviceSize.getDeviceDensity());
        submitBtn.setPadding(padding, padding, padding, padding);

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Boolean valuesValid = true;

                String boardDropdownVal = passedBoardDropdown.getSelectedItem().toString();
                String[]dropdownValuesArray = {passedTeam1Dropdown.getSelectedItem().toString(), passedTeam2Dropdown.getSelectedItem().toString()};

                //Checks that the value selected in the board dropdown isn't the default value
                if (boardDropdownVal.equals("Select Board")){
                    valuesValid = false;
                    setSpinnerBackground(false, passedBoardDropdown);
                }else{
                    setSpinnerBackground(true, passedBoardDropdown);
                }

                //Checks that the value selected in the team 1 dropdown isn't the default value
                if (dropdownValuesArray[0].equals("Select Team")){
                    valuesValid = false;
                    setSpinnerBackground(false, passedTeam1Dropdown);
                }else{
                    setSpinnerBackground(true, passedTeam1Dropdown);
                }

                //Checks that the value selected in the team 2 dropdown isn't the default value
                if (dropdownValuesArray[1].equals("Select Team")){
                    valuesValid = false;
                    setSpinnerBackground(false, passedTeam2Dropdown);
                }else{
                    setSpinnerBackground(true, passedTeam2Dropdown);
                }

                //Checks that the value in the team 1 dropdown isn't the same as the selected value in the team 2 dropdown
                //this shouldn't occur as when an item is selected in one dropdown it should be removed from the other
                //however, this if statement should catch the error if it some how happens
                if (valuesValid == true){
                    if (dropdownValuesArray[0].equals(dropdownValuesArray[1])){
                        valuesValid = false;
                        setSpinnerBackground(false, passedTeam1Dropdown);
                        setSpinnerBackground(false, passedTeam2Dropdown);
                        Log.e("teamSelectionError", "The selected colour is the same for each team!");
                    }
                }

                //Checks if the valuesValid boolean is still true, if it is still true at this point
                //then the values selected in the dropdown boxes are valid and the activity should
                //change to the next page
                if (valuesValid == true){
                    Log.e("checkedDropdownValues", "---------- All of the dropdown values are valid ----------");
                    List<String> selectedTeamsIdArray = dbHelper.getTeamIdByName(dropdownValuesArray);
                    //Gets the id of the board that was selected in the board dropdown
                    String selectedBoardId = dbHelper.getBoardIdByName(boardDropdownVal);

                    //Checks if the last game session for the selected board ended with a team reaching the end
                    Boolean existingGameCompleted = dbHelper.checkGameWin(selectedBoardId);

                    Log.e("gameSessionExists", "Game Session Exists = " + existingGameCompleted);

                    if (existingGameCompleted == false){
                        AlertDialog.Builder builder = new AlertDialog.Builder(SetupPage.this);

                        builder.setMessage("An unfinished game already exists for the board, do you want to start a new game for this board?");
                        builder.setTitle("Unfinished Game Exist");

                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.e("createNewGameSession", "A new game session should be created");
                                dbHelper.deleteGameSession(selectedBoardId);
                                dbHelper.setInitialGameSessionValues(selectedTeamsIdArray, selectedBoardId);
                                changeToBoardPg(selectedBoardId);
                                //Calls the method that will create the questions arrays and store them in the gameBoards table
                                quest.createQuestionOrder(dbHelper, selectedBoardId);
                            }
                        });

                        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.e("continueGameSession", "The old game session should be continued");
                                changeToBoardPg(selectedBoardId);

                                //Gets the questionOrderCount Blob from the gameBoards table, this is needed to that it can
                                //be checked if it is null, as if a new question is added or deleted then it will be null
                                byte[] oneDSerialisedData = dbHelper.getBoardQuestionCountOrder(selectedBoardId);

                                if (oneDSerialisedData == null){
                                    //Calls the method that will create the questions arrays and store them in the gameBoards table
                                    quest.createQuestionOrder(dbHelper, selectedBoardId);
                                }else {
                                    //Calls the method that will load the questions array from the gameBoards table
                                    quest.loadQuestionOrder(dbHelper, selectedBoardId);
                                }
                            }
                        });

                        // Create and show the AlertDialog
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }else{
                        //This runs if a board of the given boardId doesn't already exist in the gameSession table
                        dbHelper.setInitialGameSessionValues(selectedTeamsIdArray, selectedBoardId);
                        changeToBoardPg(selectedBoardId);
                        quest.createQuestionOrder(dbHelper, selectedBoardId);
                    }
                }
            }
        });
    }

    //This method setups the board spinner adding the boards that a read from the database to the spinner and then setting up the
    //onclick listener that will change the drawable that is visible depending on the board that is selected in the dropdown
    private void boardSpinner(Spinner passedBoardDropdown){
        //Calls the method that will read the team names from the database and stores them in the teamNames list
        List<String> boardNames = dbHelper.getGameBoardNames();
        boardNames.add(0, "Select Board");

        //Creates the ArrayAdapter that is to be used in the board spinner
        ArrayAdapter<String> boardAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, boardNames);
        //Binds the ArrayAdapter to the board spinner
        passedBoardDropdown.setAdapter(boardAdapter);

        //Sets a listener for the board spinner
        passedBoardDropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            //This call back method is triggered when an item is selected in the board spinner
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (isUserInput == true) {
                    String selectedBoard = passedBoardDropdown.getSelectedItem().toString();
                    if (!"Select Board".equals(selectedBoard)) {
                        //Gets the name that should be used to find the boards image resource
                        String boardImgName = (DBHelper.getBoardImgStatic(selectedBoard, dbHelper.getReadableDatabase())) + "_icon";

                        int boardImgDrawableId = 0;
                        try{
                            //Gets the integer ID of the board image resource
                            boardImgDrawableId = getResources().getIdentifier(boardImgName, "drawable", getPackageName());
                        }catch (Exception e){
                            Log.e("getBoardImgDrawable", "Error locating the board image drawable");
                        }

                        if (boardImgDrawableId != 0) {
                            //Get a reference to the board image ImageView
                            ImageView boardImgObj = findViewById(R.id.setupPgBoardImg);
                            boardImgObj.setImageResource(boardImgDrawableId);

                            int numRows = 12;
                            int numColumns = 13;

                            //Gets the reference to the setup pages constraint layout
                            ConstraintLayout constraintLayout = findViewById(R.id.setupConstraintLayout);

                            //Creates a new ConstraintSet object so that the current state of the Constraint layout can be stored in it
                            ConstraintSet constraintSet = new ConstraintSet();
                            //Clones the current state of the ConstraintLayout and stores it in the constraintSet Object
                            constraintSet.clone(constraintLayout);

                            //Calculates the aspect ratio based on the number of columns and rows
                            float aspectRatio = ((float) numColumns / (float) numRows);
                            //Sets the aspect ratio for the ImageView using the setDimensionRatio() method
                            //The "H," prefix indicates that the ratio is based on the height of the view, this needs
                            //to be done so that the height is set in relation to the width of the board icon
                            constraintSet.setDimensionRatio(boardImgObj.getId(), "H," + aspectRatio);

                            //Applies the modified ConstraintSet to the ConstraintLayout to update the layouts aspect ratio
                            constraintSet.applyTo(constraintLayout);
                        }else{
                            Log.e("ErrFindingBoardDrawable", "Unable to find a game board image drawable of the specified file name");
                        }
                    }

                    //This calls the method the sets the look of the spinner when running on API level 22 or less
                    //If the API level is 23 or more then this method will do nothing
                    setStandardSpinner(passedBoardDropdown);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Nothing
            }
        });
    }

    //This method is responsible for populating and controlling the values of the team dropdown spinners
    private void teamSpinners(Spinner passedTeam1Dropdown, Spinner passedTeam2Dropdown){
        ArrayList<String> spinnerTeamNames = new ArrayList<String>();
        spinnerTeamNames.add("Select Team");
        //Calls the method that will read the team names from the database and stores them in the teamNames list
        List<String> teamNames = dbHelper.getAllTeamNames();

        //Need to have a separate list for each of the spinners so that when the spinners values are edited it will only effect one spinner and not both of them
        ArrayList<String> team1Options = new ArrayList<String>();
        //Adds the Select Team option to the beginning of the spinner so that it is the default value that is selected in the spinner
        team1Options.add("Select Team");
        for (int i = 0; i < teamNames.size(); i++){
            //Adds the team names that we're read from the database to the spinners list
            team1Options.add((teamNames.get(i)).toString());
        }

        ArrayList<String> team2Options = new ArrayList<String>();
        team2Options.add("Select Team");
        for (int i = 0; i < teamNames.size(); i++){
            team2Options.add((teamNames.get(i)).toString());
        }

        //Creates the ArrayAdapter that is to be used in the team 1 spinner
        ArrayAdapter<String> team1Adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, team1Options);
        //Binds the ArrayAdapter to the team 1 spinner
        passedTeam1Dropdown.setAdapter(team1Adapter);

        ArrayAdapter<String> team2Adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, team2Options);
        passedTeam2Dropdown.setAdapter(team2Adapter);


        //Sets a listener for the team 1 spinner
        passedTeam1Dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            //This call back method is triggered when an item is selected in the team 1 spinner
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (isUserInput == true) {
                    String selectedTeam = passedTeam1Dropdown.getSelectedItem().toString();
                    if (!"Select Team".equals(selectedTeam)) {
                        //This gets the position of the value that is current selected in the other spinner
                        int currentSelectionPos = passedTeam2Dropdown.getSelectedItemPosition();
                        //This gets the position of the value that is going to be removed from the other spinner
                        int selectItemPosOfOtherSpinner = team2Adapter.getPosition(selectedTeam);

                        //Sets isUserInput to false so that the changes to the spinner don't cause the onItemSelected code
                        //to run again as a result of the spinners contents changing, with out this flag the code would get stuck in a loop
                        isUserInput = false;
                        try {
                            //Removes the item that was selected from the other spinner so that the spinners can have the same team selected
                            team2Adapter.remove(selectedTeam);
                        }catch (Exception e){
                            isUserInput = true;
                            Log.e("removingValFromSpinner", "Unable to remove the select value from other spinner");
                        }

                        //This if statement is responsible for adjusting the index position of the item that is selected in the spinner
                        //as if a value is removed from the spinner then the value that is in the current index position could change
                        //due to the items list of the spinner changing size
                        if (currentSelectionPos > selectItemPosOfOtherSpinner) {    //This is true is the item that is going to be remove is before
                            //the currently selected item in the list

                            //This line adjust the index position to account for earlier value being remove from the list
                            updateSpinnerSelection(passedTeam2Dropdown, currentSelectionPos - 1);
                        } else if (currentSelectionPos <= selectItemPosOfOtherSpinner) {
                            //This line doesn't adjust the index position as the value that has been removed is after it in the list
                            //However the updateSpinnerSelection method still needs to be called so that the user input is set to false
                            //so that the changes to the spinner won't trigger the code in the inItemSelected method
                            updateSpinnerSelection(passedTeam2Dropdown, currentSelectionPos);
                        }else{
                            Log.e("UpdateTeamSpinnerError", "The team spinner is empty");
                        }
                    }

                    //This if statement if responsible for adding items back to the other spinner
                    //e.g. if the spinner value is changed from Red to Blue then Red is added back to the other
                    //spinner as it can now be used by the other team
                    if (team1DropdownPrevVal != null && !team1DropdownPrevVal.equals("Select Team")){      //Checks that a previous value does exist and isn't the default value
                        //Sets isUserInput to false so that adding a value to the spinner doesn't trigger the code in the onItemSelected method
                        isUserInput = false;
                        //Adds the previous value to the other spinner so that it can now be selected
                        team2Adapter.add(team1DropdownPrevVal);
                        isUserInput = true;
                    }
                    //Stores the new value of the spinner so that is can be used later if the value of the spinner is changed
                    team1DropdownPrevVal = selectedTeam;

                    //This calls the method the sets the look of the spinner when running on API level 22 or less
                    //If the API level is 23 or more then this method will do nothing
                    setStandardSpinner(passedTeam1Dropdown);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        });

        //Sets a listener for the team 2 spinner
        passedTeam2Dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (isUserInput == true) {
                    String selectedTeam = passedTeam2Dropdown.getSelectedItem().toString();
                    if (!"Select Team".equals(selectedTeam)) {
                        //This gets the position of the value that is current selected in the other spinner
                        int currentSelectionPos = passedTeam1Dropdown.getSelectedItemPosition();
                        //This gets the position of the value that is going to be removed from the other spinner
                        int selectItemPosOfOtherSpinner = team1Adapter.getPosition(selectedTeam);

                        isUserInput = false;
                        try {
                            team1Adapter.remove(selectedTeam);
                        }catch (Exception e){
                            isUserInput = true;
                            Log.e("removingValFromSpinner", "Unable to remove the select value from other spinner");
                        }
                        if (currentSelectionPos > selectItemPosOfOtherSpinner) {
                            updateSpinnerSelection(passedTeam1Dropdown, currentSelectionPos - 1);
                        } else if (currentSelectionPos <= selectItemPosOfOtherSpinner) {
                            updateSpinnerSelection(passedTeam1Dropdown, currentSelectionPos);
                        }else{
                            Log.e("UpdateTeamSpinnerError", "The team spinner is empty");
                        }
                    }

                    //This if statement if responsible for adding items back to the other spinner
                    //e.g. if the spinner value is changed from Red to Blue then Red is added back to the other
                    //spinner as it can now be used by the other team
                    if (team2DropdownPrevVal != null && !team2DropdownPrevVal.equals("Select Team")){      //Checks that a previous value does exist and isn't the default value
                        //Sets isUserInput to false so that adding a value to the spinner doesn't trigger the code in the onItemSelected method
                        isUserInput = false;
                        //Adds the previous value to the other spinner so that it can now be selected
                        team1Adapter.add(team2DropdownPrevVal);
                        isUserInput = true;
                    }
                    //Stores the new value of the spinner so that is can be used later if the value of the spinner is changed
                    team2DropdownPrevVal = selectedTeam;

                    //This calls the method the sets the look of the spinner when running on API level 22 or less
                    //If the API level is 23 or more then this method will do nothing
                    setStandardSpinner(passedTeam2Dropdown);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        });
    }

    //This method is used to adjust the index position of the spinners to ensure that the correct item
    //is selected when the spinners items are changed
    private void updateSpinnerSelection(Spinner spinner, int selectedIndex) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                //Sets isUserInput to false so that the changes to the spinner don't trigger code inside onItemSelected that should one be trigger be a users input
                isUserInput = false;
                //Removes adjust the index location of the spinners value to account for a value been removed from the list
                spinner.setSelection(selectedIndex);
            }
        });

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //This is called after a delay so that the changes to the spinner can be carried out before setting is back to true
                //If their is no delay then changes made to the spinner will not be implemented before the isUserInput is set back to true
                //meaning that the code inside the onItemSelected method will be ran buy inputs not as a result of a user selecting an option
                isUserInput = true;
                //This calls the method the sets the look of the spinner when running on API level 22 or less
                //If the API level is 23 or more then this method will do nothing
                //This needs to be called as this code will run as a result of the spinners item index positions changing
                //and if the spinner index positions have been changed then the background of the spinner will have been changed
                //back to the default and as a result will need setting back to the standard
                setStandardSpinner(spinner);
            }
        }, 100);
    }
}
