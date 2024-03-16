package edu.uob.AllExceptions.DBExceptions;

import edu.uob.AllExceptions.DBExceptions.DBException;

import java.io.Serial;

public class NumberOfColumnMismatchException extends DBException {
    @Serial
    private static final long serialVersionUID = 62389982;
    public NumberOfColumnMismatchException(){
        super("Number of Columns mismatch with the table");
    }
}
