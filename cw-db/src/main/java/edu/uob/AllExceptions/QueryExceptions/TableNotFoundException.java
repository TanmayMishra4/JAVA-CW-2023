package edu.uob.AllExceptions.QueryExceptions;

public class TableNotFoundException extends SQLQueryException {
    public TableNotFoundException(){
        super("Table Does Not Exist");
    }
}
