package edu.uob.DBExceptions;

import edu.uob.Service.Tokeniser;

public class TokeniserOutOfBoundsException extends DBException{
    public TokeniserOutOfBoundsException(){
        super("Tokeniser went out of bounds");
    }
}
