package edu.uob.Model;

import java.util.ArrayList;
import java.util.List;

public class Column<ColumnValueType> {
    String name;
    List<ColumnValueType> values;

    Column(String name){
        this.name = name;
        values = new ArrayList<>();
    }

    public void addValue(ColumnValueType val){
        values.add(val);
    }
}
