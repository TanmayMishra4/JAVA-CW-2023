package edu.uob.AllExceptions.DBExceptions;

public class ColumnNotFoundException extends DBException{
    public ColumnNotFoundException(){
        super("Column does not exist");
    }
}
