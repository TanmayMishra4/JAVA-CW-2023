package edu.uob.AllExceptions.QueryExceptions;

import java.io.Serial;

public class KeywordMissingException extends SQLQueryException {
    @Serial
    private static final long serialVersionUID = 62389982;
    public KeywordMissingException(String message){
        super(message + " Keyword missing in Query");
    }
}
