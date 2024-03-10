package edu.uob.DBExceptions;

public class WhereKeywordMissingException extends DBException{
    public WhereKeywordMissingException(){
        super("WHERE Keyword Missing in Query");
    }
}
