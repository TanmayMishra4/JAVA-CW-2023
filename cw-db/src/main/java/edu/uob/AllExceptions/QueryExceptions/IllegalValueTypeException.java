package edu.uob.AllExceptions.QueryExceptions;

public class IllegalValueTypeException extends SQLQueryException {
    public IllegalValueTypeException(){
        super("Illegal Value Type provided");
    }
}
