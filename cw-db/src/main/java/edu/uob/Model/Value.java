package edu.uob.Model;

import edu.uob.AllEnums.SQLComparator;
import edu.uob.AllEnums.ValueType;
import edu.uob.AllExceptions.DBExceptions.CannotCompareValuesException;
import edu.uob.AllExceptions.DBExceptions.DBException;
import edu.uob.AllExceptions.DBExceptions.ValueTypeInconsistent;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static edu.uob.AllEnums.ValueType.*;

public class Value{
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

    public boolean equals(Value other) throws DBException{
        ValueType type1 = this.valueType;
        ValueType type2 = other.valueType;
        try{
            if(type1 == FLOAT ||  type2 == FLOAT){
                double val1 = Double.parseDouble(this.stringVal);
                double val2 = Double.parseDouble(other.stringVal);
                return val1 == val2;
            }
            else if(type1 == INTEGER && type2 == INTEGER){
                int val1 = Integer.parseInt(this.stringVal);
                int val2 = Integer.parseInt(other.stringVal);
                return val1 == val2;
            }
            else if(type1 == STRING && type2 == STRING){
                return this.stringVal.equals(other.getStringVal());
            }
            else if(type1 == BOOLEAN && type2 == BOOLEAN){
                return this.boolVal == other.boolVal;
            }
            else if((type1 == NULL && (type2 == STRING || type2 == NULL)) || (type2 == NULL && (type1 == STRING || type1 == NULL))){
                return this.stringVal.equals("") && other.stringVal.equals("");
            }
            else throw new ValueTypeInconsistent();
        }
        catch(Exception ignored){ throw new CannotCompareValuesException();}
    }

    public boolean compareFunc(SQLComparator sqlComparator, Value value) throws DBException {
        return switch (sqlComparator) {
            case EQUALS -> this.equals(value);
            case LESS_THAN -> this.lesser(value);
            case GREATER_THAN -> this.greater(value);
            case GREATER_EQUALS -> this.equals(value) || this.greater(value);
            case LESS_EQUALS -> this.equals(value) || this.lesser(value);
            case LIKE -> this.like(value);
            case NOT_EQUALS -> !this.equals(value);
        };
    }

    private boolean like(Value value) throws DBException{
        if(value.valueType != this.valueType) throw new ValueTypeInconsistent();
        if(value.valueType != STRING) throw new CannotCompareValuesException();
        Pattern pattern = Pattern.compile(".*"+value.toString()+".*");
        Matcher match = pattern.matcher(this.toString());
        return match.find();
    }

    private boolean greater(Value other) throws DBException{
        ValueType type1 = this.valueType;
        ValueType type2 = other.valueType;
        try{
            if(type1 == FLOAT || type2 == FLOAT){
                double val1 = Double.parseDouble(this.stringVal);
                double val2 = Double.parseDouble(other.stringVal);
                return val1 > val2;
            }
            else if(type1 == INTEGER && type2 == INTEGER){
                int val1 = Integer.parseInt(this.stringVal);
                int val2 = Integer.parseInt(other.stringVal);
                return val1 > val2;
            }
            else if(type1 == STRING && type2 == STRING){
                return this.stringVal.compareTo(other.getStringVal()) > 0;
            }
            else throw new CannotCompareValuesException();
        }
        catch(Exception ignored){ throw new CannotCompareValuesException();}
    }

    private boolean lesser(Value other)  throws DBException{
        ValueType type1 = this.valueType;
        ValueType type2 = other.valueType;
        try{
            if(type1 == FLOAT || type2 == FLOAT){
                double val1 = Double.parseDouble(this.stringVal);
                double val2 = Double.parseDouble(other.stringVal);
                return val1 < val2;
            }
            else if(type1 == INTEGER && type2 == INTEGER){
                int val1 = Integer.parseInt(this.stringVal);
                int val2 = Integer.parseInt(other.stringVal);
                return val1 < val2;
            }
            else if(type1 == STRING && type2 == STRING){
                return this.stringVal.compareTo(other.getStringVal()) < 0;
            }
            else throw new CannotCompareValuesException();
        }
        catch(Exception ignored){ throw new CannotCompareValuesException();}
    }
}
