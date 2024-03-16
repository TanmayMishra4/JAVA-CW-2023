package edu.uob.AllExceptions.QueryExceptions;

import java.io.Serial;

public class KeywordIdentifierException extends SQLQueryException {
    @Serial
    private static final long serialVersionUID = 62389982;
    public KeywordIdentifierException(){
        super("Identifier Name cannot be a keyword");
    }
}
