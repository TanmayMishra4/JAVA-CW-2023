package edu.uob.AllExceptions.QueryExceptions;

import java.io.Serial;

public class WhereKeywordMissingException extends SQLQueryException {
    @Serial
    private static final long serialVersionUID = 62389982;
    public WhereKeywordMissingException(){
        super("WHERE Keyword Missing in Query");
    }
}
