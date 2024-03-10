package edu.uob.DBExceptions;

public class InvalidComparatorException extends DBException{
    public InvalidComparatorException(){
        super("Invalid SQL Comparator");
    }
}
