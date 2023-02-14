package net.r4geviperzz.questionmaster;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "question_master.db";
    private static final Integer DB_VERSION = 1;
    private static final String TABLE_GAME_BOARDS = "gameBoards";
    private static final String COL_GAME_BOARDS_ID = "boardId";
    private static final String COL_GAME_BOARDS_TOTAL_SPACES = "totalSpaces";
    private static final String COL_GAME_BOARDS_TIME_LIMIT = "timeLimit";
    private static final String COL_GAME_BOARDS_BOARD_NAME = "boardName";
    private static final String COL_GAME_BOARDS_IMG_NAME = "boardImgName";
    private static final String TABLE_TEAMS = "teams";
    private static final String COL_TEAMS_ID = "teamId";
    private static final String COL_TEAMS_NAME = "teamName";
    private static final String COL_TEAMS_IMG_NAME = "counterImgName";
    private static final String TABLE_BOARD_POSITIONS = "boardPositions";
    private static final String COL_BOARD_POSITIONS_POS_ID = "posId";
    private static final String COL_BOARD_POSITIONS_GRID_X = "gridX";
    private static final String COL_BOARD_POSITIONS_GRID_Y = "gridY";
    private static final String COL_BOARD_POSITIONS_CARD_COLOUR = "cardColour";
    private static final String COL_BOARD_POSITIONS_SPECIAL = "special";

    private static final String TABLE_GAME_SESSION = "gameSession";
    private static final String COL_GAME_SESSION_QUESTIONS_ASKED = "questionsAsked";
    private static final String COL_GAME_SESSION_CORRECT_ANS = "correctAns";
    private static final String COL_GAME_SESSION_CURRENT_POS = "currentPos";
    private static final String TABLE_QUESTIONS = "questions";
    private static final String COL_QUESTIONS_ID = "questionId";
    private static final String COL_QUESTIONS_QUESTION = "question";
    private static final String COL_QUESTIONS_ANS_1 = "ans1";
    private static final String COL_QUESTIONS_ANS_2 = "ans2";
    private static final String COL_QUESTIONS_ANS_3 = "ans3";
    private static final String COL_QUESTIONS_ANS_4 = "ans4";
    private static final String COL_QUESTIONS_ANS_5 = "ans5";
    private static final String COL_QUESTIONS_ANS_6 = "ans6";
    private static final String COL_QUESTIONS_ANS_7 = "ans7";
    private static final String COL_QUESTIONS_ANS_8 = "ans8";
    private static final String COL_QUESTIONS_ANS_9 = "ans9";
    private static final String COL_QUESTIONS_ANS_10 = "ans10";
    private static final String TABLE_SETTINGS = "settings";
    private static final String COL_SETTINGS_SOUND_FX = "soundFx";

    //Constructor
    public DBHelper(@Nullable Context context){
        super(context, DB_NAME, null, DB_VERSION);  //Creates the database if it doesn't exist
    }

    //This method is called the fist time that the database is accessed
    //the purpose of this method is to setup the database if it already doesn't exist
    @Override
    public void onCreate(SQLiteDatabase db) {
        //Creates the settings table
        String createSettingsTable = "CREATE TABLE " + TABLE_SETTINGS + " (" + COL_SETTINGS_SOUND_FX + " BOOLEAN NOT NULL"+");";

        try {
            db.execSQL(createSettingsTable);
        }catch (Exception e){
            Log.e("createSettingsTable", "Unable to create settings table");
        }

        //Insert values into settings table
        String insertSettingsTable = "INSERT INTO " + TABLE_SETTINGS + "(" + COL_SETTINGS_SOUND_FX + ") VALUES (0);";

        try{
            db.execSQL(insertSettingsTable);
        }catch (Exception e){
            Log.e("insertSetings", "Unable to insert values into settings table");
        }

        //Creates the gameBoards table
        String createGameBoardsTable = "CREATE TABLE " + TABLE_GAME_BOARDS + " (" + COL_GAME_BOARDS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
                + COL_GAME_BOARDS_TOTAL_SPACES + " INTEGER NOT NULL, " + COL_GAME_BOARDS_TIME_LIMIT + " INTEGER NOT NULL, "
                + COL_GAME_BOARDS_BOARD_NAME + " VARCHAR(20) NOT NULL, " + COL_GAME_BOARDS_IMG_NAME + " VARCHAR(30) NOT NULL" +");";

        try {
            db.execSQL(createGameBoardsTable);
        }catch (Exception e){
            Log.e("createGameBoardTable", "Unable to create gameBoard table");
        }

        //Insert values into gameBoards table
        String insertGameBoardsTable = "INSERT INTO " + TABLE_GAME_BOARDS + "(" + COL_GAME_BOARDS_TOTAL_SPACES + ", "
                + COL_GAME_BOARDS_TIME_LIMIT + ", " + COL_GAME_BOARDS_BOARD_NAME + ", " + COL_GAME_BOARDS_IMG_NAME + ") VALUES (65, 60, \"Standard\", \"standard_board\");";

        try{
            db.execSQL(insertGameBoardsTable);
        }catch (Exception e){
            Log.e("insertGameBoards", "Unable to insert values into gameBoards table");
        }

        //Creates the teams table
        String createTeamsTable = "CREATE TABLE " + TABLE_TEAMS + " (" + COL_TEAMS_ID+ " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
                + COL_TEAMS_NAME + " VARCHAR(14) NOT NULL, " + COL_TEAMS_IMG_NAME + " VARCHAR(18)" +");";

        try {
            db.execSQL(createTeamsTable);
        }catch (Exception e){
            Log.e("createTeamsTable", "Unable to create teams table");
        }

        //Insert values into teams table
        String insertTeamsTable = "INSERT INTO " + TABLE_TEAMS + "(" + COL_TEAMS_NAME + ", " + COL_TEAMS_IMG_NAME + ") VALUES (\"Blue\", \"blue_icon\"),"
                + "(\"Red\", \"red_icon\")," +
                "(\"Yellow\", \"yellow_icon\")," +
                "(\"Green\", \"green_icon\")," +
                "(\"Orange\", \"orange_icon\")," +
                "(\"White\", \"white_icon\")," +
                "(\"Brown\", \"brown_icon\")," +
                "(\"Purple\", \"purple_icon\")," +
                "(\"Pink\", \"pink_icon\")," +
                "(\"Black\", \"black_icon\");";

        try{
            db.execSQL(insertTeamsTable);
        }catch (Exception e){
            Log.e("insertTeams", "Unable to insert values into teams table");
        }

        //Create the boardPositions table
        String createBoardPositions = "CREATE TABLE " + TABLE_BOARD_POSITIONS + " (" + COL_GAME_BOARDS_ID + " INTEGER NOT NULL, "
                + COL_BOARD_POSITIONS_POS_ID + " INTEGER NOT NULL, " + COL_BOARD_POSITIONS_GRID_X + " INTEGER NOT NULL, "
                + COL_BOARD_POSITIONS_GRID_Y + " INTEGER NOT NULL, " + COL_BOARD_POSITIONS_CARD_COLOUR + " INT, "
                + COL_BOARD_POSITIONS_SPECIAL + " INTEGER NOT NULL ,"
                + "PRIMARY KEY (" + COL_GAME_BOARDS_ID + ", " + COL_BOARD_POSITIONS_POS_ID + "),"
                + "FOREIGN KEY (" + COL_GAME_BOARDS_ID + ") REFERENCES " + TABLE_GAME_BOARDS + "(" + COL_GAME_BOARDS_ID + ")"+ ");";

        try{
            db.execSQL(createBoardPositions);
        }catch (Exception e){
            Log.e("createBoardPosTable", "Unable to create boardPositions table");
        }

        //Insert values into the boardPositions table

        //This array stores the id values of each board
        Integer[] boardIdsArray = {1};

        //This for loop is used to insert the board positions to the boardPositions table with the correct boardId
        for (int x = 0; x < boardIdsArray.length; x++){
            String[][] boardLocationVals;
            boardLocationVals = null;
            if (x == 0){    //This is true if the board that is been is the first in the boardIdsArray
                //Values {girdX, gridY, cardColour, Special}
                boardLocationVals = new String[][]{{"11","10","1","0"}, //This two dimensional string array contains all the boardPositions for the
                        {"11", "9", "0", "0"},                          //given board, excluding the boardId and posId
                        {"11", "8", "1", "0"},
                        {"11", "7", "0", "0"},
                        {"11", "6", "1", "0"},
                        {"11", "5", "0", "0"},
                        {"11", "4", "1", "0"},
                        {"11", "3", "0", "0"},
                        {"11", "2", "1", "0"},
                        {"11", "1", "1", "1"},
                        {"10", "1", "0", "0"},
                        {"9", "1", "1", "0"},
                        {"8", "1", "0", "0"},
                        {"7", "1", "1", "0"},
                        {"6", "1", "0", "0"},
                        {"5", "1", "1", "0"},
                        {"4", "1", "0", "0"},
                        {"3", "1", "1", "0"},
                        {"2", "1", "0", "0"},
                        {"1", "1", "1", "1"},
                        {"1", "2", "1", "0"},
                        {"1", "3", "0", "0"},
                        {"1", "4", "1", "0"},
                        {"1", "5", "0", "0"},
                        {"1", "6", "1", "0"},
                        {"1", "7", "0", "0"},
                        {"1", "8", "1", "0"},
                        {"1", "9", "0", "0"},
                        {"1", "10", "1", "1"},
                        {"2", "10", "1", "0"},
                        {"3", "10", "0", "0"},
                        {"4", "10", "1", "0"},
                        {"5", "10", "0", "0"},
                        {"6", "10", "1", "0"},
                        {"7", "10", "0", "0"},
                        {"8", "10", "1", "0"},
                        {"9", "10", "1", "-1"},
                        {"9", "9", "0", "0"},
                        {"9", "8", "1", "0"},
                        {"9", "7", "0", "0"},
                        {"9", "6", "1", "0"},
                        {"9", "5", "0", "0"},
                        {"9", "4", "1", "0"},
                        {"9", "3", "1", "-1"},
                        {"8", "3", "0", "0"},
                        {"7", "3", "1", "0"},
                        {"6", "3", "0", "0"},
                        {"5", "3", "1", "0"},
                        {"4", "3", "0", "0"},
                        {"3", "3", "1", "-1"},
                        {"3", "4", "1", "0"},
                        {"3", "5", "0", "0"},
                        {"3", "6", "1", "0"},
                        {"3", "7", "0", "0"},
                        {"3", "8", "1", "-1"},
                        {"4", "8", "1", "0"},
                        {"5", "8", "0", "0"},
                        {"6", "8", "1", "0"},
                        {"7", "8", "1", "-1"},
                        {"7", "7", "0", "0"},
                        {"7", "6", "1", "0"},
                        {"7", "5", "1", "-1"},
                        {"6", "5", "0", "0"},
                        {"5", "5", "1", "0"},
                        {"6", "5", "null","0"}};
            }

            //Creates the StringBuilder with first part of the insert statement added to it
            StringBuilder insertBoardPositionsTable = new StringBuilder("INSERT INTO " + TABLE_BOARD_POSITIONS + "(" + COL_GAME_BOARDS_ID
            + ", " + COL_BOARD_POSITIONS_POS_ID + ", " + COL_BOARD_POSITIONS_GRID_X + ", " + COL_BOARD_POSITIONS_GRID_Y
            + ", " + COL_BOARD_POSITIONS_CARD_COLOUR + ", " + COL_BOARD_POSITIONS_SPECIAL + ") VALUES");

            //This for loop is used to increment the posId by 1 for every row that is added to boardPositions table with the same boardId
            for (int i = 0; i < boardLocationVals.length; i++){
                //This concatenates the boardId, posId, girdX, gridY, cardColour and Special values into a single string for the values of the sql insert statement
                insertBoardPositionsTable.append("(").append(boardIdsArray[x]).append(", ").append((i + 1)).append(", ").append(boardLocationVals[i][0]).append(", ")
                        .append(boardLocationVals[i][1]).append(", ").append(boardLocationVals[i][2]).append(", ").append(boardLocationVals[i][3]).append(")");

                //Detects if it is the last row of position information for the given boardId
                if (i < (boardLocationVals.length - 1)){
                    insertBoardPositionsTable.append(",");  //Adds a comma if there is still more lines to add
                }else{
                    insertBoardPositionsTable.append(";");  //Adds a semicolon as this is the last line to be added for this given boardId
                }
            }

            try{
                //Executes the created insert statement for the given boardId
                db.execSQL(insertBoardPositionsTable.toString());
            }catch (Exception e){
                Log.e("insertBoardPositions", "Unable to insert values into the boardPositions table for the given boardId " + boardIdsArray[x].toString());
            }
        }

        //Create the gameSession table
        String createGameSession = "CREATE TABLE " + TABLE_GAME_SESSION + " (" + COL_TEAMS_ID + " INTEGER NOT NULL, "
                + COL_GAME_BOARDS_ID + " INTEGER NOT NULL, " + COL_GAME_SESSION_QUESTIONS_ASKED + " INTEGER NOT NULL, "
                + COL_GAME_SESSION_CORRECT_ANS + " INTEGER NOT NULL, " + COL_GAME_SESSION_CURRENT_POS + " INT NOT NULL, "
                + "PRIMARY KEY (" + COL_TEAMS_ID + ", " + COL_GAME_BOARDS_ID + "),"
                + "FOREIGN KEY (" + COL_TEAMS_ID + ") REFERENCES " + TABLE_TEAMS + "(" + COL_TEAMS_ID + "),"
                + "FOREIGN KEY (" + COL_GAME_BOARDS_ID + ") REFERENCES " + TABLE_GAME_BOARDS + "(" + COL_GAME_BOARDS_ID + ")"+ ");";

        try{
            db.execSQL(createGameSession);
        }catch (Exception e){
            Log.e("createGameSessionTable", "Unable to create gameSession table");
        }

        //Create the questions table
        String createQuestions = "CREATE TABLE " + TABLE_QUESTIONS + " (" + COL_BOARD_POSITIONS_CARD_COLOUR + " INTEGER NOT NULL, "
                + COL_QUESTIONS_ID + " INTEGER NOT NULL, " + COL_QUESTIONS_QUESTION + " VARCHAR(50) NOT NULL, "
                + COL_QUESTIONS_ANS_1 + " VARCHAR(30) NOT NULL, " + COL_QUESTIONS_ANS_2 + " VARCHAR(30) NOT NULL, "
                + COL_QUESTIONS_ANS_3 + " VARCHAR(30) NOT NULL, " + COL_QUESTIONS_ANS_4 + " VARCHAR(30) NOT NULL, "
                + COL_QUESTIONS_ANS_5 + " VARCHAR(30) NOT NULL, " + COL_QUESTIONS_ANS_6 + " VARCHAR(30) NOT NULL, "
                + COL_QUESTIONS_ANS_7 + " VARCHAR(30) NOT NULL, " + COL_QUESTIONS_ANS_8 + " VARCHAR(30) NOT NULL, "
                + COL_QUESTIONS_ANS_9 + " VARCHAR(30) NOT NULL, " + COL_QUESTIONS_ANS_10 + " VARCHAR(30) NOT NULL, "
                + "PRIMARY KEY (" + COL_BOARD_POSITIONS_CARD_COLOUR + ", " + COL_QUESTIONS_ID + "),"
                + "FOREIGN KEY (" + COL_BOARD_POSITIONS_CARD_COLOUR + ") REFERENCES " + TABLE_BOARD_POSITIONS + "(" + COL_BOARD_POSITIONS_CARD_COLOUR + ")"+ ");";

        try{
            db.execSQL(createQuestions);
        }catch (Exception e){
            Log.e("createQuestionsTable", "Unable to create questions table");
        }

        //Insert values into the boardPositions table

        //This array stores the values for the different types of questions
        //0 = Yellow, 1 = Purple
        Integer[] questionTypeArray = {0,1};

        //This for loop is used to insert the questions in to the questions table with the correct cardColour
        for (int j = 0; j < questionTypeArray.length; j++){
            String[][] questionVals;
            questionVals = null;
            if (j == 0){    //This is true if the questions been added are for the yellow colour
                //Values {question, ans1, ans2, ans3, ans4, ans5, ans6, ans7, ans8, ans9, ans10,}
                //This two dimensional string array contains the question and answers
                //for the questions table, excluding the cardColour and questionId
                questionVals = new String[][]{{"\"Popular Board Games\"","\"Backgammon\"","\"Chess\"","\"Cluedo\"","\"Draughts\"", "\"Guess Who\"", "\"Monopoly\"", "\"Pictionary\"", "\"Risk\"", "\"Scrabble\"", "\"Trivial Pursuit\""},
                        {"\"Things In A Toolbox\"","\"Adjustable spanner\"","\"Cordless drill\"","\"Flathead screwdriver\"", "\"Hacksaw\"", "\"Hammer\"", "\"Phillips screwdrivers\"","\"Pliers\"", "\"Socket set\"", "\"Spirit level\"", "\"Tape measure\""},
                        {"\"Things You Put On Your Head\"","\"Beret\"","\"Cap\"","\"Hair gel\"","\"Hair restorer\"", "\"Knotted handkerchief\"", "\"Scarf\"", "\"Sunglasses\"", "\"Top hat\"", "\"Turban\"", "\"Wig\""},
                        {"\"Things That Are Ridden\"","\"Bicycles\"","\"Buses\"","\"Camels\"","\"Elephants\"", "\"Horses\"", "\"Motor bikes\"", "\"Rollercoasters\"", "\"Scooters\"", "\"Tricycles\"", "\"Waves\""},
                        {"\"Household Appliances\"","\"Coffee maker\"","\"Dishwasher\"","\"Food processor\"","\"Fridge freezer\"", "\"Kettle\"", "\"Microwave\"", "\"Toaster\"", "\"Tumble dryer\"", "\"Vacuum cleaner\"", "\"Washing machine\""},
                        {"\"Types Of Shark\"","\"Basking\"","\"Blue\"","\"Bull\"","\"Great white\"", "\"Hammerhead\"", "\"Mako\"", "\"Nurse\"", "\"Reef\"", "\"Tiger\"", "\"Whale\""},
                        {"\"UK Theme Parks\"","\"Alton Towers\"","\"Blackpool Pleasure Beach\"","\"Chessington\"","\"Drayton Manor\"", "\"Flamingo Land\"", "\"Legoland Windsor\"", "\"Lightwater Valley\"", "\"Oakwood Theme Park\"", "\"Paultons Park\"", "\"Thorpe Park\""},
                        {"\"Batman Characters\"","\"Alfred\"","\"Catwoman\"","\"Commissioner Gordon\"","\"Joker\"", "\"Mr. Freeze\"", "\"Penguin\"", "\"Poison Ivy\"", "\"Riddler\"", "\"Robin\"", "\"Two-Face\""},
                        {"\"Things In A Pencil Case\"","\"Calculator\"","\"Coloured pencils\"","\"Compass\"","\"Crayons\"", "\"Eraser\"", "\"Felt-tip pens\"", "\"Pen\"", "\"Pencil\"", "\"Ruler\"", "\"Sharpener\""},
                        {"\"Types Of Boot\"","\"Car boots\"","\"Cowboy boots\"","\"Dr Martens boots\"","\"Football boots\"", "\"Riding boots\"", "\"Rugby boots\"", "\"Ski boots\"", "\"UGG boots\"", "\"Walking boots\"", "\"Wellington boots\""}};
            } else if (j == 1) {    //This is true if the questions been added are for the purple colour
                questionVals = new String[][]{{"\"Cricketing Nations\"","\"Afghanistan\"","\"Australia\"","\"Bangladesh\"","\"India\"", "\"New Zealand\"", "\"Pakistan\"", "\"South Africa\"", "\"Sri Lanka\"", "\"West Indies\"", "\"Zimbabwe\""},
                        {"\"Famous Robots\"","\"BB-8\"","\"Bender\"","\"C-3PO\"","\"Marvin\"", "\"Megatron\"", "\"Optimus Prime\"", "\"R2-D2\"", "\"RoboCop\"", "\"The Iron Giant\"", "\"WALL-E\""},
                        {"\"Ologies\"","\"Biology\"","\"Cardiology\"","\"Dermatology\"","\"Ecology\"", "\"Geology\"","\"Meteorology\"", "\"Mythology\"", "\"Neurology\"", "\"Psychology\"", "\"Zoology\""},
                        {"\"Dances\"","\"Bolero\"","\"Flamenco\"","\"Foxtrot\"","\"Jive\"", "\"Quickstep\"", "\"Rumba\"", "\"Salsa\"", "\"Samba\"", "\"Tango\"", "\"Waltz\""},
                        {"\"World Famous Bridges\"","\"Brooklyn Bridge\"","\"Clifton Suspension Bridge\"","\"Forth Bridge\"","\"Golden Gate Bridge\"", "\"Humber Bridge\"", "\"Millau Bridge\"", "\"Ponte Vecchio\"", "\"Rialto Bridge\"", "\"Sydney Harbour Bridge\"", "\"Tower Bridge\""},
                        {"\"Essential Camping Gear\"","\"Food\"","\"Knife\"","\"Matches\"","\"Sleeping bag\"", "\"Stove\"", "\"Tent\"", "\"Toilet roll\"", "\"Torch\"", "\"Water\"", "\"Waterproofs\""},
                        {"\"Card Games\"","\"Bridge\"","\"Canasta\"","\"Cribbage\"","\"Gin Rummy\"", "\"Patience\"", "\"Poker\"", "\"Pontoon\"", "\"Snap\"", "\"Uno\"", "\"Whist\""},
                        {"\"Famous London Shops\"","\"Burberry\"","\"Fenwick\"","\"Fortnum & Mason\"","\"Foyels\"", "\"Harrods\"", "\"Harvey Nichols\"", "\"Heals\"", "\"Liberty\"", "\"Selfridges\"", "\"The Conran Shop\""},
                        {"\"Foods With Country Names\"","\"Brazil nuts\"","\"French fries\"","\"Greek salad\"","\"Irish stew\"", "\"Scotch egg\"", "\"Spanish omelette\"", "\"Swedish meatballs\"", "\"Swiss roll\"", "\"Turkey\"", "\"Welsh rarebit\""},
                        {"\"World Faiths\"","\"Baha'i Faith\"","\"Buddhism\"","\"Cao Dai\"","\"Christianity\"", "\"Hinduism\"", "\"Islam\"", "\"Jainism\"", "\"Judaism\"", "\"Shinto\"", "\"Sikhism\""}};
            }

            //Creates the StringBuilder with first part of the insert statement added to it
            StringBuilder insertQuestionsTable = new StringBuilder("INSERT INTO " + TABLE_QUESTIONS + "(" + COL_BOARD_POSITIONS_CARD_COLOUR
                    + ", " + COL_QUESTIONS_ID + ", " + COL_QUESTIONS_QUESTION + ", " + COL_QUESTIONS_ANS_1
                    + ", " + COL_QUESTIONS_ANS_2 + ", " + COL_QUESTIONS_ANS_3 + ", " + COL_QUESTIONS_ANS_4
                    + ", " + COL_QUESTIONS_ANS_5 + ", " + COL_QUESTIONS_ANS_6 + ", " + COL_QUESTIONS_ANS_7
                    + ", " + COL_QUESTIONS_ANS_8 + ", " + COL_QUESTIONS_ANS_9 + ", " + COL_QUESTIONS_ANS_10 + ") VALUES");

            //This for loop is used to increment the questionId by 1 for every row that is added to questions table with the same cardColour
            for (int n = 0; n < questionVals.length; n++){
                //This concatenates the cardColour, questionsId, question, ans1, ans2, ans3, ans4, ans5, ans6, ans7, ans8, ans9 and ans10 values
                // into a single string for the values of the sql insert statement
                insertQuestionsTable.append("(").append(questionTypeArray[j]).append(", ").append((n + 1)).append(", ").append(questionVals[n][0]).append(", ")
                        .append(questionVals[n][1]).append(", ").append(questionVals[n][2]).append(", ").append(questionVals[n][3]).append(", ").append(questionVals[n][4])
                        .append(", ").append(questionVals[n][5]).append(", ").append(questionVals[n][6]).append(", ").append(questionVals[n][7])
                        .append(", ").append(questionVals[n][8]).append(", ").append(questionVals[n][9]).append(", ").append(questionVals[n][10]).append(")");

                //Detects if it is the last row of position information for the given boardId
                if (n < (questionVals.length - 1)){
                    insertQuestionsTable.append(",");  //Adds a comma if there is still more lines to add
                }else{
                    insertQuestionsTable.append(";");  //Adds a semicolon as this is the last line to be added for this given boardId
                }
            }

            try{
                //Executes the created insert statement for the given cardColour
                db.execSQL(insertQuestionsTable.toString());
            }catch (Exception e){
                Log.e("insertQuestions", "Unable to insert values into the questions table for the given cardColour " + questionTypeArray[j].toString());
            }
        }
    }

    //This method is called if the database version changes so that the databases schema is updated
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    //This method adds an entry of the given values to the gameBoards table
    public void insertGameBoards(Integer boardSpaces, Integer questionTimeLimit, String nameOfBoard){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        //Adds the values that were passed into the ContentVlaue
        cv.put(COL_GAME_BOARDS_TOTAL_SPACES, boardSpaces);
        cv.put(COL_GAME_BOARDS_TIME_LIMIT, questionTimeLimit);
        cv.put(COL_GAME_BOARDS_BOARD_NAME, nameOfBoard);

        try{
            db.insert(TABLE_GAME_BOARDS, null, cv);
        }catch(Exception e){
            Log.e("insertGameBoard", "------ Unable to insert the values into the gameBoards table ------");
        }finally {
            db.close();
        }
    }

    //This method is used to execute the read queries for the database
    private List<String> readDB(String sqlQuery){
        List<String> returnList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        try{
            cursor = db.rawQuery(sqlQuery, null);

            //Checks if the pointer can be moved to the first element in the cursor, if it can then it is true as values are in the cursor
            if (cursor.moveToFirst()){
                do{
                    returnList.add(cursor.getString(0));
                }while (cursor.moveToNext()); //Used to move to the next element in the cursor until it reaches the end
            }
        }catch(Exception e){
            Log.e("readDB Method", "------ The readDB method was unable to read the data from the table ------");
        }

        db.close();
        cursor.close();
        return returnList;
    }

    //This is a static version of the readDB method that requires the handle of the database to be passed to it
    //as a the databases handle can't be access due to the fact that the method is static meaning that it
    //can't access the database handle of the instance. The method should only be used when a static method is required
    //This method is used to execute the read queries for the database
    private static List<String> readDBStatic(String sqlQuery, SQLiteDatabase db){
        List<String> returnList = new ArrayList<>();
        Cursor cursor = null;

        try{
            cursor = db.rawQuery(sqlQuery, null);

            //Checks if the pointer can be moved to the first element in the cursor, if it can then it is true as values are in the cursor
            if (cursor.moveToFirst()){
                do{
                    returnList.add(cursor.getString(0));
                }while (cursor.moveToNext()); //Used to move to the next element in the cursor until it reaches the end
            }
        }catch(Exception e){
            Log.e("readDB Method", "------ The readDB method was unable to read the data from the table ------");
        }

        db.close();
        cursor.close();
        return returnList;
    }

    //This method is responsible for reading the names of the game boards from the database
    public List<String> getGameBoardNames(){
        //Creates an array list to store the results from the query
        List<String> boardNameList = new ArrayList<>();
        //This is the sql query that will be executed
        String readBoardNamesQuery = "SELECT " + COL_GAME_BOARDS_BOARD_NAME +" FROM " + TABLE_GAME_BOARDS + ";";
        //Passes the sql query and stores the results in the boardNameList
        boardNameList = readDB(readBoardNamesQuery);

        return boardNameList;
    }

    //This method is responsible for reading the names of the teams from the database
    public List<String> getTeamNames(){
        //Creates an array list to store the results from the query
        List<String> teamNameList = new ArrayList<>();
        //This is the sql query that will be executed
        String readTeamNamesQuery = "SELECT " + COL_TEAMS_NAME +" FROM " + TABLE_TEAMS + ";";
        //Passes the sql query and stores the results in the teamNameList
        teamNameList = readDB(readTeamNamesQuery);

        return teamNameList;
    }

    //This method is responsible for reading the names of the boards from the database
    public List<String> getBoardNames(){
        //Creates an array list to store the results from the query
        List<String> boardNameList = new ArrayList<>();
        //This is the sql query that will be executed
        String readBoardNamesQuery = "SELECT " + COL_GAME_BOARDS_BOARD_NAME +" FROM " + TABLE_GAME_BOARDS + ";";
        //Passes the sql query and stores the results in the boardNameList
        boardNameList = readDB(readBoardNamesQuery);

        return boardNameList;
    }

    //This method is responsible for reading the image name of the passed board name from the database
    //This method has to be static as it is used inside onItemSelected() that is a static method
    //that is used by the board dropdown spinner
    public static String getBoardImg(String boardName, SQLiteDatabase db){
        String boardImg = null;

        //This is the sql query that will be executed
        String readBoardImgQuery = "SELECT " + COL_GAME_BOARDS_IMG_NAME +" FROM " + TABLE_GAME_BOARDS + " WHERE "
                + COL_GAME_BOARDS_BOARD_NAME + " = \"" + boardName + "\"" + ";";
        //Passes the sql query and stores the results in the boardImg
        boardImg = readDBStatic(readBoardImgQuery, db).get(0).toString();

        return boardImg;
    }
}
