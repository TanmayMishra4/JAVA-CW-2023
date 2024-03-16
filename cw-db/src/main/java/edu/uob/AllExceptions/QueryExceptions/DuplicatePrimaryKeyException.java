package edu.uob.AllExceptions.QueryExceptions;

import java.io.Serial;

public class DuplicatePrimaryKeyException extends SQLQueryException {
    @Serial
    private static final long serialVersionUID = 62389982;
    public DuplicatePrimaryKeyException(Integer a){
        super("Primary Key values cannot be duplicate, found value "+a+" in multiple entries");
    }
}
