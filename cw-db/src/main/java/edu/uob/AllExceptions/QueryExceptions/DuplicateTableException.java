package edu.uob.AllExceptions.QueryExceptions;

public class DuplicateTableException extends SQLQueryException {
    public DuplicateTableException(){
        super("Duplicate Table Names found");
    }
}
