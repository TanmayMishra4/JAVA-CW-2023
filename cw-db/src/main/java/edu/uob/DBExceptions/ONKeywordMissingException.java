package edu.uob.DBExceptions;

public class ONKeywordMissingException extends DBException{
    public ONKeywordMissingException(){
        super("ON Keyword Missing in Query");
    }
}
