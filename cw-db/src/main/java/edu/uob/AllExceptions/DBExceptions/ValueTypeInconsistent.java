package edu.uob.AllExceptions.DBExceptions;

import edu.uob.AllEnums.ValueType;

public class ValueTypeInconsistent extends DBException{
    public ValueTypeInconsistent(){
        super("Values being compared do not have the same type");
    }
}
