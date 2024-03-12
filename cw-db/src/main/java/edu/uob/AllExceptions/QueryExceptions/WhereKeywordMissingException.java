package edu.uob.AllExceptions.QueryExceptions;

public class WhereKeywordMissingException extends SQLQueryException {
    public WhereKeywordMissingException(){
        super("WHERE Keyword Missing in Query");
    }
}
