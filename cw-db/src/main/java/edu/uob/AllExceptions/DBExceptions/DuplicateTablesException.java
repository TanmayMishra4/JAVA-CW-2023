package edu.uob.AllExceptions.DBExceptions;

import edu.uob.AllExceptions.QueryExceptions.SQLQueryException;

public class DuplicateTablesException extends DBException{
    public DuplicateTablesException(){
        super("Duplicate Table names not allowed");
    }

    public static class TableNotFoundException extends SQLQueryException {
        public TableNotFoundException(){
            super("Table Does Not Exist");
        }
    }
}
