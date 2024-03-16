package edu.uob.AllExceptions.QueryExceptions;

import java.io.Serial;

public class TokeniserOutOfBoundsException extends SQLQueryException {
    @Serial
    private static final long serialVersionUID = 62389982;
    public TokeniserOutOfBoundsException(){
        super("Tokeniser went out of bounds");
    }
}
