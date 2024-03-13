package edu.uob.AllExceptions.DBExceptions;

import edu.uob.AllExceptions.DBExceptions.DBException;

public class NumberOfColumnMismatchException extends DBException {
    public NumberOfColumnMismatchException(){
        super("Number of Columns mismatch with the table");
    }
}
