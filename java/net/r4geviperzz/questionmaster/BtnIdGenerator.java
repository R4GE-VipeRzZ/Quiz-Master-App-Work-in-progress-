package net.r4geviperzz.questionmaster;

import java.util.UUID;

//This class is used to generate an id for dynamically created button
public class BtnIdGenerator {
    //This method is used to generate a random positive number that can be assigned to a programmatically generated button
    //Math.abs gets the absolute value of the generate hash code meaning that the number that is generated is positive
    //the number needs to be positive as when running on API 25 or lower findViewById returns null if you try to find a view with a negative id
    public static int generateBtnId(){
        int generatedBtnId = Math.abs(UUID.randomUUID().hashCode());
        return generatedBtnId;
    }
}
