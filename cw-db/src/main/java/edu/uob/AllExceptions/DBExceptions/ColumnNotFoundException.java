package edu.uob.AllExceptions.DBExceptions;

import java.io.Serial;

public class ColumnNotFoundException extends DBException{
    @Serial
    private static final long serialVersionUID = 62389982;
    public ColumnNotFoundException(){
        super("Column does not exist");
    }
}
