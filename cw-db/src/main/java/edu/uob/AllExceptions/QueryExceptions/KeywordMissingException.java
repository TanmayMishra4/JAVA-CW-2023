package edu.uob.AllExceptions.QueryExceptions;

public class KeywordMissingException extends SQLQueryException {
    public KeywordMissingException(String message){
        super(message + " Keyword missing in Query");
    }
}
