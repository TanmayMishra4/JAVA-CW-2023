package edu.uob.AllExceptions.QueryExceptions;

public class ONKeywordMissingException extends SQLQueryException {
    public ONKeywordMissingException(){
        super("ON Keyword Missing in Query");
    }
}
