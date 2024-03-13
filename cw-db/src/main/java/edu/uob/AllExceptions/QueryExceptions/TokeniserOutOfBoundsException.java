package edu.uob.AllExceptions.QueryExceptions;

public class TokeniserOutOfBoundsException extends SQLQueryException {
    public TokeniserOutOfBoundsException(){
        super("Tokeniser went out of bounds");
    }
}
