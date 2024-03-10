package edu.uob.DBExceptions;

public class EqualsMissingException extends DBException{
    public EqualsMissingException(){
        super("Equals [=] sign missing in query");
    }
}
