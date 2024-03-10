package edu.uob.DBExceptions;

public class NumberOfColumnMismatchException extends DBException{
    public NumberOfColumnMismatchException(){
        super("Number of Columns mismatch with the table");
    }
}
