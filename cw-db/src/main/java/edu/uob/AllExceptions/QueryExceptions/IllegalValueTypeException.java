package edu.uob.AllExceptions.QueryExceptions;

import java.io.Serial;

public class IllegalValueTypeException extends SQLQueryException {
    @Serial
    private static final long serialVersionUID = 62389982;
    public IllegalValueTypeException(){
        super("Illegal Value Type provided");
    }
}
