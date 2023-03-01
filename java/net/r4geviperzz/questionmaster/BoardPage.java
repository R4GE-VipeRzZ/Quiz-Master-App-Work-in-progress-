package net.r4geviperzz.questionmaster;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.UUID;


public class BoardPage extends Activity {
    private DBHelper dbHelper = new DBHelper(BoardPage.this);
    private Question quest = new Question();
    private GridLayout gridLayout;
    private int numColumns = 13;
    private int numRows = 12;
    private int cellSize;      //Used to store the pixel size of the grids row and columns / cell size
    private String idOfBoard = null;
    private int posWinPosition = 0;
    private String[] teamIdsArray;
    private String[] teamNamesArray;
    private ImageView[] teamCountersImgViewArray;
    private int numOfTeams = 0;
    private int currentTeamIndex = 0;
    private String winningTeam = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board_page);

        // Get the screen width
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        //This gets the boardId that was passed from the SetupPage as a result of a user selecting a board in the dropdown on that page
        idOfBoard = getIntent().getStringExtra("boardId");
        //This gets the value of the winning position for the given board
        posWinPosition = dbHelper.getBoardTotalSpaces(idOfBoard);

        //Gets the ids of the teams that have been selected for the board on the current session an stores them in the teamIdsArray
        teamIdsArray = (dbHelper.getTeamIdsFromSession(idOfBoard)).toArray(new String[0]);
        //Gets the names of the teams that have been selected for the board on the current session an stores them in the teamNamesArray
        teamNamesArray = (dbHelper.getTeamNamesForSession(teamIdsArray)).toArray(new String[0]);
        //This stores the number of teams that are playing
        numOfTeams = teamIdsArray.length;

        Button askQuestionBtn = findViewById(R.id.askQuestionBtn);

        askQuestionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (winningTeam == null) {
                    //Calls the method that will display the dialog with the question and answers,
                    //and will then call the appropriate methods to move the board counters
                    askQuestion();
                }
            }
        });

        gridLayout = findViewById(R.id.boardPgGrid);
        gridLayout.setColumnCount(numColumns);
        gridLayout.setRowCount(numRows);

        // Gets the screen density
        float density = getResources().getDisplayMetrics().density;

        //Gets the screen width
        int screenWidth = displayMetrics.widthPixels;

        // Calculate the margin size in pixels (10dp)
        int marginSize = (int) (10 * density);

        // Subtract the total margin width from the screen width
        int availableWidth = screenWidth - (2 * marginSize);

        // Set the width of each cell to fit the screen
        cellSize = availableWidth / numColumns;

        // Create the checkerboard pattern
        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numColumns; j++) {
                TextView cell = new TextView(this);
                cell.setWidth(cellSize);
                cell.setHeight(cellSize);
                cell.setGravity(Gravity.CENTER);
                gridLayout.addView(cell);
            }
        }

        // Set the margins of the grid layout
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) gridLayout.getLayoutParams();
        layoutParams.setMargins(marginSize, marginSize, marginSize, marginSize);
        gridLayout.setLayoutParams(layoutParams);

        setupPlayerCounter(cellSize);


        // Create the image view for the drawable
        ImageView playBoardImg = new ImageView(this);

        // Load the drawable
        String boardDrawableName = dbHelper.getBoardImgById(idOfBoard);
        int boardImgResId = getResources().getIdentifier(boardDrawableName, "drawable", getPackageName());
        Drawable playBoardDrawable= getResources().getDrawable(boardImgResId);

        // Set the new drawable on the ImageView
        playBoardImg.setImageDrawable(playBoardDrawable);

        // Create the layout parameters for the image view
        GridLayout.LayoutParams playBoardParams = new GridLayout.LayoutParams();
        playBoardParams.height = (cellSize * numRows);
        playBoardParams.width = (cellSize * numColumns);
        playBoardParams.rowSpec = GridLayout.spec(0, numRows);
        playBoardParams.columnSpec = GridLayout.spec(0, numColumns);

        //Sets the elevation of the board image to a negative to ensure that it is always behind all other views in the grid
        ViewCompat.setElevation(playBoardImg, -1);
        // Add the image view to the grid
        gridLayout.addView(playBoardImg, playBoardParams);
    }
    //This method is responsible for running code when the back button is pressed
    @Override
    public void onBackPressed(){
        //Gets the team id that corresponds to the currentTeamIndex
        String teamIdToPass = teamIdsArray[currentTeamIndex];

        //Sets the teamToAksNext value for the specified team id in the gameSession table to 1,
        //this is so that if the game session is reloaded then it will ask the correct team the next question
        dbHelper.setTeamToAskNext(teamIdToPass, idOfBoard);
        //This is called so that the index positions of how far into a question colour set a user is, is stored
        //in the questionOrderCount field of the gameBoards table
        quest.saveQuestionCountOrder();
        super.onBackPressed();
    }

    //This method is responsible for setting up the counters and label text when a game session is started
    private void setupPlayerCounter(int passedCellSize){
        //Used to get the image names for each of the teams
        String [] teamDrawableNamesArray = (dbHelper.getTeamImgByIds(teamIdsArray)).toArray(new String[0]);
        teamCountersImgViewArray = new ImageView[teamDrawableNamesArray.length];

        for (int j = 0; j < teamIdsArray.length; j++){
            //Creates the imageView
            ImageView imageView = new ImageView(this);
            //Sets an id for the imageView
            int generatedId = (UUID.randomUUID()).hashCode();
            Log.e("MyApp", "Generated ID: " + generatedId);
            imageView.setId(generatedId);

            int counterDrawableResId = getResources().getIdentifier(teamDrawableNamesArray[j], "drawable", getPackageName());
            // Set the new drawable on the ImageView
            imageView.setImageResource(counterDrawableResId);

            // Add the image view to the grid
            gridLayout.addView(imageView);
            //Stores the imageView of the counter in the class array so that the references to the counters can be used elsewhere
            teamCountersImgViewArray[j] = imageView;
        }

        //Gets the id of the team that should be asked the next question, this is needed so that the correct value can be set for currentTeamIndex
        int idOfTeamToAskNext = dbHelper.getIdOfTeamToAskNext(idOfBoard);

        //Sets the currentTeamIndex value by finding the index position that the team of the given id is at in the teamIdsArray
        for (int n =0; n < teamIdsArray.length;n++){
            if (idOfTeamToAskNext == Integer.parseInt(teamIdsArray[n])){
                currentTeamIndex = n;
                break;
            }
        }

        //This line is used get all the currentPos values off all the teams in the session
        //The values are need for all of the teams so that it can be determined if more than one team is in the same position
        List<String> countersPosArray = dbHelper.getCurrentPosByTeamId(teamIdsArray, idOfBoard);
        int counterPosTotal = Integer.parseInt(countersPosArray.get(currentTeamIndex));

        Boolean newGameStarted = true;

        for (int z = 0; z < countersPosArray.size(); z++){
            //Checks that it hasn't already been determined that one or more of the counters aren't at the start position
            //meaning that the session that has been loaded isn't a new game
            if (newGameStarted == true){
                //Checks if the counter is at the start position
                if (Integer.parseInt(countersPosArray.get(z)) != 1){
                    newGameStarted = false;
                }
            }else{
                break;
            }
        }

        //This is statement checks if it is an new game session that has been started or if an old one is been loaded
        if (newGameStarted == true){
            moveCounter(0, counterPosTotal, countersPosArray);
        }else{
            //Stores the value of the currentTeamIndex as it will need to be changed back to its original value after the for loop
            int storedCurrentTeamIndex = currentTeamIndex;
            //This for loop positions each of the teams counters in the game session on the grid
            for(int k = 0; k < countersPosArray.size(); k++){
                //Sets the currentTeamIndex to the index value of the counter that is going to be position on the grid by the moveCounter method
                //If the currentTeamIndex value wasn't updated here then the duplicate detection method wouldn't work
                currentTeamIndex = k;
                //Gets the position value of the team counter that is been positioned in the grid
                counterPosTotal = Integer.parseInt(countersPosArray.get(k));
                //Calls the method to position the counter in the grid
                moveCounter(0,counterPosTotal, countersPosArray);
            }
            //Sets the currentTeamIndex to the origional value that it had before the for loop
            currentTeamIndex = storedCurrentTeamIndex;
        }

        TextView currentTeamTextView = findViewById(R.id.currentTeamLabel);
        currentTeamTextView.setText("The team to be asked the \nnext question is: " + teamNamesArray[currentTeamIndex]);
    }

    //This method is responsible for reading the position values of the teams from the database, and then calling
    //the methods responsible for moving the counter along with changing the label text
    private void checkWinAndMove(int passedPrevCounterPos){
        TextView currentTeamTextView = findViewById(R.id.currentTeamLabel);

        //This code is used get all the currentPos values off all the teams in the session
        //The values are need for all of the teams so that it can be determined if more than one team is in the same position
        List<String> countersPosArray = dbHelper.getCurrentPosByTeamId(teamIdsArray, idOfBoard);
        int counterPosTotal = Integer.parseInt(countersPosArray.get(currentTeamIndex));

        int nextTeamIndex = 0;

        if (currentTeamIndex < (numOfTeams - 1)){
            nextTeamIndex = currentTeamIndex + 1;
        }else{
            nextTeamIndex = 0;
        }

        //Checks if a user has won
        if (counterPosTotal < posWinPosition) {
            //Calls the method that moves the counter to grid position that corresponds to the position value for the board
            moveCounter(passedPrevCounterPos, counterPosTotal, countersPosArray);
            //Changes the labels text so that the user know what team will be asked a question next
            currentTeamTextView.setText("The team to be asked the \nnext question is: " + teamNamesArray[nextTeamIndex]);
        } else {
            //Calls the method that moves the counter to the winning position on the board
            moveCounter(passedPrevCounterPos,counterPosTotal, countersPosArray);
            //Gets the name of the winning team
            winningTeam = teamNamesArray[currentTeamIndex];
            Log.e("Win", winningTeam + " Team has won");
            //Changes the label text to let the user know who has won
            currentTeamTextView.setText(winningTeam + " Team has won");
        }

        currentTeamIndex = nextTeamIndex;
    }

    //This method is responsible for changing the background images of the incorrect/left and correct/right buttons in the question dialog window
    private int changeDialogLeftRightBtnBg(Dialog passedDialog, int passedBtnClickedVal, int passedLeftBtnId, int passedRightBtnId){
        Button leftBtn = passedDialog.findViewById(passedLeftBtnId);
        Button rightBtn = passedDialog.findViewById(passedRightBtnId);

        //This variable is returned so that the buttons clicked status can be update as after this method has been executed the buttons
        //as the button will have changed from clicked to un-clicked or the other way around
        int newBtnClickedVal = -1;

        //This if statement checks if right button is currently in the clicked mode
        if(passedBtnClickedVal == 1){
            //This runs if the right button is in the clicked mode
            leftBtn.setBackgroundResource(R.drawable.incorrect_btn_background_clicked);
            rightBtn.setBackgroundResource(R.drawable.correct_btn_background);
            newBtnClickedVal = 0;
        }else{
            //This runs if the left button is in the clicked mode
            leftBtn.setBackgroundResource(R.drawable.incorrect_btn_background);
            rightBtn.setBackgroundResource(R.drawable.correct_btn_background_clicked);
            newBtnClickedVal = 1;
        }

        return newBtnClickedVal;
    }

    //This method changes the counters position value depending on the number of answers a user has got correct
    private void updatePosValue(int passedCounterPosVal, int passedNumAnsCorrect){
        if (passedCounterPosVal < posWinPosition) {
            //This if statement checks if a user has got any answers correct, if they haven't then the counterPosVal doesn't need updating
            if(passedNumAnsCorrect != 0) {
                int newPos = passedCounterPosVal + passedNumAnsCorrect;

                //Stops the new position value been larger than the max value for the board
                if (newPos >= posWinPosition) {
                    newPos = posWinPosition;
                }
                Log.e("numAnswersCorrect", "The number of answers correct is = " + passedNumAnsCorrect);
                dbHelper.setCurrentPosByTeamId(idOfBoard, teamIdsArray[currentTeamIndex], newPos);
            }

            //Calls the method that will check if the position is the winning position and will call the appropriate methods to move the counters
            //passes the position value before it was increased so that it can be check if there was other ImageViews in the counters previous position
            //and then if there was resize the ImageViews to take into account that a counter has been removed
            checkWinAndMove(passedCounterPosVal);
        }
    }

    //This method is responsible for asking the user the question
    private void askQuestion(){
        // Create the dialog
        Dialog dialog = new Dialog(BoardPage.this);

        //Gets the current counter position of the player counter
        List<String> countersPosArray = dbHelper.getCurrentPosByTeamId(teamIdsArray, idOfBoard);
        int counterPosTotal = Integer.parseInt(countersPosArray.get(currentTeamIndex));

        //Gets the card colour for the counters current board position
        String cardColour = dbHelper.getPosCardColour(counterPosTotal, idOfBoard);

        List<String> returnedQuestionAndAnsList = quest.getQuestionAndAnswers(cardColour);
        String questionString = returnedQuestionAndAnsList.remove(0);
        String[] ansArray = returnedQuestionAndAnsList.toArray(new String[0]);

        // Inflate the dialog layout
        LayoutInflater inflater = LayoutInflater.from(BoardPage.this);
        View dialogView = inflater.inflate(R.layout.question_dialog_layout, null);

        //Set the content view of the dialog
        dialog.setContentView(dialogView);

        // Get references to the views in the layout
        TextView questionTextView = dialogView.findViewById(R.id.questionTextView);
        LinearLayout ansLinLayout = dialogView.findViewById(R.id.ansLinLayout);

        // Set the question text
        questionTextView.setText(questionString);

        //This list is used to keep track of which button is in the clicked mode / which answers have been selected as correct
        List<Integer> ansCorrectList = new ArrayList<>();
        //This list is used to store all the ids of the left/incorrect buttons so that they can be reference later on
        List<Integer> leftBtnIdList = new ArrayList<>();
        //This list is used to store all the ids of the right/correct buttons so that they can be reference later on
        List<Integer> rightBtnIdList = new ArrayList<>();

        // Add the answer TextViews and buttons to the answers layout
        for (String answer : ansArray) {
            //Adds the initial value of 0 to the ansCorrectList as the left/incorrect button is set to be clicked when the dialog is first loaded
            ansCorrectList.add(0);

            // Create a horizontal LinearLayout to hold the answer TextView and buttons
            LinearLayout answerLayout = new LinearLayout(BoardPage.this);
            answerLayout.setOrientation(LinearLayout.HORIZONTAL);

            // Create the answer TextView and set its properties
            TextView answerTextView = new TextView(BoardPage.this);
            answerTextView.setText(answer);
            answerTextView.setGravity(Gravity.CENTER_HORIZONTAL);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1
            );
            answerTextView.setLayoutParams(layoutParams);

            // Get the screen width
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

            int screenHeight = displayMetrics.heightPixels;

            //This int is used for setting with height and width of the left/incorrect and right/correct buttons
            int dialogBtnsWidth = screenHeight / 20;
            //This int is used for setting the padding of the left/incorrect and right/correct buttons
            int buttonPadding = dialogBtnsWidth / 6;

            // Create the left button and set its properties
            Button leftBtn = new Button(BoardPage.this);
            leftBtn.setBackgroundResource(R.drawable.incorrect_btn_background_clicked);

            //These lines of code generate an id for the button, store the generated id in the leftBtnIdList
            //and then set the id of the button to the generated id
            int leftBtnGenId = (UUID.randomUUID()).hashCode();
            leftBtnIdList.add(leftBtnGenId);
            leftBtn.setId(leftBtnGenId);

            //These lines of code set the height and width of the button, along with its padding
            LinearLayout.LayoutParams buttonLayoutParams = new LinearLayout.LayoutParams(dialogBtnsWidth, dialogBtnsWidth);
            buttonLayoutParams.setMargins(buttonPadding, buttonPadding, buttonPadding, buttonPadding);
            leftBtn.setLayoutParams(buttonLayoutParams);

            // Create the right button and set its properties
            Button rightBtn = new Button(BoardPage.this);
            rightBtn.setBackgroundResource(R.drawable.correct_btn_background);

            //These lines of code generate an id for the button, store the generated id in the rightBtnIdList
            //and then set the id of the button to the generated id
            int rightBtnGenId = (UUID.randomUUID()).hashCode();
            rightBtnIdList.add(rightBtnGenId);
            rightBtn.setId(rightBtnGenId);

            //These lines of code set the height and width of the button, along with its padding
            rightBtn.setLayoutParams(buttonLayoutParams);
            rightBtn.setLayoutParams(buttonLayoutParams);

            // Attach an OnClickListener to the left button
            leftBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Gets a reference to the button that has been clicked
                    Button clickedBtn = (Button) v;
                    //Gets the id of the click button
                    int idOfClickedBtn = clickedBtn.getId();

                    int leftBtnIndexPos = -1;

                    for(int p = 0; p < leftBtnIdList.size(); p++){
                        if(leftBtnIdList.get(p) == idOfClickedBtn){
                            leftBtnIndexPos = p;
                            break;
                        }
                    }

                    //This store which button is currently in the clicked mode 0 for left and 1 for right
                    int btnClickedVal = ansCorrectList.get(leftBtnIndexPos);
                    //This calls the method that changes the left/incorrect and right/correct button backgrounds and then updates the ansCorrectList
                    ansCorrectList.set(leftBtnIndexPos, changeDialogLeftRightBtnBg(dialog, btnClickedVal,
                            leftBtnIdList.get(leftBtnIndexPos), rightBtnIdList.get(leftBtnIndexPos)));
                }
            });

            // Attach an OnClickListener to the right button
            rightBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Gets a reference to the button that has been clicked
                    Button clickedBtn = (Button) v;
                    //Gets the id of the click button
                    int idOfClickedBtn = clickedBtn.getId();

                    int rightBtnIndexPos = -1;

                    for(int n = 0; n < rightBtnIdList.size(); n++){
                        if(rightBtnIdList.get(n) == idOfClickedBtn){
                            rightBtnIndexPos = n;
                            break;
                        }
                    }

                    //This store which button is currently in the clicked mode 0 for left and 1 for right
                    int btnClickedVal = ansCorrectList.get(rightBtnIndexPos);
                    //This calls the method that changes the left/incorrect and right/correct button backgrounds and then updates the ansCorrectList
                    ansCorrectList.set(rightBtnIndexPos, changeDialogLeftRightBtnBg(dialog, btnClickedVal,
                            leftBtnIdList.get(rightBtnIndexPos), rightBtnIdList.get(rightBtnIndexPos)));
                }
            });

            // Add the answer TextView and buttons to the answer layout
            answerLayout.addView(leftBtn);
            answerLayout.addView(answerTextView);
            answerLayout.addView(rightBtn);

            // Add the answer layout to the answers layout
            ansLinLayout.addView(answerLayout);
        }


        // Set up the Submit button
        Button dialogSubmitButton = dialog.findViewById(R.id.submitBtn);
        dialogSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int numAnsCorrect = 0;

                for(int j =0; j < ansCorrectList.size(); j++){
                    if(ansCorrectList.get(j) == 1){
                        numAnsCorrect++;
                    }
                }
                //Calls the method that will update the position value in the database and will then
                //call the appropriate methods to move the counters
                updatePosValue(counterPosTotal, numAnsCorrect);
                dialog.dismiss();
            }
        });

        // Show the dialog
        dialog.show();
    }

    //This method is responsible for detecting if the counter that is been moved is going to end up in the same location
    //on the board as another counter(s)
    private List<Integer> checkForDuplicatePos(int passedCounterPosTotal, List<String> passedCountersPosArray){
        List<Integer> duplicatePosLocationIndexs = new ArrayList<Integer>();

        for(int indexVal = 0; indexVal < passedCountersPosArray.size(); indexVal++){
            //Stops the location value of the counter that is currently been moved from been checked as it shouldn't checked
            //as the current counter that is been moved should have the same value as passedCounterPosTotal as the passedCounterPosTotal
            //is the current position of the counter that is been moved
            if (indexVal != currentTeamIndex) {
                //Checks if there is already one or more counter(s) in the new position value location for the counter that is been moved
                if (Integer.parseInt(passedCountersPosArray.get(indexVal)) == passedCounterPosTotal) {
                    duplicatePosLocationIndexs.add(indexVal);
                }
            }
        }

        if (duplicatePosLocationIndexs.size() > 0) {
            List<String> teamCurrentlyInPosList = new ArrayList<String>();

            for (int k = 0; k < duplicatePosLocationIndexs.size(); k++) {
                teamCurrentlyInPosList.add(teamNamesArray[duplicatePosLocationIndexs.get(k)]);
            }

            String teamCurrentlyInPosString = String.join(", ", teamCurrentlyInPosList);
            Log.e("samePosIndexs", "The counter has moved to a position that the following team(s) are already in: " + teamCurrentlyInPosString);
        }

        return duplicatePosLocationIndexs;
    }

    //This method is responsible for resizing and positioning the ImageViews when a grid cell contains more than one ImageView
    private void resizeAndPosForMultiCounters(int passedCounterPosTotal, List<Integer> passedDuplicatePosIndexValues, Boolean moveIn){
        // Get the layout parameters of the counter
        GridLayout.LayoutParams params = (GridLayout.LayoutParams) teamCountersImgViewArray[currentTeamIndex].getLayoutParams();

        //This line is used to get the row and column values for the counterPosTotal according to the current board
        List<String> gridPositions = dbHelper.getPosGridLocations(passedCounterPosTotal, idOfBoard);
        int row = Integer.parseInt(gridPositions.get(1));
        int column = Integer.parseInt(gridPositions.get(0));

        int numCountersInPos = 0;
        int leftCountVal = 0;

        //Detects if the ImageView are been resized due to a new ImageView been added to the cell or an ImageView been removed from the cell
        if (moveIn == true) {
            //Gets the total number of ImageView that are in the grid cell, 1 is added on in order to take into account the counter that is been moved
            numCountersInPos = passedDuplicatePosIndexValues.size() + 1;
            //Starts at 1 as the first ImageView doesn't need any leftMargin and topMargin values as it need to be located in the top left corner
            leftCountVal = 1;
        }else{
            //Gets the total number of ImageView that are in the grid cell
            numCountersInPos = passedDuplicatePosIndexValues.size();
        }
        //Gets the square root of the total number of ImageViews and rounds up to the nearest whole number
        //This value is needed to know how many rows and columns should be setup using the leftMargin and topMargin image params
        int numRowsAndCols = (int)Math.ceil(Math.sqrt(numCountersInPos));
        //This value stores the new size that the counter should be in order for all of them to fit into the grid cell
        int newCounterSize = (cellSize / numRowsAndCols);

        // Update the row and column specs of the layout parameters
        params.rowSpec = GridLayout.spec(row, 1);
        params.columnSpec = GridLayout.spec(column, 1);
        //Sets the new size of the ImageView
        params.width = newCounterSize;
        params.height = newCounterSize;

        int topCountVal = 0;
        // Set the updated layout parameters on the ImageView
        teamCountersImgViewArray[currentTeamIndex].setLayoutParams(params);

        //This for loop is responsible for setting the individual leftMargin and topMargin values for each of the ImageViews
        //These values need to be set so that the ImageViews are position in a grid like manner with the grid cell
        for (int z = 0; z < passedDuplicatePosIndexValues.size(); z++) {
            //This if statement is used adjust the margin values so that a new row is started after the correct number of ImageViews have been positioned in a row
            if(leftCountVal >= numRowsAndCols){
                leftCountVal = 0;
                topCountVal++;
            }

            //Gets the index position of the ImageView in the teamCountersImgViewArray that needs to parameters editing
            int index = passedDuplicatePosIndexValues.get(z);

            //Creates a new Grid layout params instance and sets it to inherit the previous params named params
            GridLayout.LayoutParams multiCounterParamsMargin = new GridLayout.LayoutParams(params);

            //Adjusts the margin values of the multiCounterParamsMargin
            multiCounterParamsMargin.leftMargin = (newCounterSize * leftCountVal);
            multiCounterParamsMargin.topMargin = (newCounterSize * topCountVal);
            //Updates the ImageView to the new parameters
            teamCountersImgViewArray[index].setLayoutParams(multiCounterParamsMargin);

            leftCountVal++;
        }
    }

    //This method is responsible for moving the counter to the correct location on the board in relation to the pos value
    //that is has been passed
    private void moveCounter(int passedPrevCounterPos, int passedCounterPosTotal, List<String> passedCounterPosArray) {
        List<Integer> duplicatePosIndexValues = checkForDuplicatePos(passedCounterPosTotal, passedCounterPosArray);
        Log.e("duplicatePosIndexValues", duplicatePosIndexValues.toString());

        // Get the layout parameters of the counter
        GridLayout.LayoutParams params = (GridLayout.LayoutParams) teamCountersImgViewArray[currentTeamIndex].getLayoutParams();

        //Checks if the ImageView params has its width set to the same width as the cell, if it isn't then it means that the ImageView
        //params have previously been changed to make the ImageView fit into a grid cell with multiple other ImageViews/player counters
        //the params are set back to default values so that the size of the ImageView is back to how it would be when there is a single
        //ImageView/player counter in the grid cell
        if (params.width != cellSize){
            params.width = cellSize;
            params.height = cellSize;
            params.leftMargin = 0;
            params.topMargin = 0;

            if(passedPrevCounterPos != 0){
                List<Integer> stillExistingDuplicatePosIndexValues = checkForDuplicatePos(passedPrevCounterPos, passedCounterPosArray);

                if (stillExistingDuplicatePosIndexValues.size() > 0) {
                    int tempIndex = 0;
                    //This for loop is used to iterate through all the position values in the passedCounterPosArray
                    for(int n = 0; n < passedCounterPosArray.size(); n++){
                        //This if statement is used to get the index position of the first counter in the
                        //passCounterPosArray that has the same position value as previous counter location
                        if (passedPrevCounterPos == Integer.parseInt(passedCounterPosArray.get(n))){
                            tempIndex = n;
                            break;
                        }
                    }

                    //Checks if there is more than one ImageView still left in the grid cell
                    if (stillExistingDuplicatePosIndexValues.size() == 1){
                        //The section runs if there is only one ImageView left in the grid cell
                        //meaning that it can be returned back to its default params
                        // Get the layout parameters of the counter
                        GridLayout.LayoutParams tempParams = (GridLayout.LayoutParams) teamCountersImgViewArray[tempIndex].getLayoutParams();
                        tempParams.width = cellSize;
                        tempParams.height = cellSize;
                        tempParams.leftMargin = 0;
                        tempParams.topMargin = 0;
                    }else {
                        //This selection runs if there is more than one ImageView left in the grid cell
                        int tempCurrentTeamIndex = currentTeamIndex;
                        //Temporarily changes the currentTeamIndex array to the index of the ImageView that was first found in the passedCounterPosArray
                        currentTeamIndex = tempIndex;
                        resizeAndPosForMultiCounters(passedPrevCounterPos, stillExistingDuplicatePosIndexValues, false);
                        //Sets the currentTeamIndex back to the value it was at the start of this section
                        currentTeamIndex = tempCurrentTeamIndex;
                    }
                }
            }
        }

        //Checks if the duplicatePosIndexValues List<Integer> has any values in it
        //If it does have values then it means that there are one or more ImageViews in the new position for the counter
        if (duplicatePosIndexValues.size() > 0) {
            //Called so that all the ImageViews in the cell that the ImageView has been added to are resized and repositioned
            resizeAndPosForMultiCounters(passedCounterPosTotal, duplicatePosIndexValues, true);
        }else{
            //This section is ran if the new position is a cell in the grid that doesn't already have any ImageViews currently in it
            //This line is used to get the row and column values for the counterPosTotal according to the current board
            List<String> gridPositions = dbHelper.getPosGridLocations(passedCounterPosTotal, idOfBoard);
            int row = Integer.parseInt(gridPositions.get(1));
            int column = Integer.parseInt(gridPositions.get(0));

            // Update the row and column specs of the layout parameters
            params.rowSpec = GridLayout.spec(row, 1);
            params.columnSpec = GridLayout.spec(column, 1);

            // Set the updated layout parameters on the blue counter
            teamCountersImgViewArray[currentTeamIndex].setLayoutParams(params);
        }
    }
}

