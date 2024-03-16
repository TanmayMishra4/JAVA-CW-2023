package edu.uob.AllExceptions.DBExceptions;

import java.io.Serial;

public class DBDoesNotExistException extends DBException{
    @Serial
    private static final long serialVersionUID = 62389982;
    public DBDoesNotExistException(){
        super("Database Does not exist");
    }
}
