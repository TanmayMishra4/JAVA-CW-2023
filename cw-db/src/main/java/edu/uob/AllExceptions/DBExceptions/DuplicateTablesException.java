package edu.uob.AllExceptions.DBExceptions;

import edu.uob.AllExceptions.QueryExceptions.SQLQueryException;

import java.io.Serial;
@SuppressWarnings("serial")
public class DuplicateTablesException extends DBException{
    @Serial
    private static final long serialVersionUID = 62389982;
    public DuplicateTablesException(){
        super("Duplicate Table names not allowed");
    }

    public static class TableNotFoundException extends SQLQueryException {
        public TableNotFoundException(){
            super("Table Does Not Exist");
        }
    }
}
