package net.r4geviperzz.questionmaster;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

// This is the model class for the recycler view
public class Question {
    private static DBHelper dbHelper;
    private static String idOfBoard = null;
    private static String[][] questionOrderArray = null;
    private static String[] cardColoursArray = null;
    private static int[] cardColourCountArray = null;

    //This method will deserialise the date that is passed to it and return the date in the form of an object
    private Object deserialiseArray(byte[] passedSerialisedData) {
        Object deserializedData = null;

        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(passedSerialisedData);
            ObjectInputStream ois = new ObjectInputStream(bis);
            deserializedData = ois.readObject();
            ois.close();
            bis.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        // Check the type of the deserialized data and return either a int[] or a String[][]
        //int[] for when the data relates to a 1d int array and String[][] for when the data relates to a 2d String array
        if (deserializedData instanceof int[]) {
            return (int[]) deserializedData;
        } else if (deserializedData instanceof String[][]) {
            return (String[][]) deserializedData;
        } else {
            throw new IllegalArgumentException("Invalid serialized data type.");
        }
    }

    //This method will serialise the data that is passed to it and return the data in the form of a byte
    private byte[] serialiseArray(Object passedData){
        byte[] serialisedData = null;

        try{
            // serialise the 2D array
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(passedData);
            serialisedData = bos.toByteArray();
        }catch (IOException e){
            Log.e("errSerialisingQstOrdAry", e.toString());
        }

        return serialisedData;
    }

    //This method stores the values that are in the questionOrderArray to the corresponding boardId in the gameBoards table
    private void saveQuestionOrder(){
        byte[] returnedData = serialiseArray(questionOrderArray);
        dbHelper.setBoardQuestionOrder(returnedData,idOfBoard);
    }

    //This method stores the values that are in the cardColourCountArray to the corresponding boardId in the gameBoards table
    public void saveQuestionCountOrder(){
        byte[] returnedData = serialiseArray(cardColourCountArray);
        dbHelper.setBoardQuestionCountOrder(returnedData,idOfBoard);
    }

    //This method will create the and randomise the 2d array that is used to store the ids of the questions for each type
    //Each question type is stored as a separate row in the 2d array
    //Each row has to be randomised so that the questions aren't asked in the same order for each game session
    public void createQuestionOrder(DBHelper passedDBHelper, String passedBoardId){
        //Sets the class dbHelper to the passedDBHelper
        dbHelper = passedDBHelper;
        //Sets the class idOfBoard to the passedBoardId
        idOfBoard = passedBoardId;

        //Store the card colour values e.g.(0,1) for the passedBoardId in the cardColourArray
        cardColoursArray = (dbHelper.getBoardCardColours(passedBoardId)).toArray(new String[0]);

        //Sets the array that will be used to keep track of how many elements in to each row of the questionOrderArray a user is
        cardColourCountArray = new int[cardColoursArray.length];

        for(int z = 0; z < cardColourCountArray.length; z++){
            cardColourCountArray[z] = 0;
        }

        for(int i = 0; i < cardColoursArray.length; i++){
            //Gets all of the question ids of the questions that correspond to the passed colour value
            List<String> newQuestionIdRowList = dbHelper.getQuestionIdsByCardColour(cardColoursArray[i]);
            //This shuffles the list so that the values are in a random order, this needs to be done so
            //that the questions of this colour aren't asked in the same order for every game session
            Collections.shuffle(newQuestionIdRowList);
            //Converts the shuffled list to an array
            String[] newQuestionIdRowShuffled = newQuestionIdRowList.toArray(new String[0]);

            //Create a new temporary 2d array that is one row larger than the current questionOrderArray
            String[][] tempNewArray;
            int questionOrderArrayLength = 0;

            //Try catch is needed for when the first row is been added to the questionOrderArray as with no rows it will have a length of null
            try {
                questionOrderArrayLength= questionOrderArray.length;
                tempNewArray = new String[questionOrderArray.length + 1][];
            }catch (NullPointerException e){
                tempNewArray = new String[1][];
            }

            //This if statement is used to check if there are any rows in the questionOrderArray and
            // if there is then copies questionOrderArray to the tempNewArray
            if (questionOrderArrayLength != 0) {
                //Copies the contents of the questionOrderArray to the tempNewArray
                System.arraycopy(questionOrderArray, 0, tempNewArray, 0, questionOrderArrayLength);
            }

            //Adds the new row stored in the newQuestionIdRow to the tempNewArray
            tempNewArray[tempNewArray.length -1] = newQuestionIdRowShuffled;
            //Sets the questionOrderArray to the tempNewArray
            questionOrderArray = tempNewArray;
        }

        //Calls the method so that the newly generated questionOrderArray is stored in the database
        saveQuestionOrder();
        //Calls the method so that the newly generated cardColourCountArray is stored in the database
        saveQuestionCountOrder();
    }

