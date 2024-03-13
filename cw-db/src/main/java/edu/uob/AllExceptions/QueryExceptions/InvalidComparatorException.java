package edu.uob.AllExceptions.QueryExceptions;

public class InvalidComparatorException extends SQLQueryException {
    public InvalidComparatorException(){
        super("Invalid SQL Comparator");
    }
}
