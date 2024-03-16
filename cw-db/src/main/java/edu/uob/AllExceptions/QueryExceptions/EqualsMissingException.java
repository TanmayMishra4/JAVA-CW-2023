package edu.uob.AllExceptions.QueryExceptions;

import java.io.Serial;

public class EqualsMissingException extends SQLQueryException {
    @Serial
    private static final long serialVersionUID = 62389982;
    public EqualsMissingException(){
        super("Equals [=] sign missing in query");
    }
}
