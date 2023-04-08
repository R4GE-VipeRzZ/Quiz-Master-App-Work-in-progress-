package net.r4geviperzz.questionmaster;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.style.TypefaceSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.os.Handler;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SetupPage extends AppCompatActivity {
    //Instantiates an instance of the DBHelper class so that data can be read from the database
    private DBHelper dbHelper = new DBHelper(SetupPage.this);
    private Question quest = new Question();
    private Boolean isUserInput = false;
    private Float heightAdjustValue;
    private Float widthAdjustValue;
    private Float density;
    private int spinnerTextSize;
    //This List stores the items that should be currently available in a spinner, this list is needed so
    //that if a new team is added that spinner will only contain valid values upon its creation
    private List<String> spinnerAvailableItems = new ArrayList<>();
    private LinearLayout parentTeamLinLayout;
    //This class list stores all of the child linearlayout for each team
    private List<LinearLayout> childTeamLinLayoutsList = new ArrayList<>();
    //This class list stores all of the Lists that are uses by each spinners adapter
    private List<List<String>> listOfSpinnerLists = new ArrayList<>();
    //This class list stores all of the spinners that are used on the page for each team
    private List<Spinner> teamSpinnersList = new ArrayList<>();
    //This class list stores the ID that is assign to each spinner, this is needed so that the
    //onItemSelected method can detect which spinner has been selected
    private List<Integer> spinnerTeamIds = new ArrayList<>();
    //This class list stores the ID that is assign to each remove team button, this is needed so
    //that the onClick listener can detect which remove team button has been clicked
    private List<Integer> removeTeamBtnIds = new ArrayList<>();
    private int numRequiredTeams = 2;
    private String previousSpinnerItem = null;
    private Handler handler = new Handler();
    private Boolean addNewTeamBtnEnabled = true;
    private Boolean removeTeamBtnEnabled = true;
    //This is the offset value used to adjust the location of the spinner arrow when running on API 22 and lower
    private int spinnerArrowOffset;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_page);

        //Sets the spinner arrow offset that should be used when the app is running on API 22 and lower
        spinnerArrowOffset = -96;

        //Gets a reference to the ScrollView that the parentLinLayout is contained within
        ScrollView parentLinLayoutScrollView = findViewById(R.id.setupPgParentLinearLayoutScrollView);
        //Hides the scrollbar from the scrollview, this can't be done in XML as it
        //causes an error when it is done in xml when running on API 19
        parentLinLayoutScrollView.setVerticalScrollBarEnabled(false);

        heightAdjustValue = TextScale.getFontAdjustHeightValue();
        widthAdjustValue = TextScale.getFontAdjustWidthValue();
        density = DeviceSize.getDeviceDensity();

        List<String> teamNamesList = new ArrayList<String>();
        //Calls the method that will read the team names from the database and stores them in the teamNames list
        teamNamesList = dbHelper.getAllTeamNames();
        teamNamesList.add(0, "Select Team");

        //Stores all of the teams names along with the Select Team option in the spinnerAvailableItems list
        spinnerAvailableItems = teamNamesList;

        //Gets a reference to the linearlayout that stores all of the teams on the setup page
        LinearLayout teamLinLayoutParent = findViewById(R.id.setupPgParentLinearLayout);
        parentTeamLinLayout = teamLinLayoutParent;

        //Sets the text size of the pages spinners
        spinnerTextSize = (int) (19 * heightAdjustValue);

        //Get a reference to the board spinner
        Spinner boardDropdown = findViewById(R.id.setupPgBoardDropdown);

        //Sets up the board spinner so that its onClickListener will work
        boardSpinner(boardDropdown);
        //Sets up the submit button so that its onClickListener will work
        submitBtnFunc(boardDropdown);

        //The for loop is adds the team Linearlayouts to the page depending on the minimum number of teams
        for (int x = 0; x < numRequiredTeams; x++){
            //Calls the method that will create the childLinearLayout for the team, passing false as they shouldn't
            //have a remove button as they are required teams
            addTeamToPage(false, (x + 1));
        }

        Button addAnotherTeamBtn = findViewById(R.id.setupPgAddAnotherTeamBtn);

        addAnotherTeamBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (addNewTeamBtnEnabled == true) {
                    addNewTeamBtnEnabled = false;
                    //Calls the handler so that the button won't be set back to enabled until 300 milliseconds has elapsed
                    //doing this means that a new team can only be added every 300 milliseconds
                    handler.postDelayed(addNewTeamBtnRunnable, 300);
                    //Gets the current number of teams on the page
                    int currentNumTeams = childTeamLinLayoutsList.size();

                    //Checks that the current number of teams is less than the max number of teams
                    if (currentNumTeams < dbHelper.getNumTeams()) {
                        //Calculates the available team name
                        int newTeamNumber = (currentNumTeams + 1);
                        //Calls the method that will create a childLinearLayout for a new team
                        addTeamToPage(true, newTeamNumber);
                    } else {
                        //Runs if the max number of teams has been reached
                        Toast.makeText(SetupPage.this, "The Max Number Of Teams Has Been Reached", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }


    //This method is called to change the activity to the boardPage activity
    private void changeToBoardPg(String selectedBoardId){
        Intent intent = new Intent(SetupPage.this, BoardPage.class);
        //Passes the id of the board that was selected to the BoardPage class
        intent.putExtra("boardId", selectedBoardId);
        startActivity(intent);
    }

    //This method is responsible for setting the background images on the spinners
    private void setBackgroundForSpinner(Boolean passedValidValue, Spinner passedSpinner, int arrowOffset){
        if (passedValidValue == true){
            //Runs for normal background
            //Calls the method that will set the background for the spinner
            CustomSpinnerStyle.setSpinnerBackground(SetupPage.this, passedSpinner, heightAdjustValue, arrowOffset, false);
        }else{
            //Runs for a background with a red outline
            //Calls the method that will set the background for the spinner
            CustomSpinnerStyle.setSpinnerBackground(SetupPage.this, passedSpinner, heightAdjustValue, arrowOffset, true);
        }
    }

    //This method creates a new game session
    private void createNewGameSession(List<String> passedSelectedTeamIds, String passedSelectedBoardId){
        dbHelper.setInitialGameSessionValues(passedSelectedTeamIds, passedSelectedBoardId);
        changeToBoardPg(passedSelectedBoardId);
        //Calls the method that will create the questions arrays and store them in the gameBoards table
        quest.createQuestionOrder(dbHelper, passedSelectedBoardId);
    }

    //This method gets all of the teams that have been selected in the team dropdowns and returns them in a single list
    private List<String> getSelectedTeams(){
        List<String> selectedTeamsList = new ArrayList<>();

        for (int x = 0; x < teamSpinnersList.size(); x++){
            Spinner tempTeamSpinner = teamSpinnersList.get(x);
            selectedTeamsList.add(tempTeamSpinner.getSelectedItem().toString());
        }

        return selectedTeamsList;
    }

    //This method checks if all of the selected teams are valid
    private Boolean checkSelectedTeams(List<String> passedSelectedTeamList){
        Boolean validTeams = true;

        //Iterates through all of the elements in the passed teams list
        for (int i = 0; i < passedSelectedTeamList.size(); i++){
            //Checks that none of the teams values are Select Team
            if (passedSelectedTeamList.get(i).equals("Select Team")){
                //Sets the boolean to false so that the app won't proceed to the game board
                validTeams = false;
                //Sets the background of the spinner to a background with a red outline
                setBackgroundForSpinner(false, teamSpinnersList.get(i), spinnerArrowOffset);
            }else{
                //Sets the background to the default background with no red outline
                setBackgroundForSpinner(true, teamSpinnersList.get(i), spinnerArrowOffset);
            }
        }

        //Creates an empty hash set to keep track of the unique elements
        Set<String> setWithoutDuplicates = new HashSet<>();
        //Checks that there are no duplicate values in the passed teams list
        for (int x = 0; x < passedSelectedTeamList.size(); x++){
            String tempSelectedItem = passedSelectedTeamList.get(x);
            //Checks that the select item isn't Select Team as that will have already been checked for in the previous for loop
            if (!tempSelectedItem.equals("Select Team")) {
                //Tries to add the team in the passedSelectedTeamList, if an
                //identical element is already in the hash set then false will be returned
                if (setWithoutDuplicates.add(tempSelectedItem) != true) {
                    //Sets the boolean to false so that the app won't proceed to the game board
                    validTeams = false;
                    //Sets the background of the spinner to a background with a red outline
                    setBackgroundForSpinner(false, teamSpinnersList.get(x), spinnerArrowOffset);
                }
            }
        }

        return validTeams;
    }

    //This method is responsible for checking if the value in the dropdown boxes are valid and if they aren't calling the method to change there background
    //so that they are highlighted red, if the values are valid then it also checks if a game session already exists for the current board, and if it does
    //gives a user the option of starting a new game or continuing the game
    private void submitBtnFunc(Spinner passedBoardDropdown){
        Button submitBtn = findViewById(R.id.setupPgStartBtn);

        Float heightAdjustValue = TextScale.getFontAdjustHeightValue();
        int padding = (int) ((14 * heightAdjustValue) * DeviceSize.getDeviceDensity());
        submitBtn.setPadding(padding, padding, padding, padding);

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Boolean valuesValid = true;

                //Gets the value that is selected in the board dropdown
                String boardDropdownVal = passedBoardDropdown.getSelectedItem().toString();

                //Checks that the value selected in the board dropdown isn't the default value
                if (boardDropdownVal.equals("Select Board")){
                    valuesValid = false;
                    setBackgroundForSpinner(false, passedBoardDropdown, spinnerArrowOffset);
                }else{
                    setBackgroundForSpinner(true, passedBoardDropdown, spinnerArrowOffset);
                }

                if (valuesValid == true) {
                    //Gets the id of the board that was selected in the board dropdown
                    String selectedBoardId = dbHelper.getBoardIdByName(boardDropdownVal);

                    //Checks if the last game session for the selected board ended with a team reaching the end
                    Boolean existingGameCompleted = dbHelper.checkGameWin(selectedBoardId);

                    //Calls the method that will return a list with all of the selected team names in it
                    List<String> selectedTeamDropdownNames = getSelectedTeams();
                    //Gets the ID values of all the teams that were selected in the teams dropdowns
                    List<String> selectedTeamIds = dbHelper.getTeamIdsByName(selectedTeamDropdownNames);

                    if (existingGameCompleted == false) {
                        // Create the dialog
                        CustomQuestionDialog dialog;
                        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
                            //This runs when on API 19 or lower
                            dialog = new CustomQuestionDialog(SetupPage.this, R.style.MyDialogThemeAPI19);
                        } else {
                            //This runs when running on API 21 or higher

                            //Checks if running on API 22 or less
                            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP_MR1) {
                                dialog = new CustomQuestionDialog(SetupPage.this, R.style.MyDialogThemeAPI21And22);
                            } else {
                                //Runs if running on API 23 and up
                                dialog = new CustomQuestionDialog(SetupPage.this);
                            }
                        }

                        if (dialog != null) {
                            Window dialogWindow = dialog.getWindow();

                            if (dialogWindow != null) {
                                // Inflate the dialog layout
                                LayoutInflater inflater = LayoutInflater.from(SetupPage.this);
                                View dialogView = inflater.inflate(R.layout.alert_dialog_layout, null);

                                //Set the content view of the dialog
                                dialog.setContentView(dialogView);

                                //Alert dialog title
                                TextView titleLabel = dialogView.findViewById(R.id.alertDialogTitle);
                                //Alert dialog message
                                TextView messageLabel = dialogView.findViewById(R.id.alertDialogMessage);

                                titleLabel.setText("Unfinished Game Exists");
                                messageLabel.setText("An unfinished game already exists for the board, do you want to start a new game for this board?");

                                //Setup Yes button
                                Button alertBtnYes = dialogView.findViewById(R.id.alertDialogBtnYes);
                                alertBtnYes.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        //Yes Click
                                        dialog.dismiss();

                                        //Calls the method that will check that all of the selected teams are valid
                                        Boolean teamsValid = checkSelectedTeams(selectedTeamDropdownNames);

                                        if (teamsValid == true){
                                            //Calls the method that will delete the existing game session for the board from the database
                                            dbHelper.deleteGameSession(selectedBoardId);
                                            createNewGameSession(selectedTeamIds, selectedBoardId);
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
                                        ///Changes the activity
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


                                dialogWindow.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, (int) (DeviceSize.getDeviceHeightPX() * 0.288));
                                // Show the dialog
                                dialog.show();
                            } else {
                                Log.e("alertDialogWindowError", "Failed to get the window off the alert dialog");
                            }
                        } else {
                            Log.e("alertDialogError", "Failed to create alert dialog");
                        }
                    } else {
                        //Calls the method that will check that all of the selected teams are valid
                        Boolean teamsValid = checkSelectedTeams(selectedTeamDropdownNames);

                        if (teamsValid == true) {
                            //This runs if a board of the given boardId doesn't already exist in the gameSession table
                            createNewGameSession(selectedTeamIds, selectedBoardId);
                        }
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
        passedBoardDropdown.setPadding((int) ((9 * widthAdjustValue) * density),0,0,0);

        //Sets up the board dropdown spinner
        CustomSpinnerStyle.setSpinnerStyle(SetupPage.this, passedBoardDropdown, heightAdjustValue, boardNames, (int) ((120 * widthAdjustValue) * density), spinnerTextSize, 22, spinnerArrowOffset);

        passedBoardDropdown.setOnTouchListener(new View.OnTouchListener() {
            @Override
            //This method runs when a user taps on a spinner
            public boolean onTouch(View v, MotionEvent event) {
                //Only runs the code on the ACTION_DOWN event as the action down event only occurs once at the start of a press,
                //this means that if a user holds there finger down on the spinner then the code won't run multiple times as another
                //ACTION_DOWN event won't be triggered until they lift there finger and tap again
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    isUserInput = true;
                }
                return false;
            }
        });

        //Sets a listener for the board spinner
        passedBoardDropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            //This call back method is triggered when an item is selected in the board spinner
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (isUserInput == true) {
                    isUserInput = false;

                    String selectedBoard = passedBoardDropdown.getSelectedItem().toString();

                    int numRows = 12;
                    int numColumns = 13;

                    //Calculates the aspect ratio based on the number of columns and rows
                    float aspectRatio = ((float) numColumns / (float) numRows);

                    //Gets a reference to the ImageView that displays the boards icon
                    ImageView boardImgObj = findViewById(R.id.setupPgBoardImg);

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
                            boardImgObj.setImageResource(boardImgDrawableId);

                            //Gets the reference to the setup pages constraint layout
                            ConstraintLayout constraintLayout = findViewById(R.id.setupConstraintLayout);

                            //Creates a new ConstraintSet object so that the current state of the Constraint layout can be stored in it
                            ConstraintSet constraintSet = new ConstraintSet();
                            //Clones the current state of the ConstraintLayout and stores it in the constraintSet Object
                            constraintSet.clone(constraintLayout);

                            //Sets the aspect ratio for the ImageView using the setDimensionRatio() method
                            //The "H," prefix indicates that the ratio is based on the height of the view, this needs
                            //to be done so that the height is set in relation to the width of the board icon
                            constraintSet.setDimensionRatio(boardImgObj.getId(), "H," + aspectRatio);

                            //Applies the modified ConstraintSet to the ConstraintLayout to update the layouts aspect ratio
                            constraintSet.applyTo(constraintLayout);
                        }else{
                            Log.e("ErrFindingBoardDrawable", "Unable to find a game board image drawable of the specified file name");
                        }
                    }else{
                        //Sets the previous image to the blank board icon as the item selected in the drop down is select board
                        boardImgObj.setImageResource(R.drawable.blank_board_icon);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Nothing
            }
        });
    }

    //This method renumbers the TextViews for the required teams when a team is remove from the page
    private void renumberTeamLabels(int passedStartIndex){
        //This for loop iterates through the linearlayouts that are in the childTeamLinLayoutsList, starting at the passedStartIndex
        //the passedStartIndex is the index location where the remove button was removed from, the TextView before this don't need
        //renaming as they are already in order and aren't effect by a LinearLayout been removed below them
        for (int z = passedStartIndex; z < childTeamLinLayoutsList.size(); z ++){
            //This for loop iterates through all the children of the childLinearLayout in order to find its TextView
            for (int i = 0; i < childTeamLinLayoutsList.get(z).getChildCount(); i++){
                View child = childTeamLinLayoutsList.get(z).getChildAt(i);
                //Checks if the child is a TextView
                if (child instanceof TextView){
                    //Changes the text in the text view to the new value using the z value
                    TextView teamLabel = (TextView) child;
                    teamLabel.setText("Team " + (z + 1));
                    break;
                }
            }
        }
    }

    //This method remove a linearlayout containing a label and a spinner for a team that is in list for the given index location
    private void removeTeamFromPage(int passedTeamIndex){
        //Gets the parent of the child LinearLayout for the team
        ViewGroup parentLayout = (ViewGroup) childTeamLinLayoutsList.get(passedTeamIndex).getParent();
        //Removes the child LinearLayout from the parent so that it is no longer displayed
        parentLayout.removeView(childTeamLinLayoutsList.get(passedTeamIndex));

        //Gets the spinner that is going to be deleted so that its selected item can be read
        Spinner toBeDeletedSpinner = teamSpinnersList.get(passedTeamIndex);
        //Gets the selected item of the spinner that is going to be deleted as if the selection
        //isn't Select Team then the item needs to be added back to all the other spinners
        String toBeDeletedSpinnerSelection = toBeDeletedSpinner.getSelectedItem().toString();
        Log.e("deletedSpinnerItem", "Delete Spinner Item = " + toBeDeletedSpinnerSelection);

        if (!toBeDeletedSpinnerSelection.equals("Select Team")){
            //Calls the method that will add the item that is selected in the spinner that is going to be deleted
            //back to all the other spinners, an index location of -1 is passed as this item should be added back
            //to all of the spinners, so setting the excluded index to -1 means it will add the item to all the spinners
            addItemBackToOtherSpinners(-1, toBeDeletedSpinnerSelection);
        }

        //Removes the spinner from the teamSpinnersList
        teamSpinnersList.remove(passedTeamIndex);
        //Removes the list that is used by the spinners adapter from the listOfSpinnersLists
        listOfSpinnerLists.remove(passedTeamIndex);
        //Removes the spinners ID from the spinnerTeamIds list
        spinnerTeamIds.remove(passedTeamIndex);
        //Removes the child LinearLayout from the list as it is no longer part of the layout
        childTeamLinLayoutsList.remove(passedTeamIndex);
        Toast.makeText(SetupPage.this, "Team Has Been Removed", Toast.LENGTH_SHORT).show();
    }

    //This method adds the item that is no longer selected in the spinner back to the rest of the spinners
    private void addItemBackToOtherSpinners(int excludedSpinnerIndex, String itemToAddBack){
        //Removes the element in the first index as it should be the Selected Team item,
        //it needs removing as the Selected Team item should always be the first element in the List
        spinnerAvailableItems.remove(0);
        //Adds the item back to the spinnerAvailableItems List as it should now be available for selection again
        spinnerAvailableItems.add(itemToAddBack);
        //Orders the spinnerAvailableItems List in alphabetical order
        Collections.sort(spinnerAvailableItems);
        //Adds the Select Team item back to the start of the spinnerAvailableItems List
        spinnerAvailableItems.add(0, "Select Team");

        for (int i = 0; i < teamSpinnersList.size(); i++){
            if (i != excludedSpinnerIndex){
                //Gets the the spinner
                Spinner currentSpinner = teamSpinnersList.get(i);
                //Gets the item that is currently selected in the spinner
                String currentSelectedItem = currentSpinner.getSelectedItem().toString();

                //Gets the spinners List from the listOfSpinnerLists
                List<String> currentSpinnerList = listOfSpinnerLists.get(i);
                //Adds the item back to the List
                currentSpinnerList.add(itemToAddBack);
                //Removes the first item that should be Select Team from the List as that should always be at the start of the List
                currentSpinnerList.remove(0);
                //Orders the new List in alphabetical order
                Collections.sort(currentSpinnerList);
                //Adds Select Team back to the start of the List
                currentSpinnerList.add(0, "Select Team");

                //Gets the index location of the selected item as adding a new item to the
                //list may have changed the index location of the selected item
                int selectedItemIndex = currentSpinnerList.indexOf(currentSelectedItem);
                //Sets the selected item to the selectedItemIndex position so that the selected item
                //in the spinner remains the same even after adding an item to the list
                currentSpinner.setSelection(selectedItemIndex);

                //Gets the spinners adapter
                BaseAdapter currentAdapter = (BaseAdapter) currentSpinner.getAdapter();
                //Notifies the spinners adapter that its data has now changed due to the List been changed
                currentAdapter.notifyDataSetChanged();
            }
        }
    }

    //This method removes the item that has been selected in one spinner from the rest of the spinners
    private void removeItemFromOtherSpinners(String passedSelectedItem, int excludedSpinnerIndex){
        //Gets the index location of the passedSelectedItem in the spinnerAvailableItems List
        int availableItemIndex = spinnerAvailableItems.indexOf(passedSelectedItem);
        //This line removes the passedSelectedItem from the List as it is now selected
        //meaning that it shouldn't be an option for any newly added spinners
        spinnerAvailableItems.remove(availableItemIndex);

        for (int i = 0; i < teamSpinnersList.size(); i++){
            if (i != excludedSpinnerIndex){
                //Gets the spinner
                Spinner currentSpinner = teamSpinnersList.get(i);
                //Gets the item that is currently selected in the spinner
                String currentSelectedItem = currentSpinner.getSelectedItem().toString();

                //Gets the spinners List from the listOfSpinnerLists
                List<String> currentSpinnerList = listOfSpinnerLists.get(i);
                //Gets the index location of the item that needs to be removed from the List
                int indexOfItemToRemove = currentSpinnerList.indexOf(passedSelectedItem);
                //Removes the selected item from the List
                currentSpinnerList.remove(indexOfItemToRemove);
                //Updates the listOfSpinnerLists with the updated List
                listOfSpinnerLists.set(i, currentSpinnerList);

                //Gets the index location of the selected item as removing an item from the
                //list may have changed the index location of the selected item
                int selectedItemIndex = currentSpinnerList.indexOf(currentSelectedItem);
                //Sets the selected item to the selectedItemIndex position so that the selected item
                //in the spinner remains the same even after removing an item to the list
                currentSpinner.setSelection(selectedItemIndex);

                //Gets the spinners adapter
                BaseAdapter currentAdapter = (BaseAdapter) currentSpinner.getAdapter();
                //Notifies the spinners adapter that its data has now changed due to the List been changed
                currentAdapter.notifyDataSetChanged();
            }
        }
    }

    //This method adds a linearlayout containing a label and a spinner for a team to the parent linearlayout of the setup page
    private void addTeamToPage(Boolean passedRemovableTeam, int passedTeamNumber){
        //Creates a new LinearLayout and sets it orientation
        LinearLayout childLinLayout = new LinearLayout(SetupPage.this);
        //Sets the padding on the childLinLayout so that there is space between the teams in the linearlayout

        LinearLayout.LayoutParams childLinLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                (int) ((23 * widthAdjustValue) * density)
        );
        childLinLayoutParams.setMargins(0, (int) ((16 * heightAdjustValue) * density), 0, 0);
        childLinLayout.setLayoutParams(childLinLayoutParams);

        //Creates the team label
        TextView teamTextView = new TextView(SetupPage.this);
        teamTextView.setText("Team " + Integer.toString(passedTeamNumber));
        teamTextView.setTextSize((int) (23 * heightAdjustValue));
        teamTextView.setTypeface(null, Typeface.BOLD);
        teamTextView.setTextColor(Color.BLACK);
        teamTextView.setGravity(Gravity.CENTER_VERTICAL);


        //Set the teamTextView weight
        LinearLayout.LayoutParams textViewParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );

        if (passedRemovableTeam == true) {
            textViewParams.weight = 0.35f;
        }else{
            textViewParams.weight = 0.42f;
        }
        textViewParams.setMargins(0,(int) ((-10 * widthAdjustValue) * density),0,(int) ((-10 * widthAdjustValue) * density));
        teamTextView.setLayoutParams(textViewParams);



        //Creates the team spinner
        Spinner teamSpinner = new Spinner(SetupPage.this);
        teamSpinner.setPadding((int) ((9 * widthAdjustValue) * density),0,0,0);
        //Creates a new list that will be used for the spinner that is populated with the current available values
        List<String> teamSpinnerList = new ArrayList<>(spinnerAvailableItems);
        CustomSpinnerStyle.setSpinnerStyle(SetupPage.this, teamSpinner, heightAdjustValue, teamSpinnerList, (int) ((125 * widthAdjustValue) * density), spinnerTextSize, 22, spinnerArrowOffset);
        //Adds the spinner to the teamSpinnersList list so that the spinners items can be read and changed later
        teamSpinnersList.add(teamSpinner);
        //Adds the spinner lists to the listOfSpinnerLists
        listOfSpinnerLists.add(teamSpinnerList);

        //Generates a unique id for the spinner
        int spinnerId = BtnIdGenerator.generateBtnId();
        //Sets assigns the unique id to the spinner
        teamSpinner.setId(spinnerId);
        //Stores the spinners id in the spinnerTeamIds list so that it can be detected which spinner has been clicked in the OnSelectedItem method
        spinnerTeamIds.add(spinnerId);

        //Set the teamSpinner weight
        LinearLayout.LayoutParams spinnerParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );
        spinnerParams.weight = 0.25f;

        if (passedRemovableTeam == true) {
            spinnerParams.setMargins((int) ((2 * widthAdjustValue) * density), 0, (int) ((16 * widthAdjustValue) * density), 0);
        } else {
            spinnerParams.setMargins((int) ((2 * widthAdjustValue) * density), 0, (int) ((40 * widthAdjustValue) * density), 0);
        }

        teamSpinner.setLayoutParams(spinnerParams);

        teamSpinner.setOnTouchListener(new View.OnTouchListener() {
            @Override
            //This method runs when a user taps on a spinner
            public boolean onTouch(View v, MotionEvent event) {
                //Only runs the code on the ACTION_DOWN event as the action down event only occurs once at the start of a press,
                //this means that if a user holds there finger down on the spinner then the code won't run multiple times as another
                //ACTION_DOWN event won't be triggered until they lift there finger and tap again
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    isUserInput = true;
                    previousSpinnerItem = teamSpinner.getSelectedItem().toString();
                }
                return false;
            }
        });

        teamSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (isUserInput == true) {
                    isUserInput = false;
                    //Gets the is of the selected spinner
                    int selectedSpinnerId = parent.getId();
                    //Gets the index location of the spinner in the spinnerTeamIds List
                    //so it can be calculated which spinner has been pressed
                    int spinnerIndex = spinnerTeamIds.indexOf(selectedSpinnerId);

                    //Gets the item that was selected in the spinner
                    String selectedItem = parent.getItemAtPosition(position).toString();

                    //Checks that the selected item isn't Select Team as if it is the selected item
                    //shouldn't be removed as Select Team should always be an option
                    if (!selectedItem.equals("Select Team")) {
                        //Calls the method that will remove the selected item from the rest of the spinners
                        //meaning that none of the spinner could end up with the same value except for Select Team
                        removeItemFromOtherSpinners(selectedItem, spinnerIndex);
                    }

                    //Checks that the previous selected item isn't Select Team as if it is
                    //then it doesn't need adding back to the spinners as it was never removed
                    if (!previousSpinnerItem.equals("Select Team")){
                        //Calls the method that will add the previous selected item back to the rest of the spinners,
                        //as if the item is no longer selected in the spinner then it should be available to all spinners
                        addItemBackToOtherSpinners(spinnerIndex, previousSpinnerItem);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        Button teamRemoveBtn = null;
        if (passedRemovableTeam == true) {
            //Creates the remove team button
            teamRemoveBtn = new Button(SetupPage.this);
            teamRemoveBtn.setTextSize(16);
            teamRemoveBtn.setBackgroundResource(R.drawable.remove_team_btn_background);
            int btnId = BtnIdGenerator.generateBtnId();
            teamRemoveBtn.setId(btnId);
            removeTeamBtnIds.add(btnId);

            int parentHeight = childLinLayout.getHeight();
            Log.e("parentHeight", Integer.toString(parentHeight));

            //Set the teamRemoveBtn weight
            LinearLayout.LayoutParams removeBtnParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT
            );
            removeBtnParams.weight = 0.52f;
            teamRemoveBtn.setLayoutParams(removeBtnParams);

            teamRemoveBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (removeTeamBtnEnabled == true) {
                        removeTeamBtnEnabled = false;
                        handler.postDelayed(removeTeamBtnRunnable, 300);

                        int clickedBtnId = v.getId();
                        //Gets the index location of the buttons id within the removeTeamBtnIds list
                        int teamToRemove = (removeTeamBtnIds.indexOf(clickedBtnId)) + numRequiredTeams;

                        //Checks that the buttons id was found in the list
                        if (teamToRemove >= 0) {
                            //Calls the method that will remove the teams childLinLayout from the layout
                            removeTeamFromPage(teamToRemove);
                            //Removes the button id from the list, the number of required teams has to be taken off the index value as
                            //the required teams don't have a remove button meaning there is not button for them in the removeTeamBtnsIds list
                            removeTeamBtnIds.remove(teamToRemove - numRequiredTeams);
                            renumberTeamLabels(teamToRemove);
                        } else {
                            Log.e("removeTeamBtnIdError", "There is no remove team button of the provided ID in the removeTeamBtnIds list");
                        }
                    }
                }
            });
        }

        //Adds the TextView and Spinner to the parent LinearLayout
        childLinLayout.addView(teamTextView);
        childLinLayout.addView(teamSpinner);
        if (passedRemovableTeam == true) {
            childLinLayout.addView(teamRemoveBtn);
        }

        //Adds the childLinLayout to the main parent LinearLayout
        parentTeamLinLayout.addView(childLinLayout);

        //Stores a reference to the childLinLayout in the childTeamLinLayoutsList
        childTeamLinLayoutsList.add(childLinLayout);
    }

    private Runnable addNewTeamBtnRunnable = new Runnable() {
        @Override
        public void run() {
            addNewTeamBtnEnabled = true;
        }
    };

    private Runnable removeTeamBtnRunnable = new Runnable() {
        @Override
        public void run() {
            removeTeamBtnEnabled = true;
        }
    };
}
