package edu.uob.Model;

public class NameValuePair {
    private String attributeName;
    private Value value;

    public NameValuePair(String attributeName, Value value) {
        this.attributeName = attributeName;
        this.value = value;
    }

    public String getAttributeName() {
        return attributeName;
    }

    public Value getValue() {
        return value;
    }
}
