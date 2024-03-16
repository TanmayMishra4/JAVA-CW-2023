package edu.uob.AllExceptions.DBExceptions;

import java.io.Serial;

public class DBException extends Exception{
    @Serial
    private static final long serialVersionUID = 62389982;
    public DBException(String message){
        super(message);
    }
}
