package edu.uob.AllExceptions.QueryExceptions;

public class DuplicatePrimaryKeyException extends SQLQueryException {
    public DuplicatePrimaryKeyException(Integer a){
        super("Primary Key values cannot be duplicate, found value "+a+" in multiple entries");
    }
}
