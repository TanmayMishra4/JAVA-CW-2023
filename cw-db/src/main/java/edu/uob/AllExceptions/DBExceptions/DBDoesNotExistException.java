package edu.uob.AllExceptions.DBExceptions;

public class DBDoesNotExistException extends DBException{
    public DBDoesNotExistException(){
        super("Database Does not exist");
    }
}
