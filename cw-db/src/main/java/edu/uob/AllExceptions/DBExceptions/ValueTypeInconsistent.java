package edu.uob.AllExceptions.DBExceptions;

public class ValueTypeInconsistent extends DBException{
    public ValueTypeInconsistent(){
        super("Values being compared do not have the same type");
    }
}
