package edu.uob.AllExceptions.DBExceptions;

import java.io.Serial;

public class DuplicateColumnNameException extends DBException{

    @Serial
    private static final long serialVersionUID = 62389982;
    public DuplicateColumnNameException(){
        super("Query contains duplicate query names");
    }
}
