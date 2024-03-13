package edu.uob.Model;

import edu.uob.AllEnums.SQLComparator;
import edu.uob.AllExceptions.DBExceptions.DBException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Column {
    String name;
    HashMap<Integer, Value> values;

    Column(String name){
        this.name = name;
        values = new HashMap<>();
    }

    public void addValue(Value val, Integer primaryKeyValue) throws DBException{
        try{
            values.put(primaryKeyValue, val);
        }
        catch(Exception e){
            throw new DBException("Cannot add value" + val + " to Column "+name);
        }
    }

    public String getName() {
        return name;
    }

    public Value getValue(int index) throws DBException{
        // returns value at index
        try{
            return values.get(index);
        }
        catch(Exception ignored){
            throw new DBException("Index not Found in Column");
        }
    }

    public HashMap<Integer, Value> getValues() {
        return values;
    }

    public void deleteValuesWithIndex(List<Integer> pkSet){
        for(Integer primaryKey : pkSet){
            values.remove(primaryKey);
        }
    }

    public List<Integer> filter(SQLComparator sqlComparator, Value value) throws DBException {
        List<Integer> result = new ArrayList<>();
        for(var entry : values.entrySet()){
            Integer entryPrimaryKey = entry.getKey();
            Value entryValue = entry.getValue();
            if(entryValue.compareFunc(sqlComparator, value)){
                result.add(entryPrimaryKey);
            }
        }
        return result;
    }

    public void update(Value updatedValue, List<Integer> resultSet) {
        for(int primaryKeyValue : resultSet){
            if(values.containsKey(primaryKeyValue)){
                values.put(primaryKeyValue, updatedValue);
            }
        }
    }
}
