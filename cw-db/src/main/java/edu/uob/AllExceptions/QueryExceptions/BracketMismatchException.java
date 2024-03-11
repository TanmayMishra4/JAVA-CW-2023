package edu.uob.AllExceptions.QueryExceptions;

public class BracketMismatchException extends SQLQueryException {
    public BracketMismatchException(){
        super("Brackets mismatch");
    }
}
