package edu.uob.AllExceptions.DBExceptions;

public class CannotDropTableException extends DBException{
    public CannotDropTableException(){
        super("Cannot Drop Table");
    }
}
