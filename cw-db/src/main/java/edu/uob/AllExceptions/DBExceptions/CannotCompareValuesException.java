package edu.uob.AllExceptions.DBExceptions;

public class CannotCompareValuesException extends DBException{
    public  CannotCompareValuesException(){
        super("The operation provided in the query does not apply to values being compared");
    }
}
