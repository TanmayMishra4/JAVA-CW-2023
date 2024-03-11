package edu.uob.AllExceptions.QueryExceptions;

public class KeywordIdentifierException extends SQLQueryException {
    public KeywordIdentifierException(){
        super("Identifier Name cannot be a keyword");
    }
}
