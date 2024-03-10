package edu.uob.DBExceptions;

public class DuplicateTableException extends DBException{
    public DuplicateTableException(){
        super("Duplicate Table Names found");
    }
}
