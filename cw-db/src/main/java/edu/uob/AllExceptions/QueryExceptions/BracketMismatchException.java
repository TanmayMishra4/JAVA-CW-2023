package edu.uob.AllExceptions.QueryExceptions;

import java.io.Serial;

public class BracketMismatchException extends SQLQueryException {
    @Serial
    private static final long serialVersionUID = 62389982;
    public BracketMismatchException(){
        super("Brackets mismatch");
    }
}
