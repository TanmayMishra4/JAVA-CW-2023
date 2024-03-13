package edu.uob.Model;

import edu.uob.AllEnums.SQLComparator;
import edu.uob.AllEnums.ValueType;
import edu.uob.AllExceptions.DBExceptions.CannotCompareValuesException;
import edu.uob.AllExceptions.DBExceptions.DBException;
import edu.uob.AllExceptions.DBExceptions.ValueTypeInconsistent;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static edu.uob.AllEnums.ValueType.*;

public class Value implements AutoCloseable{
    public String getStringVal() {
        return stringVal;
    }

    @Override
    public String toString(){
        return stringVal;
    }

    public Integer getIntVal() {
        return intVal;
    }

    public Double getFloatVal() {
        return floatVal;
    }

    public Boolean getBoolVal() {
        return boolVal;
    }

    public NULLObject getNullVal() {
        return nullVal;
    }

    public Value(NULLObject nullVal) {
        stringVal = String.valueOf(nullVal);
        valueType = NULL;
        this.nullVal = nullVal;
    }

    public Value(String stringVal) {
        valueType = STRING;
        this.stringVal = stringVal;
    }

    public Value(Integer intVal) {
        stringVal = String.valueOf(intVal);
        valueType = INTEGER;
        this.intVal = intVal;
    }

    public Value(Double floatVal) {
        stringVal = String.valueOf(floatVal);
        valueType = FLOAT;
        this.floatVal = floatVal;
    }

    public Value(Boolean boolVal) {
        stringVal = String.valueOf(boolVal).toUpperCase();
        valueType = BOOLEAN;
        this.boolVal = boolVal;
    }

    private ValueType valueType;
    private String stringVal;
    private Integer intVal;
    private Double floatVal;
    private Boolean boolVal;

    private NULLObject nullVal;

    public ValueType getValueType() {
        return valueType;
    }

    @Override
    public void close() throws Exception {

    }

    public boolean equals(Value other) throws DBException{
        if(this.valueType != other.getValueType()) throw new ValueTypeInconsistent();
        try{
            return switch (this.valueType) {
                case STRING -> this.stringVal.equals(other.getStringVal());
                case FLOAT -> this.floatVal.equals(Double.parseDouble(other.getStringVal()));
                case INTEGER -> this.intVal.equals(Integer.parseInt(other.getStringVal()));
                case BOOLEAN -> this.boolVal == Boolean.parseBoolean(other.getStringVal().toLowerCase());
                case NULL -> this.nullVal != null;
            };
        }
        catch(Exception e){ throw new DBException("Exception comparing values");}
    }

    public boolean compareFunc(SQLComparator sqlComparator, Value value) throws DBException {
        switch(sqlComparator){
            case EQUALS: return this.equals(value);
            case LESS_THAN: return this.lesser(value);
            case GREATER_THAN: return this.greater(value);
            case GREATER_EQUALS: return this.equals(value) || this.greater(value);
            case LESS_EQUALS: return this.equals(value) || this.lesser(value);
            case LIKE: return this.like(value);
            case NOT_EQUALS: return !this.equals(value);
        }
        return false;
    }

    private boolean like(Value value) throws DBException{
        if(value.valueType != this.valueType) throw new ValueTypeInconsistent();
        if(value.valueType != STRING) throw new CannotCompareValuesException();
        // TODO to be implemented
        Pattern pattern = Pattern.compile(".*"+value.toString()+".*");
        Matcher match = pattern.matcher(this.toString());
        return match.find();
    }

    private boolean greater(Value value) throws DBException{
        // TODO check if greater and lesses functions sshoudl be ijplemented for STRING
        if(value.valueType != INTEGER &&  value.valueType != FLOAT && value.valueType != STRING) throw new ValueTypeInconsistent();
        if(this.valueType != INTEGER &&  this.valueType != FLOAT && this.valueType != STRING) throw new ValueTypeInconsistent();
        if(this.valueType ==  INTEGER){
            int otherValue = Integer.parseInt(value.stringVal);
            return this.intVal > otherValue;
        }
        else if(this.valueType == FLOAT){
            double otherValue = Double.parseDouble(value.stringVal);
            return this.floatVal > otherValue;
        }
        else if(this.valueType == STRING){
            return this.stringVal.compareTo(value.getStringVal()) > 0;
        }
        else{
            throw new CannotCompareValuesException();
        }
    }

    private boolean lesser(Value value)  throws DBException{
        if(value.valueType != INTEGER &&  value.valueType != FLOAT && value.valueType != STRING) throw new ValueTypeInconsistent();
        if(this.valueType != INTEGER &&  this.valueType != FLOAT && this.valueType != STRING) throw new ValueTypeInconsistent();
        if(this.valueType ==  INTEGER){
            int otherValue = Integer.parseInt(value.stringVal);
            return this.intVal < otherValue;
        }
        else if(this.valueType == FLOAT){
            double otherValue = Double.parseDouble(value.stringVal);
            return this.floatVal < otherValue;
        }
        else if(this.valueType == STRING){
            return this.stringVal.compareTo(value.getStringVal()) < 0;
        }
        else{
            throw new CannotCompareValuesException();
        }
    }
}
