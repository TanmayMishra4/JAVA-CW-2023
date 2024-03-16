package edu.uob.AllExceptions.DBExceptions;

import java.io.Serial;

public class ValueTypeInconsistent extends DBException{
    @Serial
    private static final long serialVersionUID = 62389982;
    public ValueTypeInconsistent(){
        super("Values being compared do not have the same type");
    }
}
