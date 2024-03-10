package edu.uob.DBExceptions;

public class IllegalValueTypeException extends DBException{
    public IllegalValueTypeException(){
        super("Illegal Value Type provided");
    }
}
