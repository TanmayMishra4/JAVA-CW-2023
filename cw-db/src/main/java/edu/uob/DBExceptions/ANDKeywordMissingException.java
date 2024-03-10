package edu.uob.DBExceptions;

public class ANDKeywordMissingException extends DBException{
    public ANDKeywordMissingException(){
        super("AND Keyword missing in Query");
    }
}
