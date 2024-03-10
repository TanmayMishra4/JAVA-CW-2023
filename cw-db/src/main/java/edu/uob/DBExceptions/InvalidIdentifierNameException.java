package edu.uob.DBExceptions;

public class InvalidIdentifierNameException extends DBException{
    public InvalidIdentifierNameException(){
        super("Invalid Identifier Name provided. Identifier name contains Illegal characters");
    }
}
