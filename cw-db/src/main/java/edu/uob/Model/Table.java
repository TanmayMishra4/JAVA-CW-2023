package edu.uob.Model;

import edu.uob.DBExceptions.DBException;
import edu.uob.DBExceptions.DuplicatePrimaryKeyException;
import edu.uob.DBExceptions.NumberOfColumnMismatchException;
import edu.uob.Utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class Table {
    String name;
    public static final String primaryKey = "id";
    HashSet<Integer> primaryKeys;
    List<String> columnNames;
    HashMap<String, Column> columnsMap;

    public Table(String name){
        this.name = name;
        columnsMap = new HashMap<>();
        primaryKeys = new HashSet<>();
        columnNames = new ArrayList<>();
        columnNames.add(primaryKey);
        columnsMap.put(primaryKey, new Column<Integer>(primaryKey));
    }

    public void addColumn(String columnName) throws DBException {
        if(columnName.equals(primaryKey)) return;
        if(columnsMap.containsKey(columnName)) throw new DBException("Duplicate Columns found");
        columnNames.add(columnName);
        columnsMap.put(columnName, new Column(columnName));
    }

    public void addData(String[] columnValues) throws DBException{
        if(columnValues.length != columnNames.size()) throw new NumberOfColumnMismatchException();
        try {
            Value primaryKeyLiteral = Utils.getIntegerLiteral(columnValues[0]);
            int primaryKeyValue = primaryKeyLiteral.getIntVal();
            if(primaryKeys.contains(primaryKeyValue)) throw new DuplicatePrimaryKeyException(primaryKeyValue);

            for(int index=1;index<columnValues.length;index++){
                String columnValue = columnValues[index];
                Value value = Utils.getValueLiteral(columnValue);
                String columnName = columnNames.get(index);
                Column column = columnsMap.get(columnName);
                column.addValue(value);
            }
        }
        catch(DBException d){
            throw d;
        }
        catch (Exception e){
            throw new DBException("Primary Key Error");
        }
    }
}