    //This method will read the blob data from questionOrder field of the gameBoards table
    //and will then convert the data back to a 2d array
    public void loadQuestionOrder(DBHelper passedDBHelper, String passedBoardId){
        //The the class dbHelper to the passed dbHelper
        dbHelper = passedDBHelper;
        //Sets the class idOfBoard to the passedBoardId
        idOfBoard = passedBoardId;

        //Store the card colour values e.g.(0,1) for the passedBoardId in the cardColourArray
        cardColoursArray = (dbHelper.getBoardCardColours(passedBoardId)).toArray(new String[0]);

        //This reads the data for the questionOrderArray from the database
        byte[] twoDSerialisedData = dbHelper.getBoardQuestionOrder(passedBoardId);

        if (twoDSerialisedData == null){
            Log.e("blobDataNull", "The blob returned from the database for the questionOrderArray is null");
        }else{
            //This code calls the method that will convert data from a byte/blob to a 2d array and then
            //updates the questionOrderArray class 2d array to the 2d array read from the gameBoard
            //table for the given game board id
            String[][] returnedTwoDimensionData = (String[][]) deserialiseArray(twoDSerialisedData);
            //Updates the questionOrderArray to the array that was read from the database
            questionOrderArray = returnedTwoDimensionData;
        }

        //This reads the data for the cardColourCountArray from the database
        byte[] oneDSerialisedData = dbHelper.getBoardQuestionCountOrder(passedBoardId);

        if (oneDSerialisedData == null){
            Log.e("blobDataNull", "The blob returned from the database for the cardColourCountArray is null");
        }else{
            //This code calls the method that will convert data from a byte/blob toa 1d array and then
            //updates the cardColourCountArray class 1d array to the 1d array read from the gameBoard
            //table for the given game board id
            int[] returnedOneDimensionData = (int[]) deserialiseArray(oneDSerialisedData);
            //Updates the cardColourCountArray to the array that was read from the database
            cardColourCountArray = returnedOneDimensionData;
        }
    }

    //This method will get the next question for the specified card colour from the database
    public List<String> getQuestionAndAnswers(String passedCardColour, Context passedContext){
        //This variable stores the row index location that should be used when reading the question id from the array
        //if this isn't set to the correct value then it will end up reading the wrong colour question from the database
        int questionOrderRowIndex = 0;

        //Loops through the cardColoursArray to find the index of the passedCardColour
        for(int n = 0; n < cardColoursArray.length; n++){
            //If the passedCardColour is found then the questionOrderRowIndex is set to the current index
            if(cardColoursArray[n].equals(passedCardColour)){
                questionOrderRowIndex = n;
                break;
            }
        }

        Log.e("cardColourArray", String.join(" ", cardColoursArray));
        //Reads the questionId from the questionOrderArray with the correct row set depending on the card colour and the index of the
        //row set correctly depending on how far a user has already got into the row

        String questionIdToRead = null;
        List<String> resultQuestionAndAnsList = null;
        try {
            questionIdToRead = questionOrderArray[questionOrderRowIndex][cardColourCountArray[questionOrderRowIndex]];
            resultQuestionAndAnsList = dbHelper.getQuestionByColour(passedCardColour, questionIdToRead);
            //Increase the count so that the next time this colour question is asked it will ask the next one int the row of the questionOrderArray for this colour
            //If the new cardColourCountArray is large than the length of the row within the questionOrderArray then it sets it back to 0
            cardColourCountArray[questionOrderRowIndex] = (cardColourCountArray[questionOrderRowIndex] + 1) % questionOrderArray[questionOrderRowIndex].length;
        }catch (ArrayIndexOutOfBoundsException e){
            Log.e("questionOfColourError", "Question of space colour doesn't exist" + e);
            Toast.makeText(passedContext, "No Question Of Given Space Colour Exists", Toast.LENGTH_SHORT).show();
        }

        return resultQuestionAndAnsList;
    }
}
