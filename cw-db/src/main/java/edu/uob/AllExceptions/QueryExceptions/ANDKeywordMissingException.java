package edu.uob.AllExceptions.QueryExceptions;

import java.io.Serial;

public class ANDKeywordMissingException extends SQLQueryException {
    @Serial
    private static final long serialVersionUID = 62389982;
    public ANDKeywordMissingException(){
        super("AND Keyword missing in Query");
    }
}
