package edu.uob.AllExceptions.QueryExceptions;

import java.io.Serial;

public class InvalidComparatorException extends SQLQueryException {
    @Serial
    private static final long serialVersionUID = 62389982;
    public InvalidComparatorException(){
        super("Invalid SQL Comparator");
    }
}
