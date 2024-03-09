package edu.uob;

import static edu.uob.ValueType.*;

public class ValueLiteral<E> implements AutoCloseable{
    private ValueType valueType;
    private E value;
    public ValueLiteral(E value){
        setValue(value);
        if(value instanceof String) setValueType(STRING);
        else if(value instanceof Boolean) setValueType(BOOLEAN);
        else if(value instanceof Double) setValueType(FLOAT);
        else if(value instanceof Integer) setValueType(INTEGER);
        else if(value instanceof NULLObject) setValueType(NULL); // TODO check for any other type of case involved
    }

    public ValueType getValueType() {
        return valueType;
    }

    public void setValueType(ValueType valueType) {
        this.valueType = valueType;
    }

    public E getValue() {
        return value;
    }

    public void setValue(E value) {
        this.value = value;
    }

    @Override
    public void close() throws Exception {

    }
}
