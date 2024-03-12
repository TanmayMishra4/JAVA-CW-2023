package edu.uob.Model;

import edu.uob.AllExceptions.DBExceptions.ColumnNotFoundException;
import edu.uob.AllExceptions.DBExceptions.DBException;
import edu.uob.AllExceptions.DBExceptions.RemovalOfPrimaryKeyException;
import edu.uob.AllExceptions.QueryExceptions.SQLQueryException;
import edu.uob.AllExceptions.QueryExceptions.DuplicatePrimaryKeyException;
import edu.uob.AllExceptions.DBExceptions.NumberOfColumnMismatchException;
import edu.uob.Utils.PrimaryKeyGenerator;
import edu.uob.Utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class Table {
    private PrimaryKeyGenerator pkgen;
    private String name;
    public static final String primaryKey = "id";
    private HashSet<Integer> primaryKeys;
    private List<String> columnNames;
    private HashMap<String, Column> columnsMap;

    public String getName() {
        return name;
    }

    public Table(String name) {
        pkgen = new PrimaryKeyGenerator();
        this.name = name;
        columnsMap = new HashMap<>();

        primaryKeys = new HashSet<>();
        columnNames = new ArrayList<>();
        columnNames.add(primaryKey);
        columnsMap.put(primaryKey, new Column(primaryKey));
    }

    public void addColumn(String columnName) throws DBException {
        if (columnName.equals(primaryKey)) return;
        if (columnsMap.containsKey(columnName)) throw new DBException("Duplicate Columns found");
        columnNames.add(columnName);
        columnsMap.put(columnName, new Column(columnName));
    }

    public void addData(String[] rowOfValues) throws DBException {
        if (rowOfValues.length != columnNames.size() - 1) throw new NumberOfColumnMismatchException();
        try {
            Value primaryKeyLiteral = pkgen.getPrimaryKey();
            int primaryKeyValue = primaryKeyLiteral.getIntVal();
            columnsMap.get("id").addValue(primaryKeyLiteral);
            if (!primaryKeys.add(primaryKeyValue)) throw new DuplicatePrimaryKeyException(primaryKeyValue);

            for (int index = 0; index < rowOfValues.length; index++) {
                String columnValue = rowOfValues[index];
                Value value = Utils.getValue(columnValue);
                String columnName = columnNames.get(index + 1);
                Column column = columnsMap.get(columnName);
                column.addValue(value);
            }
        } catch (SQLQueryException d) {
            throw new DBException(d.getMessage());
        } catch (Exception e) {
            throw new DBException("Primary Key Error");
        }
    }

    public void loadDataRows(String[] rowOfValues) throws DBException{
        if (rowOfValues.length != columnNames.size()) throw new NumberOfColumnMismatchException();
        try {// TODO refactor this method to be the same as above
            for (int index = 0; index < rowOfValues.length; index++) {
                String columnValue = rowOfValues[index];
                Value value = Utils.getValue(columnValue);
                String columnName = columnNames.get(index);
                Column column = columnsMap.get(columnName);
                column.addValue(value);
            }
        } catch (SQLQueryException d) {
            throw new DBException(d.getMessage());
        } catch (Exception e) {
            throw new DBException("Primary Key Error");
        }
    }

    public boolean hasAttribute(String attribute) {
        return columnNames.contains(attribute);
    }

    public HashMap<String, Column> getColumnsMap() {
        return columnsMap;
    }

    public List<String> getColumnNames() {
        return columnNames;
    }

    public void setColumnNames(List<String> columnNames) {
        this.columnNames = columnNames;
    }

    public void removeColumn(String columnName) throws DBException {
        if (columnName.equals("id")) throw new RemovalOfPrimaryKeyException();
        if (!columnsMap.containsKey(columnName)) throw new ColumnNotFoundException();
        columnNames.remove(columnName);
        columnsMap.remove(columnName);
    }

    public void removeRowsWithIndex(HashSet<Integer> indexesToDelete) throws DBException {
        // TODO to be implemented
        for (Column column : columnsMap.values()) {
            column.deleteValuesWithIndex(indexesToDelete);
        }
    }

    public String selectQuery(List<String> wildAttributes) throws Exception {
        if (wildAttributes.get(0).equals("*")) {
            wildAttributes.clear();
            wildAttributes.addAll(columnNames);
        }

        int maxRows = columnsMap.get("id").getValues().size();
        StringBuilder sb = new StringBuilder();
        extractRowNames(wildAttributes, sb);
        for (int index = 0; index < maxRows; index++) {
            extractRow(wildAttributes, index, sb, maxRows);
        }
        return sb.toString();
    }

    private void extractRowNames(List<String> wildAttributes, StringBuilder sb) throws Exception {
        int size = wildAttributes.size();
        for (int index = 0; index < size; index++) {
            String columnName = wildAttributes.get(index);
            sb.append(columnName);
            if(index == size - 1) sb.append("\n");
            else sb.append("\t");
        }
    }

    private void extractRow(List<String> wildAttributes, int index, StringBuilder sb, int maxRows) throws Exception{
        int size = wildAttributes.size();
        for (int colIndex = 0; colIndex < size; colIndex++) {
            Column column = columnsMap.get(wildAttributes.get(colIndex));
            sb.append(column.getValue(index).getStringVal());
            if(index != maxRows-1 && colIndex == size - 1) sb.append("\n");
            else sb.append("\t");
        }
    }
}
