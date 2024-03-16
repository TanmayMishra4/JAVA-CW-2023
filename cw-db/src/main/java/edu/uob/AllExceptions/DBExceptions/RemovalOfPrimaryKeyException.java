package edu.uob.AllExceptions.DBExceptions;

import java.io.Serial;

public class RemovalOfPrimaryKeyException extends DBException{
    @Serial
    private static final long serialVersionUID = 62389982;
    public RemovalOfPrimaryKeyException(){
        super("Cannot remove primary key column");
    }
}
