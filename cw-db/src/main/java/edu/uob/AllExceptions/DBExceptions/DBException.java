package edu.uob.AllExceptions.DBExceptions;

public class DBException extends Exception{
    DBException(){
        super("Database Exception");
    }
    public DBException(String message){
        super(message);
    }
}
