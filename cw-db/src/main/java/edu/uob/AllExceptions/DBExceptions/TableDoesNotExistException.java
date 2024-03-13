package edu.uob.AllExceptions.DBExceptions;

public class TableDoesNotExistException extends DBException{
    public TableDoesNotExistException(){
        super("Table does not exist");
    }
}
