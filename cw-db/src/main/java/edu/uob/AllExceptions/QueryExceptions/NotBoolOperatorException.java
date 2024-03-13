package edu.uob.AllExceptions.QueryExceptions;

public class NotBoolOperatorException extends SQLQueryException {
    public NotBoolOperatorException(){
        super("Not a Boolean Operator ");
    }
}
