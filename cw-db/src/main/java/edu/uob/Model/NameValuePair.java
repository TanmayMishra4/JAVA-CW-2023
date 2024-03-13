package edu.uob.Model;

public class NameValuePair {
    private String columnName;
    private Value value;

    public NameValuePair(String attributeName, Value value) {
        this.columnName = attributeName;
        this.value = value;
    }

    public String getColumnName() {
        return columnName;
    }

    public Value getValue() {
        return value;
    }
}
