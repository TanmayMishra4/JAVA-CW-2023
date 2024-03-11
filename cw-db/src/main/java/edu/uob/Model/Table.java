package edu.uob.Model;

import edu.uob.AllExceptions.QueryExceptions.SQLQueryException;
import edu.uob.AllExceptions.QueryExceptions.DuplicatePrimaryKeyException;
import edu.uob.AllExceptions.QueryExceptions.NumberOfColumnMismatchException;
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

    public void addColumn(String columnName) throws SQLQueryException {
        if(columnName.equals(primaryKey)) return;
        if(columnsMap.containsKey(columnName)) throw new SQLQueryException("Duplicate Columns found");
        columnNames.add(columnName);
        columnsMap.put(columnName, new Column(columnName));
    }

    public void addData(String[] columnValues) throws SQLQueryException {
        if(columnValues.length != columnNames.size()) throw new NumberOfColumnMismatchException();
        try {
            Value primaryKeyLiteral = Utils.getIntegerLiteral(columnValues[0]);
            int primaryKeyValue = primaryKeyLiteral.getIntVal();
            if(!primaryKeys.add(primaryKeyValue)) throw new DuplicatePrimaryKeyException(primaryKeyValue);

            for(int index=1;index<columnValues.length;index++){
                String columnValue = columnValues[index];
                Value value = Utils.getValue(columnValue);
                String columnName = columnNames.get(index);
                Column column = columnsMap.get(columnName);
                column.addValue(value);
            }
        }
        catch(SQLQueryException d){
            throw d;
        }
        catch (Exception e){
            throw new SQLQueryException("Primary Key Error");
        }
    }
}
