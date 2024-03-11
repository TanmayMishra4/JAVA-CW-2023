package edu.uob.AllExceptions.DBExceptions;

public class DuplicateColumnNameException extends DBException{
    public DuplicateColumnNameException(){
        super("Query contains duplicate query names");
    }
}
