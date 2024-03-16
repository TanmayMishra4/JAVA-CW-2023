package edu.uob.AllExceptions.DBExceptions;

import java.io.Serial;

public class TableDoesNotExistException extends DBException{
    @Serial
    private static final long serialVersionUID = 62389982;
    public TableDoesNotExistException(){
        super("Table does not exist");
    }
}
