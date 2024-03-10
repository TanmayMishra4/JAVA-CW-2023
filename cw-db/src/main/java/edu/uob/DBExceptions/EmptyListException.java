package edu.uob.DBExceptions;

public class EmptyListException extends DBException{
    public EmptyListException(String message){
        super("Empty " + message + " provided");
    }
}
