package edu.uob.AllExceptions.QueryExceptions;

import java.io.Serial;

public class EmptyListException extends SQLQueryException {
    @Serial
    private static final long serialVersionUID = 62389982;
    public EmptyListException(String message){
        super("Empty " + message + " provided");
    }
}
