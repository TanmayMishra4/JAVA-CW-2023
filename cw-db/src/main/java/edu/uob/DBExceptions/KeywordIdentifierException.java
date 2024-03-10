package edu.uob.DBExceptions;

public class KeywordIdentifierException extends DBException{
    public KeywordIdentifierException(){
        super("Identifier Name cannot be a keyword");
    }
}
