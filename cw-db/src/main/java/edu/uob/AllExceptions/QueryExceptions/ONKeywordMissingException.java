package edu.uob.AllExceptions.QueryExceptions;

import java.io.Serial;

public class ONKeywordMissingException extends SQLQueryException {
    @Serial
    private static final long serialVersionUID = 62389982;
    public ONKeywordMissingException(){
        super("ON Keyword Missing in Query");
    }
}
