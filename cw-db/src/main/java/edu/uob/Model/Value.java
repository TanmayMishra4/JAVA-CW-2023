package edu.uob.Model;

import edu.uob.AllEnums.ValueType;

import static edu.uob.AllEnums.ValueType.*;

public class Value implements AutoCloseable{
    public String getStringVal() {
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

    public Value(NULLObject nullValVal) {
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
        stringVal = String.valueOf(boolVal);
        valueType = BOOLEAN;
        this.boolVal = boolVal;
    }
    public Value(){

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
}
