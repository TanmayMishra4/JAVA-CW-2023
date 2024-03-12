package edu.uob.AllExceptions.QueryExceptions;

public class InvalidIdentifierNameException extends SQLQueryException {
    public InvalidIdentifierNameException(){
        super("Invalid Identifier Name provided. Identifier name contains Illegal characters");
    }
}
