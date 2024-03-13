package edu.uob.AllExceptions.DBExceptions;

public class RemovalOfPrimaryKeyException extends DBException{
    public RemovalOfPrimaryKeyException(){
        super("Cannot remove primary key column");
    }
}
