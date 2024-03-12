package edu.uob.AllExceptions.QueryExceptions;

public class ANDKeywordMissingException extends SQLQueryException {
    public ANDKeywordMissingException(){
        super("AND Keyword missing in Query");
    }
}
