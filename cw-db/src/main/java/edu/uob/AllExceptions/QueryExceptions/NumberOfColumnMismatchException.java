package edu.uob.AllExceptions.QueryExceptions;

public class NumberOfColumnMismatchException extends SQLQueryException {
    public NumberOfColumnMismatchException(){
        super("Number of Columns mismatch with the table");
    }
}
