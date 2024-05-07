package edu.uob.Utils;

public class UtilityClass {

    public static boolean checkIfNormalActionWord(String name, String ignore){
        if(name.equals("look") || name.equals("get") || name.equals("drop") || name.equals("inv") || name.equals("goto")
                || name.equals("inventory")){
            return !name.equals(ignore);
        }
        return false;
    }
}
