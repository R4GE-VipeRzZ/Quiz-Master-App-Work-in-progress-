package net.r4geviperzz.questionmaster;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

//This class is used to check the answers in the questionAndAnsList
public class CheckAnsList {
    //This method checks that the answers in the updated list are in alphabetical order
    public static Boolean checkAnswerInAlphabeticalOrder(List<String> passedQuestionAndAnsList){
        Boolean inAlphabeticalOrder = true;

        //Checks that the inputList isn't empty
        if(!passedQuestionAndAnsList.isEmpty() || passedQuestionAndAnsList != null){
            //Runs if the inputList isn't empty
            //This line gets the first string that is going to be compared against
            String previous = passedQuestionAndAnsList.get(1);

            //This for loop is used to iterate through the whole inputList, i starts at 2 as it needs to skip the first string
            //in index 0 as that is the question on the string in index 1 is what it is going to be compared against
            for (int i = 2; i < passedQuestionAndAnsList.size(); i++){
                //Gets the string that is next in the list after the previous string
                String current = passedQuestionAndAnsList.get(i);

                //Checks the previous string is in the correct alphabetical order compared with the current string
                if(current.compareTo(previous) < 0){
                    //This runs if the previous string wasn't in alphabetical order in comparison to the current string
                    inAlphabeticalOrder = false;
                    break;
                }
                previous = current;
            }
        }
        return inAlphabeticalOrder;
    }

    //This method is used to order the answers in the questionAndAnsList into alphabetical order
    public static List<String> orderAnswersByAscending(List<String> passedQuestionAndAnsList){
        //Gets the question out of the array
        String tempQuestion = passedQuestionAndAnsList.get(0);

        //Setups up a list that will be used to store the answers
        List<String> tempAnsList = new ArrayList<>();

        //Gets the answers out of the questionAndAnsList
        for (int n = 1; n < passedQuestionAndAnsList.size(); n++){
            tempAnsList.add(passedQuestionAndAnsList.get(n));
        }

        // Use Collections.sort with a custom comparator
        Collections.sort(tempAnsList, new Comparator<String>() {
            public int compare(String string1, String string2) {
                int result = 0;
                // Check if both strings contain a number
                if (string1.matches(".*\\d+.*") && string2.matches(".*\\d+.*")) {
                    // Split each string into two parts - the part before the number, and the number itself
                    String[] s1parts = string1.split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)");
                    String[] s2parts = string2.split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)");
                    // If the parts before the number are the same, compare the numbers
                    if (s1parts[0].equals(s2parts[0])) {
                        int num1 = Integer.parseInt(s1parts[1]);
                        int num2 = Integer.parseInt(s2parts[1]);
                        result = Integer.compare(num1, num2);
                        // Otherwise, compare the parts before the numbers
                    } else {
                        result = s1parts[0].compareTo(s2parts[0]);
                    }
                    // If neither string contains a number, compare them as usual
                } else {
                    result = string1.compareTo(string2);
                }
                return result;
            }
        });

        //Adds the question back to the start of the list
        tempAnsList.add(0, tempQuestion);

        return tempAnsList;
    }

    //This method checks if there are any duplicate values in the passed list
    public static Boolean checkForListDuplicates(List<String> passedQuestionAndAnsList){
        Boolean duplicateValues = false;

        //This set is used to keep track of unique values
        Set<String> set = new HashSet<>();

        for(String str : passedQuestionAndAnsList){
            //This if statement added each element in the list to the set
            if(!set.add(str)){
                //This runs if there value that was just added to the set already
                //exists in the set, meaning that is it a duplicate value
                duplicateValues = true;
                break;
            }
        }

        return duplicateValues;
    }

    //This method will ensure that all the strings in the list begin with a capital letter
    public static List<String> capitaliseListStrings(List<String> passedQuestionAndAnsList) {
        List<String> capitalizedList = new ArrayList<>();

        for (int i = 0; i < passedQuestionAndAnsList.size(); i++) {
            String indexString = passedQuestionAndAnsList.get(i);
            // Check if string is not empty and first character is a letter and not already uppercase
            if (!indexString.isEmpty() && Character.isLetter(indexString.charAt(0)) && !Character.isUpperCase(indexString.charAt(0))) {
                // Capitalize first character and concatenate remaining characters
                indexString = Character.toUpperCase(indexString.charAt(0)) + indexString.substring(1);
            }
            capitalizedList.add(indexString);
        }
        return capitalizedList;
    }
}
