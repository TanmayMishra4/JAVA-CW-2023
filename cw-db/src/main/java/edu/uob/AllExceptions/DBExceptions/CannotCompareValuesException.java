package edu.uob.AllExceptions.DBExceptions;

import java.io.Serial;

public class CannotCompareValuesException extends DBException{
    @Serial
    private static final long serialVersionUID = 62389982;
    public  CannotCompareValuesException(){
        super("The operation provided in the query does not apply to values being compared");
    }
}
