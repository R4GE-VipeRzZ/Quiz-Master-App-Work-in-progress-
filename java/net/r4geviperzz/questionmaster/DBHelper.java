package net.r4geviperzz.questionmaster;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "question_master.db";
    private static final int DB_VERSION = 1;
    private static final String TABLE_GAME_BOARDS = "gameBoards";
    private static final String COL_GAME_BOARDS_ID = "boardId";
    private static final String COL_GAME_BOARDS_TOTAL_SPACES = "totalSpaces";
    private static final String COL_GAME_BOARDS_TIME_LIMIT = "timeLimit";
    private static final String COL_GAME_BOARDS_BOARD_NAME = "boardName";
    private static final String COL_GAME_BOARDS_IMG_NAME = "boardImgName";
    private static final String COL_GAME_BOARDS_QUESTION_ORDER = "questionOrder";
    private static final String COL_GAME_BOARDS_QUESTION_ORDER_COUNT = "questionOrderCount";
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
    private static final String COL_GAME_SESSION_NEXT_TEAM_TO_ASK = "teamToAskNext";
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
                + COL_GAME_BOARDS_BOARD_NAME + " VARCHAR(20) NOT NULL, " + COL_GAME_BOARDS_IMG_NAME + " VARCHAR(30) NOT NULL, "
                + COL_GAME_BOARDS_QUESTION_ORDER + " BLOB, " + COL_GAME_BOARDS_QUESTION_ORDER_COUNT + " BLOB" +");";

        try {
            db.execSQL(createGameBoardsTable);
        }catch (Exception e){
            Log.e("query" , createGameBoardsTable);
            Log.e("createGameBoardTable", "Unable to create gameBoard table");
        }

        //Insert values into gameBoards table
        String insertGameBoardsTable = "INSERT INTO " + TABLE_GAME_BOARDS + "(" + COL_GAME_BOARDS_TOTAL_SPACES + ", "
                + COL_GAME_BOARDS_TIME_LIMIT + ", " + COL_GAME_BOARDS_BOARD_NAME + ", " + COL_GAME_BOARDS_IMG_NAME + ") VALUES " +
                "(65, 60, \"Standard\", \"standard_board\")," +
                "(60,60, \"Snake\", \"snake_board\");";

        try{
            db.execSQL(insertGameBoardsTable);
        }catch (Exception e){
            Log.e("insertGameBoards", "Unable to insert values into gameBoards table");
        }

        //Creates the teams table
        String createTeamsTable = "CREATE TABLE " + TABLE_TEAMS + " (" + COL_TEAMS_ID+ " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
                + COL_TEAMS_NAME + " VARCHAR(14) NOT NULL, " + COL_TEAMS_IMG_NAME + " VARCHAR(18), "
                + " UNIQUE (" + COL_TEAMS_NAME + ", " + COL_TEAMS_IMG_NAME + "));";

        try {
            db.execSQL(createTeamsTable);
        }catch (Exception e){
            Log.e("createTeamsTable", "Unable to create teams table");
        }

        //Insert values into teams table
        String insertTeamsTable = "INSERT INTO " + TABLE_TEAMS + "(" + COL_TEAMS_NAME + ", " + COL_TEAMS_IMG_NAME + ") VALUES " +
                "(\"Blue\", \"blue_counter\"),"
                + "(\"Red\", \"red_counter\")," +
                "(\"Yellow\", \"yellow_counter\")," +
                "(\"Green\", \"green_counter\")," +
                "(\"Orange\", \"orange_counter\")," +
                "(\"White\", \"white_counter\")," +
                "(\"Brown\", \"brown_counter\")," +
                "(\"Purple\", \"purple_counter\")," +
                "(\"Pink\", \"pink_counter\")," +
                "(\"Black\", \"black_counter\");";

        try{
            db.execSQL(insertTeamsTable);
        }catch (Exception e){
            Log.e("insertTeams", "Unable to insert values into teams table");
        }

        //Create the boardPositions table
        String createBoardPositions = "CREATE TABLE " + TABLE_BOARD_POSITIONS + " (" + COL_GAME_BOARDS_ID + " INTEGER NOT NULL, "
                + COL_BOARD_POSITIONS_POS_ID + " INTEGER NOT NULL, " + COL_BOARD_POSITIONS_GRID_X + " INTEGER NOT NULL, "
                + COL_BOARD_POSITIONS_GRID_Y + " INTEGER NOT NULL, " + COL_BOARD_POSITIONS_CARD_COLOUR + " INTEGER, "
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
        int[] boardIdsArray = {1,2};

        //This for loop is used to insert the board positions to the boardPositions table with the correct boardId
        for (int x = 0; x < boardIdsArray.length; x++){
            String[][] boardLocationVals;
            boardLocationVals = null;
            if (x == 0){    //This is true if the board that is being selected is the first in the boardIdsArray
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
                        {"5", "6", "null","0"}};
            } else if (x == 1) {    //This is true if the board that is being selected is the second in the boardIdsArray
                //Values {girdX, gridY, cardColour, Special}
                boardLocationVals = new String[][]{{"1","10","1","0"}, //This two dimensional string array contains all the boardPositions for the
                        {"2", "10", "0", "0"},
                        {"3", "10", "1", "0"},
                        {"4", "10", "0", "0"},
                        {"5", "10", "1", "0"},
                        {"6", "10", "0", "0"},
                        {"7", "10", "1", "0"},
                        {"8", "10", "0", "0"},
                        {"9", "10", "1", "0"},
                        {"10", "10", "0", "0"},
                        {"11", "10", "1", "1"},
                        {"11", "9", "1", "0"},
                        {"11", "8", "1", "-1"},
                        {"10", "8", "0", "0"},
                        {"9", "8", "1", "0"},
                        {"8", "8", "0", "0"},
                        {"7", "8", "1", "0"},
                        {"6", "8", "0", "0"},
                        {"5", "8", "1", "0"},
                        {"4", "8", "0", "0"},
                        {"3", "8", "1", "0"},
                        {"2", "8", "0", "0"},
                        {"1", "8", "1", "-1"},
                        {"1", "7", "1", "0"},
                        {"1", "6", "1", "1"},
                        {"2", "6", "0", "0"},
                        {"3", "6", "1", "0"},
                        {"4", "6", "0", "0"},
                        {"5", "6", "1", "0"},
                        {"6", "6", "0", "0"},
                        {"7", "6", "1", "0"},
                        {"8", "6", "0", "0"},
                        {"9", "6", "1", "0"},
                        {"10", "6", "0", "0"},
                        {"11", "6", "1", "-1"},
                        {"11", "5", "1", "0"},
                        {"11", "4", "1", "1"},
                        {"10", "4", "0", "0"},
                        {"9", "4", "1", "0"},
                        {"8", "4", "0", "0"},
                        {"7", "4", "1", "0"},
                        {"6", "4", "0", "0"},
                        {"5", "4", "1", "0"},
                        {"4", "4", "0", "0"},
                        {"3", "4", "1", "0"},
                        {"2", "4", "0", "0"},
                        {"1", "4", "1", "-1"},
                        {"1", "3", "1", "0"},
                        {"1", "2", "1", "-1"},
                        {"2", "2", "0", "0"},
                        {"3", "2", "1", "0"},
                        {"4", "2", "0", "0"},
                        {"5", "2", "1", "0"},
                        {"6", "2", "0", "0"},
                        {"7", "2", "1", "0"},
                        {"8", "2", "0", "0"},
                        {"9", "2", "1", "0"},
                        {"10", "2", "0", "0"},
                        {"11", "2", "1", "0"},
                        {"11", "1", "null","0"}};
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
                Log.e("insertBoardPositions", "Unable to insert values into the boardPositions table for the given boardId " + Integer.toString(boardIdsArray[x]));
            }
        }

        //Create the gameSession table
        String createGameSession = "CREATE TABLE " + TABLE_GAME_SESSION + " (" + COL_TEAMS_ID + " INTEGER NOT NULL, "
                + COL_GAME_BOARDS_ID + " INTEGER NOT NULL, " + COL_GAME_SESSION_QUESTIONS_ASKED + " INTEGER NOT NULL, "
                + COL_GAME_SESSION_CORRECT_ANS + " INTEGER NOT NULL, " + COL_GAME_SESSION_CURRENT_POS + " INTEGER NOT NULL, "
                + COL_GAME_SESSION_NEXT_TEAM_TO_ASK + " BOOLEAN NOT NULL, "
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
        int[] questionTypeArray = {0,1};

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
                Log.e("insertQuestions", "Unable to insert values into the questions table for the given cardColour " + Integer.toString(questionTypeArray[j]));
            }
        }
    }

    //This method is called if the database version changes so that the databases schema is updated
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

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
                    for (int n = 0; n < cursor.getColumnCount(); n++) {
                        returnList.add(cursor.getString(n));
                    }
                }while (cursor.moveToNext()); //Used to move to the next element in the cursor until it reaches the end
            }
        }catch(Exception e){
            Log.e("readDB Method", "------ The readDB method was unable to read the data from the table ------");
        }finally {
            if (cursor == null){
                Log.e("readDBMethod", "No such values are contained in the database");
            }
            db.close();
            cursor.close();
        }

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
                    for (int n = 0; n < cursor.getColumnCount(); n++) {
                        returnList.add(cursor.getString(n));
                    }
                }while (cursor.moveToNext()); //Used to move to the next element in the cursor until it reaches the end
            }
        }catch(Exception e){
            Log.e("readDB Method", "------ The readDB method was unable to read the data from the table ------");
        }finally {
            if (cursor == null){
                Log.e("readDBStaticMethod", "No such values are contained in the database");
            }

            db.close();
            cursor.close();
        }

        return returnList;
    }

    //This method is used to execute the read queries for the database when reading a blob field
    private byte[] readBlobDB(String sqlQuery) {
        byte[] blob = null;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        try {
            cursor = db.rawQuery(sqlQuery, null);

            if (cursor.moveToFirst()) {
                blob = cursor.getBlob(0);
            }
        } catch (Exception e) {
            Log.e("readByteDB Method", "------ The readByteDB method was unable to read the data from the table ------");
            Log.e("error", e.toString());
        } finally {
            if (cursor == null) {
                Log.e("readByteDB Method", "No such values are contained in the database");
            }
            db.close();
            cursor.close();
        }

        return blob;
    }

    //This method is used to write byte[] arrays to the database for blob fields
    private void writeBlobDB(byte[] passedBytes, String sqlQuery, String whereClause) {
        SQLiteDatabase db = this.getWritableDatabase();
        SQLiteStatement statement = db.compileStatement(sqlQuery);
        statement.bindBlob(1, passedBytes);
        if (whereClause != null && !whereClause.isEmpty()) {
            statement.bindString(2, whereClause);
        }
        try {
            statement.execute();
        } catch (Exception e) {
            Log.e("writeDB Method", "------ The writeByteDB method was unable to add the data to the database ------");
            Log.e("error", e.toString());
        } finally {
            statement.close();
            db.close();
        }
    }

    //This method is used to execute the write queries for the database
    private void writeDB(String sqlQuery){
        SQLiteDatabase db = this.getWritableDatabase();

        try{
            db.execSQL(sqlQuery);
        }catch(Exception e){
            Log.e("writeDB Method", "------ The writeDB method was unable to add the data to the database ------");
            Log.e("error", e.toString());
        }finally {
            db.close();
        }
    }

    //This method return a boolean depending on it the last game on the specified board in the boardSession table ended with a team winning
    //returns true in a gameSession doesn't already exist for the given board or if a team had won in the gameSession for the given board
    //is a team has already won for the session the it will called the delete method to remove the entries for the given board from the
    //gameSession table before it returns true
    public Boolean checkGameWin(String passedBoardId){
        Boolean gameWon = false;

        //This is the sql query that will be executed
        String checkForBoardQuery = "SELECT " + COL_TEAMS_ID +" FROM " + TABLE_GAME_SESSION + " WHERE " + COL_GAME_BOARDS_ID
                + " = " + passedBoardId + ";";

        //Executes the sql query that checks if an entry of the passed boardId is in the gameSession table
        int boardOfIdSize = readDB(checkForBoardQuery).size();

        //This is true if there is an entry with the passed boardId in it
        if (boardOfIdSize  > 0){
            String totalBoardSpacesQuery = "SELECT " + COL_GAME_BOARDS_TOTAL_SPACES + " FROM " + TABLE_GAME_BOARDS + " WHERE "
                    + COL_GAME_BOARDS_ID + " = " + passedBoardId +";";

            //Executes the query that gets to total number of spaces for that boardId that was passed into the method
            String numBoardSpaces = readDB(totalBoardSpacesQuery).get(0);

            String readTeamWinQuery = "SELECT " + COL_TEAMS_ID +" FROM " + TABLE_GAME_SESSION + " WHERE "
                    + COL_GAME_SESSION_CURRENT_POS + " = " + numBoardSpaces + ";";

            //Executes the query that checks if there is any team in the gameSession table with the passed boardId
            //that has got to the end win position on the board
            int winningTeamSize = readDB(readTeamWinQuery).size();

            //This is true if a game session exists for the board where a team has already won
            if (winningTeamSize > 0){
                deleteGameSession(passedBoardId);
                gameWon = true;
            }
        }else{
            gameWon = true;
        }

        return gameWon;
    }

    //This method deletes the entries in the gameSession table that have the boardId of the passed value
    public void deleteGameSession(String passedBoardId){
        //This is the sql query that will be executed
        String deleteGameSessionQuery = "DELETE FROM " + TABLE_GAME_SESSION + " WHERE " + COL_GAME_BOARDS_ID + " = " + passedBoardId + ";";

        //Passes the sql query that need to be executed
        writeDB(deleteGameSessionQuery);
    }

    //This method inserts the required data to the gameSession table for when a new game is being created
    public void setInitialGameSessionValues(List<String> passedTeamIdsList, String passedBoardId){
        //This is the sql query that will be executed
        String insertGameSessionValQuery = "INSERT INTO " + TABLE_GAME_SESSION + "(" + COL_TEAMS_ID + ", "
                + COL_GAME_BOARDS_ID + ", " + COL_GAME_SESSION_QUESTIONS_ASKED + ", " + COL_GAME_SESSION_CORRECT_ANS
                + ", " + COL_GAME_SESSION_CURRENT_POS + ", " + COL_GAME_SESSION_NEXT_TEAM_TO_ASK + ") VALUES ";

        for(int i = 0; i < passedTeamIdsList.size(); i++){
            //Checks if it is the first team been added to the gameSession table and if it is makes it so that it will be the team that gets asked a question first
            if (i == 0) {
                insertGameSessionValQuery = insertGameSessionValQuery + "(" + passedTeamIdsList.get(i) + ", "
                        + passedBoardId + ", 0, 0, 1, 1)";
            }else{
                insertGameSessionValQuery = insertGameSessionValQuery + "(" + passedTeamIdsList.get(i) + ", "
                        + passedBoardId + ", 0, 0, 1, 0)";
            }

            //This if statement adds a semi colon to the end of the value brackets if it is the last value that is going to be added, else it sets it to a comma
            if (i == (passedTeamIdsList.size() - 1)){
                insertGameSessionValQuery = insertGameSessionValQuery + ";";
            }else{
                insertGameSessionValQuery = insertGameSessionValQuery + ",";
            }
        }

        //Passes the sql query that needs to be executed
        writeDB(insertGameSessionValQuery);
    }

    //This method updates the currentPos of the passed teamId to the passed value
    public void setCurrentPosByTeamId (String passedBoardId, String passedTeamId, int passedTotalPosValue){
        //This is the sql query that will be executed
        String updatedCounterTotalPosQuery = "UPDATE " + TABLE_GAME_SESSION + " SET "
                + COL_GAME_SESSION_CURRENT_POS + " = " + passedTotalPosValue
                + " WHERE " + COL_TEAMS_ID + " = " + passedTeamId + " AND "
                + COL_GAME_BOARDS_ID + " = " + passedBoardId + ";";
        //Passes the sql query that needs to be executed
        writeDB(updatedCounterTotalPosQuery);
    }

    //This method stores the serialised data from the questionOrderArray in the gameBoards table for the passed boardId
    public void setBoardQuestionOrder(byte[] passedSerialisedData, String passedBoardId){
        //Create the prepared sql statement
        String byteInsertQuery = "UPDATE " + TABLE_GAME_BOARDS + " SET "
                + COL_GAME_BOARDS_QUESTION_ORDER + " = ? WHERE "
                + COL_GAME_BOARDS_ID + " = ?;";

        //Pass the sql query and byte date that needs to be executed
        writeBlobDB(passedSerialisedData, byteInsertQuery, passedBoardId);
    }

    //This method stores the serialised data from the cardColourCountArray in the gameBoards table for the passed boardId
    public void setBoardQuestionCountOrder(byte[] passedSerialisedData, String passedBoardId){
        //Create the prepared sql statement
        String byteInsertQuery = "UPDATE " + TABLE_GAME_BOARDS + " SET "
                + COL_GAME_BOARDS_QUESTION_ORDER_COUNT + " = ? WHERE "
                + COL_GAME_BOARDS_ID + " = ?;";

        //Pass the sql query and byte date that needs to be executed
        writeBlobDB(passedSerialisedData, byteInsertQuery, passedBoardId);
    }

    //This method is used to store the team that should be asked the question next so that when the game session is loaded again the correct team is asked the next question
    public void setTeamToAskNext (String passedTeamId, String passedBoardId){
        //This is the sql query that will be executed to reset the old ask next value
        String removePreviousAskNextValue = "UPDATE " + TABLE_GAME_SESSION + " SET "
                + COL_GAME_SESSION_NEXT_TEAM_TO_ASK + " = 0 " + "WHERE " + COL_GAME_BOARDS_ID
                + " = " + passedBoardId + " AND " + COL_GAME_SESSION_NEXT_TEAM_TO_ASK + " = 1;";
        writeDB(removePreviousAskNextValue);

        String updatedAskNextValue = "UPDATE " + TABLE_GAME_SESSION + " SET "
                + COL_GAME_SESSION_NEXT_TEAM_TO_ASK + " = 1 " + "WHERE " + COL_TEAMS_ID
                + " = " + passedTeamId+ " AND " + COL_GAME_BOARDS_ID + " = " + passedBoardId +";";
        //Passes the sql query that needs to be executed
        writeDB(updatedAskNextValue);
    }

    //This method gets the row and column values that correspond to the posId that is passed
    public List<String> getPosGridLocations(int passedPosValue, String passedBoardId){
        //Creates an array list to store the results from the query
        List<String> gridLocations = new ArrayList<>();
        //This is the sql query that will be executed
        String readGridLocationsQuery = "SELECT " +  COL_BOARD_POSITIONS_GRID_X + ", " + COL_BOARD_POSITIONS_GRID_Y + " FROM " + TABLE_BOARD_POSITIONS + " WHERE "
                + COL_GAME_BOARDS_ID + " = " + passedBoardId + " AND " + COL_BOARD_POSITIONS_POS_ID + " = " + Integer.toString(passedPosValue) +";";
        //Passes the sql query and stores the results in the teamNameList
        gridLocations = readDB(readGridLocationsQuery);

        return gridLocations;
    }

    //This method is responsible for reading the names of the boards from the database
    public List<String> getGameBoardNames(){
        //Creates an array list to store the results from the query
        List<String> boardNameList = new ArrayList<>();
        //This is the sql query that will be executed
        String readBoardNamesQuery = "SELECT " + COL_GAME_BOARDS_BOARD_NAME +" FROM " + TABLE_GAME_BOARDS + ";";
        //Passes the sql query and stores the results in the boardNameList
        boardNameList = readDB(readBoardNamesQuery);

        return boardNameList;
    }

    //This method is responsible for reading the image name of the passed board id from the database
    public String getBoardImgById(String passedBoardId){
        String boardImg = null;

        //This is the sql query that will be executed
        String readBoardImgByIdQuery = "SELECT " + COL_GAME_BOARDS_IMG_NAME +" FROM " + TABLE_GAME_BOARDS + " WHERE "
                + COL_GAME_BOARDS_ID + " = " + passedBoardId + ";";
        //Passes the sql query and stores the results in the boardImg
        boardImg = readDB(readBoardImgByIdQuery).get(0).toString();

        return boardImg;
    }

    //This method is responsible for reading the image name of the passed board name from the database
    //This method has to be static as it is used inside onItemSelected() that is a static method
    //that is used by the board dropdown spinner
    public static String getBoardImgStatic(String boardName, SQLiteDatabase db){
        String boardImg = null;

        //This is the sql query that will be executed
        String readBoardImgQuery = "SELECT " + COL_GAME_BOARDS_IMG_NAME +" FROM " + TABLE_GAME_BOARDS + " WHERE "
                + COL_GAME_BOARDS_BOARD_NAME + " = \"" + boardName + "\"" + ";";
        //Passes the sql query and stores the results in the boardImg
        boardImg = readDBStatic(readBoardImgQuery, db).get(0).toString();

        return boardImg;
    }

    //This method returns the boardId from the gameBoards table using the boardName that is passed to it
    public String getBoardIdByName(String passedBoardName){
        //Creates the string that is used to store the results from the query
        String returnedBoardId = null;
        //This is the sql query that will be executed
        String readTeamIDByNameQuery = "SELECT " + COL_GAME_BOARDS_ID +" FROM " + TABLE_GAME_BOARDS + " WHERE " + COL_GAME_BOARDS_BOARD_NAME
                + " = \"" + passedBoardName + "\";";
        //Passes the sql query and stores the results in the returnedTeamId String
        returnedBoardId = readDB(readTeamIDByNameQuery).get(0);

        return returnedBoardId;
    }

    //This method returns the total number of spaces in the passed board id
    public int getBoardTotalSpaces(String passedBoardId){
        //Creates the string that is used to store the results from the query
        int returnedBoardSpaces = 0;
        //This is the sql query that will be executed
        String readBoardSpacesQuery = "SELECT " + COL_GAME_BOARDS_TOTAL_SPACES + " FROM " + TABLE_GAME_BOARDS + " WHERE " + COL_GAME_BOARDS_ID + " = " + passedBoardId + ";";
        //Passes the sql query and stores the results in the returnedTeamId String
        returnedBoardSpaces = Integer.parseInt(readDB(readBoardSpacesQuery).get(0));

        return returnedBoardSpaces;
    }

    //This method is responsible for reading all the names of the teams from the database
    public List<String> getAllTeamNames(){
        //Creates an array list to store the results from the query
        List<String> teamNameList = new ArrayList<>();
        //This is the sql query that will be executed
        String readTeamNamesQuery = "SELECT " + COL_TEAMS_NAME +" FROM " + TABLE_TEAMS + ";";
        //Passes the sql query and stores the results in the teamNameList
        teamNameList = readDB(readTeamNamesQuery);

        return teamNameList;
    }

    //This method returns the teamId from the team table using the team name that is passed to it
    public List<String> getTeamIdByName(String[] passedTeamNamesArray){
        List<String> teamNamesArrayWithSpeechMarks = new ArrayList<>();
        for (String element : passedTeamNamesArray){
            teamNamesArrayWithSpeechMarks.add("\"" + element + "\"");
        }
        String passedTeamNamesString = String.join(", ", teamNamesArrayWithSpeechMarks);
        //Creates the string that is used to store the results from the query
        List<String> returnedTeamIdsList = null;
        //This is the sql query that will be executed
        String readTeamIDByNameQuery = "SELECT " + COL_TEAMS_ID +" FROM " + TABLE_TEAMS + " WHERE " + COL_TEAMS_NAME
                + " IN (" + passedTeamNamesString + ");";
        //Passes the sql query and stores the results in the returnedTeamId String
        returnedTeamIdsList = readDB(readTeamIDByNameQuery);

        return returnedTeamIdsList;
    }

    //This method is responsible for reading the IDs of the teams from the database for the given boardId
    public List<String> getTeamIdsFromSession(String passedBoardId){
        //Creates an array list to store the results from the query
        List<String> sessionTeamIdsList = new ArrayList<>();
        //This is the sql query that will be executed
        String readTeamIdsQuery = "SELECT " + COL_TEAMS_ID +" FROM " + TABLE_GAME_SESSION + " WHERE "
                + COL_GAME_BOARDS_ID + " = " + passedBoardId + ";";
        //Passes the sql query and stores the results in the sessionTeamIdsList
        sessionTeamIdsList = readDB(readTeamIdsQuery);

        return sessionTeamIdsList;
    }

    //This method is responsible for reading all the team names that correspond to the teams in the gameSession table for the boardId that is passed
    public List<String> getTeamNamesForSession(String[] passedTeamIds){
        //Converts the String[] to a single string with each of the elements separated in the string by a comma
        //This string of all the team ids for the session is then used in the next query to get the teams names
        String sessionTeamIdsString = String.join(", ", passedTeamIds);
        //Creates an array list to store the results from the query
        List<String> sessionTeamNamesList = new ArrayList<>();
        //This is the sql query that will be executed
        String readTeamNamesQuery = "SELECT " + COL_TEAMS_NAME +" FROM " + TABLE_TEAMS + " WHERE "
                + COL_TEAMS_ID + " IN (" + sessionTeamIdsString + ");";
        //Passes the sql query and stores the results in the sessionTeamNamesList
        sessionTeamNamesList = readDB(readTeamNamesQuery);

        return sessionTeamNamesList;
    }

    //This method gets the id of the team that should be asked the next question, this information is need when loading a game session
    public Integer getIdOfTeamToAskNext(String passedBoardId){
        //Creates a variable to store tha id of the team that should be asked the next question
        Integer teamToAskNextQuestion = 0;
        //This is the sql query that will be executed
        String readTeamIdsQuery = "SELECT " + COL_TEAMS_ID +" FROM " + TABLE_GAME_SESSION + " WHERE "
                + COL_GAME_BOARDS_ID + " = " + passedBoardId + " AND " + COL_GAME_SESSION_NEXT_TEAM_TO_ASK + " = 1;";
        //Passes the sql query and stores the results in the sessionTeamIdsList
        teamToAskNextQuestion = Integer.parseInt(readDB(readTeamIdsQuery).get(0));

        return teamToAskNextQuestion;
    }

    //This method returns the image names of the team ids that are passed
    public List<String> getTeamImgByIds(String[] passedTeamIds){
        //Converts the String[] to a single string with each of the elements separated in the string by a comma
        //This string of all the team ids for the session is then used in the next query to get the teams images
        String sessionTeamIdsString = String.join(", ", passedTeamIds);
        //Creates an array list to store the results from the query
        List<String> teamImgList = new ArrayList<>();
        //This is the sql query that will be executed
        String readTeamNamesQuery = "SELECT " + COL_TEAMS_IMG_NAME +" FROM " + TABLE_TEAMS + " WHERE "
                + COL_TEAMS_ID + " IN (" + sessionTeamIdsString + ");";
        //Passes the sql query and stores the results in the teamImgList
        teamImgList = readDB(readTeamNamesQuery);

        return teamImgList;
    }

    //This method gets the current position of a passed user id from the gameSession table
    public List<String> getCurrentPosByTeamId(String[] passedTeamIdArray, String passedBoardId){
        //Converts the String[] to a single string with each of the elements separated in the string by a comma
        //This string of all the team ids for the session is then used to find the position of each
        String passedTeamIds = String.join(", ", passedTeamIdArray);
        //Creates the array list that is used to store the results from the query
        List<String> returnedCountersPos = new ArrayList<>();
        //This is the sql query that will be executed
        String readCurrentCounterPosQuery = "SELECT " + COL_GAME_SESSION_CURRENT_POS + " FROM " + TABLE_GAME_SESSION
                + " WHERE  " +  COL_GAME_BOARDS_ID + " = " + passedBoardId + " AND "
                + COL_TEAMS_ID + " IN (" + passedTeamIds + ");";
        //Passes the sql query and stores the results in the returnedTeamId String
        returnedCountersPos = readDB(readCurrentCounterPosQuery);

        return returnedCountersPos;
    }

    //This method returns the distinct card colour values that are in the boardPositions table for the given boardId
    //For example, if the field of cardColour only ever stores the value of 0 or 1 then this method should return 0,1
    public List<String> getBoardCardColours(String passedBoardId){
        //Creates an array list to store the results from the query
        List<String> cardColoursList = new ArrayList<>();
        //This is the sql query that will be executed
        String readTeamNamesQuery = "SELECT DISTINCT " + COL_BOARD_POSITIONS_CARD_COLOUR + " FROM "
                + TABLE_BOARD_POSITIONS + " WHERE " + COL_GAME_BOARDS_ID + " = " + passedBoardId
                + " AND " + COL_BOARD_POSITIONS_CARD_COLOUR + " IS NOT NULL ORDER BY "
                + COL_BOARD_POSITIONS_CARD_COLOUR + " ASC" +";";
        //Passes the sql query and stores the results in the cardColoursList
        cardColoursList = readDB(readTeamNamesQuery);

        return cardColoursList;
    }

    //This method gets the questions in the questions table that have cardColour value that was passed in
    //p.s. the card colour should be a number e.g. 1 or 0
    public List<String> getQuestionIdsByCardColour(String passedCardColour){
        //Creates an array list to store the results from the query
        List<String> questionOfColourList = new ArrayList<>();
        //This is the sql query that will be executed
        String questionIdsOfCardColourQuery = "SELECT " + COL_QUESTIONS_ID + " FROM " + TABLE_QUESTIONS
                + " WHERE " + COL_BOARD_POSITIONS_CARD_COLOUR + " = " + passedCardColour +";";
        //Passes the sql query and stores the results in the questionOfColourList
        questionOfColourList = readDB(questionIdsOfCardColourQuery);

        return questionOfColourList;
    }

    //This method get the Blob value for the questionOrder field from the gameBoards table for the given boardId
    public byte[] getBoardQuestionOrder(String passedBoardId){
        //Creates a byte[] to store the results from the query
        byte[] resultBlob = null;
        //This is the sql query that will be executed
        String questionOrderBlobQuery = "SELECT " + COL_GAME_BOARDS_QUESTION_ORDER + " FROM "
                + TABLE_GAME_BOARDS + " WHERE " + COL_GAME_BOARDS_ID + " = " + passedBoardId +";";
        //Passes the sql query and stores the results in the questionOfColourList
        resultBlob = readBlobDB(questionOrderBlobQuery);

        return resultBlob;
    }

    //This method get the Blob value for the questionOrderCount field from the gameBoards table for the given boardId
    public byte[] getBoardQuestionCountOrder(String passedBoardId){
        //Creates a byte[] to store the results from the query
        byte[] resultBlob = null;
        //This is the sql query that will be executed
        String questionCountOrderBlobQuery = "SELECT " + COL_GAME_BOARDS_QUESTION_ORDER_COUNT + " FROM "
                + TABLE_GAME_BOARDS + " WHERE " + COL_GAME_BOARDS_ID + " = " + passedBoardId +";";
        //Passes the sql query and stores the results in the questionOfColourList
        resultBlob = readBlobDB(questionCountOrderBlobQuery);

        return resultBlob;
    }

    //This method gets the cardColour value of the passed position for the passed boardId
    public String getPosCardColour(int passedPosValue, String passedBoardId){
        //Creates an string to store the results from the query
        String posCardColour = null;
        //This is the sql query that will be executed
        String readPosCardColourQuery = "SELECT " + COL_BOARD_POSITIONS_CARD_COLOUR + " FROM " + TABLE_BOARD_POSITIONS
                + " WHERE " + COL_GAME_BOARDS_ID + " = " + passedBoardId + " AND "
                + COL_BOARD_POSITIONS_POS_ID + " = " + passedPosValue +";";
        //Passes the sql query and stores the results in the cardColoursList
        posCardColour = readDB(readPosCardColourQuery).get(0);

        return posCardColour;
    }

    //This method gets the question from the questions table that corresponds to the passed card colour and question id
    public List<String> getQuestionByColour(String passedCardColour, String passedQuestionId){
        //Creates an array list to store the results from the query
        List<String> questionAndAnswersList = new ArrayList<>();
        //This is the sql query that will be executed
        String questionAndAnswersQuery = "SELECT " + COL_QUESTIONS_QUESTION + ", " + COL_QUESTIONS_ANS_1 + ", "
                + COL_QUESTIONS_ANS_2 + ", " + COL_QUESTIONS_ANS_3 + ", " + COL_QUESTIONS_ANS_4 + ", "
                + COL_QUESTIONS_ANS_5 + ", " + COL_QUESTIONS_ANS_6 + ", " + COL_QUESTIONS_ANS_7
                + ", " + COL_QUESTIONS_ANS_8 + ", " + COL_QUESTIONS_ANS_9 + ", " + COL_QUESTIONS_ANS_10 + " FROM "
                + TABLE_QUESTIONS + " WHERE " + COL_QUESTIONS_ID + " = " + passedQuestionId + " AND "
                + COL_BOARD_POSITIONS_CARD_COLOUR + " = " + passedCardColour + " ORDER BY " +
                COL_QUESTIONS_QUESTION + " ASC," + COL_QUESTIONS_ANS_1 + ", " + COL_QUESTIONS_ANS_2 + ", "
                + COL_QUESTIONS_ANS_3 + ", " + COL_QUESTIONS_ANS_4 + ", " + COL_QUESTIONS_ANS_5 + ", "
                + COL_QUESTIONS_ANS_6 + ", " + COL_QUESTIONS_ANS_7 + ", " + COL_QUESTIONS_ANS_8 + ", "
                + COL_QUESTIONS_ANS_9 + ", " + COL_QUESTIONS_ANS_10 + ";";
        //Passes the sql query and stores the results in the questionOfColourList
        questionAndAnswersList = readDB(questionAndAnswersQuery);

        return questionAndAnswersList;
    }
}
