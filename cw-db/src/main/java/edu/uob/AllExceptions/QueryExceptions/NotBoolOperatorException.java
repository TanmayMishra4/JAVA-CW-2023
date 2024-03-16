package edu.uob.AllExceptions.QueryExceptions;

import java.io.Serial;

public class NotBoolOperatorException extends SQLQueryException {
    @Serial
    private static final long serialVersionUID = 62389982;
    public NotBoolOperatorException(){
        super("Not a Boolean Operator ");
    }
}
