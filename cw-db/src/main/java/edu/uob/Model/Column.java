package edu.uob.Model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class Column {
    String name;
    HashMap<Integer, Value> pkToValueMap;
    List<Value> values;

    Column(String name){
        this.name = name;
        values = new ArrayList<>();
        pkToValueMap = new HashMap<>();
    }

    public void addValue(Value val){
        values.add(val);
    }

    public String getName() {
        return name;
    }

    public Value getValue(int index){
        return values.get(index);
    }

    public List<Value> getValues() {
        return values;
    }

    public void deleteValuesWithIndex(HashSet<Integer> pkSet){
        for(Integer pk : pkSet){
            if(pkToValueMap.containsKey(pk))
                pkToValueMap.remove(pk);
        }
    }
}
