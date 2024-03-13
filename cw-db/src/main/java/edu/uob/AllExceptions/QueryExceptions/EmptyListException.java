package edu.uob.AllExceptions.QueryExceptions;

public class EmptyListException extends SQLQueryException {
    public EmptyListException(String message){
        super("Empty " + message + " provided");
    }
}
