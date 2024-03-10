package edu.uob.DBExceptions;

public class DuplicatePrimaryKeyException extends DBException{
    public DuplicatePrimaryKeyException(Integer a){
        super("Primary Key values cannot be duplicate, found value "+a+" in multiple entries");
    }
}
