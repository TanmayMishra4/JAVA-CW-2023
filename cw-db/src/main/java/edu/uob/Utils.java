package edu.uob;

import static edu.uob.ResponseType.ERROR;

public class Utils {
    static String generateResponse(ResponseType type, String message){
        if(type == ERROR){
            return "[ERROR: " + message;
        }
        else{
            return "[OK]: " + message;
        }
    }
}
