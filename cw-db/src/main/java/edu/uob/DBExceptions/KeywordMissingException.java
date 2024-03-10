package edu.uob.DBExceptions;

public class KeywordMissingException extends DBException{
    public KeywordMissingException(String message){
        super(message + " Keyword missing in Query");
    }
}
