package edu.uob.DBExceptions;

public class TableNotFoundException extends DBException {
    public TableNotFoundException(){
        super("Table Does Not Exist");
    }
}
