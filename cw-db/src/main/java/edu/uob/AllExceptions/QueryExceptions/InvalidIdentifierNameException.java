package edu.uob.AllExceptions.QueryExceptions;

import java.io.Serial;

public class InvalidIdentifierNameException extends SQLQueryException {
    @Serial
    private static final long serialVersionUID = 62389982;
    public InvalidIdentifierNameException(){
        super("Invalid Identifier Name provided. Identifier name contains Illegal characters");
    }
}
