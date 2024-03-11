package edu.uob.AllExceptions.QueryExceptions;

public class EqualsMissingException extends SQLQueryException {
    public EqualsMissingException(){
        super("Equals [=] sign missing in query");
    }
}
