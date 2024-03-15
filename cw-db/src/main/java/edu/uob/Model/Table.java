package edu.uob.Model;

import edu.uob.AllEnums.SQLComparator;
import edu.uob.AllExceptions.DBExceptions.*;
import edu.uob.AllExceptions.QueryExceptions.SQLQueryException;
import edu.uob.AllExceptions.QueryExceptions.DuplicatePrimaryKeyException;
import edu.uob.Utils.PrimaryKeyGenerator;
import edu.uob.Utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

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

    public void addColumn(String columnName) throws DBException { //TODO figure out a way to compare columns with ignore case
        if (columnName.equalsIgnoreCase(primaryKey)) return;
        if (columnsMap.containsKey(primaryKey) && columnName.equalsIgnoreCase(primaryKey))
            throw new DBException("Cannot add column id");
        if (hasColumn(columnName)) throw new DuplicateColumnNameException();
        columnNames.add(columnName);
        columnsMap.put(columnName, new Column(columnName));
        addDefaultValueToColumn(columnName);
    }

    private void addDefaultValueToColumn(String columnName) throws DBException {
        Column column = getColumn(columnName);
        for (int primaryKey : primaryKeys) {
            column.addValue(new Value(new NULLObject()), primaryKey);
        }
    }

    public void addData(String[] rowOfValues) throws DBException {
        if (rowOfValues.length != columnNames.size() - 1)
            throw new NumberOfColumnMismatchException();
        try {
            Value primaryKeyLiteral = pkgen.getPrimaryKey();
            int primaryKeyValue = primaryKeyLiteral.getIntVal();
            getColumn("id").addValue(primaryKeyLiteral, primaryKeyValue);
            if (!primaryKeys.add(primaryKeyValue))
                throw new DuplicatePrimaryKeyException(primaryKeyValue);

            for (int index = 0; index < rowOfValues.length; index++) {
                String columnValue = rowOfValues[index];
                Value value = Utils.getValue(columnValue);
                String columnName = columnNames.get(index + 1);
                Column column = getColumn(columnName);
                column.addValue(value, primaryKeyValue);
            }
        } catch (SQLQueryException d) {
            throw new DBException(d.getMessage());
        } catch (Exception e) {
            throw new DBException("Primary Key Error");
        }
    }

    public void loadDataRows(String[] rowOfValues) throws DBException {
        if (rowOfValues.length != columnNames.size()) throw new NumberOfColumnMismatchException();
        try {// TODO refactor this method to be the same as above
            int primaryKeyValue = Integer.parseInt(rowOfValues[0]);
            for (int index = 0; index < rowOfValues.length; index++) {
                String columnValue = rowOfValues[index];
                Value value = Utils.getValue(columnValue);
                String columnName = columnNames.get(index);
                Column column = getColumn(columnName);
                column.addValue(value, primaryKeyValue);
            }
            primaryKeys.add(primaryKeyValue);
        } catch (SQLQueryException d) {
            throw new DBException(d.getMessage());
        } catch (Exception e) {
            throw new DBException("Primary Key Error");
        }
    }

    public HashMap<String, Column> getColumnsMap() {
        return columnsMap;
    }

    public List<String> getColumnNames() {
        return columnNames;
    }


    public void removeColumn(String columnName) throws DBException {
        if (columnName.equals("id")) throw new RemovalOfPrimaryKeyException();
        if (!hasColumn(columnName)) throw new ColumnNotFoundException();
        columnNames.remove(columnName);
        columnsMap.remove(columnName);
    }

    public void removeRowsWithIndex(List<Integer> indexesToDelete) throws DBException {
        // TODO to be implemented
        for (Column column : columnsMap.values()) {
            column.deleteValuesWithIndex(indexesToDelete);
        }
        indexesToDelete.forEach(primaryKeys::remove);
    }

    public String selectQuery(List<String> wildAttributes) throws Exception {
        if (wildAttributes.get(0).equals("*")) {
            wildAttributes.clear();
            wildAttributes.addAll(columnNames);
        }
        return selectQuery(wildAttributes, primaryKeys);
    }

    public String selectQuery(List<String> wildAttributes, HashSet<Integer> filteredValues) throws Exception {
        if (wildAttributes.get(0).equals("*")) {
            wildAttributes.clear();
            wildAttributes.addAll(columnNames);
        }
        for (String columnName : wildAttributes) {
            if (!hasColumn(columnName)) throw new ColumnNotFoundException();
        }

        List<Integer> indexList = new ArrayList<>(primaryKeys);
        indexList.sort(Integer::compareTo);
        StringBuilder sb = new StringBuilder();
        extractColNames(wildAttributes, sb);
        for (int index : indexList) {
            if (filteredValues.contains(index))
                extractRow(wildAttributes, index, sb);
        }
        sb.append("\n");
        return sb.toString();
    }

    private void extractColNames(List<String> wildAttributes, StringBuilder sb) throws Exception {
        int size = wildAttributes.size();
        for (int index = 0; index < size; index++) {
            String columnName = wildAttributes.get(index);
            sb.append(columnName);
            if (index == size - 1) sb.append("\n");
            else sb.append("\t");
        }
    }

    private void extractRow(List<String> wildAttributes, int index, StringBuilder sb) throws Exception {
        int size = wildAttributes.size();
        for (int colIndex = 0; colIndex < size; colIndex++) {
            Column column = getColumn(wildAttributes.get(colIndex));
            Value value = column.getValue(index);
            sb.append(value.getStringVal());
            if (colIndex == size - 1) sb.append("\n");
            else sb.append("\t");
        }
    }

    public List<Integer> filter(String columnName, SQLComparator sqlComparator, Value value) throws DBException {
        if (!hasColumn(columnName)) throw new ColumnNotFoundException();
        return getColumn(columnName).filter(sqlComparator, value);
    }

    public void update(List<NameValuePair> nameValuePairList, List<Integer> resultSet) throws Exception {
        for (NameValuePair pair : nameValuePairList) {
            String columnName = pair.getColumnName();
            if (!hasColumn(columnName)) throw new ColumnNotFoundException();
            Column column = getColumn(columnName);
            Value updatedValue = pair.getValue();
            column.update(updatedValue, resultSet);
        }
    }

    public HashSet<Integer> getPrimaryKeys() {
        return primaryKeys;
    }

    public List<Column> getColumnList() throws DBException {
        List<Column> columnList = new ArrayList<>();
        try {
            for (String columnName : columnNames) { // Done this way to get columns in order
                columnList.add(getColumn(columnName));
            }
            return columnList;
        } catch (Exception ignored) {
            throw new ColumnNotFoundException();
        }
    }

    public Column getColumn(String columnName) throws DBException {
        try {
            for(String storedColName : columnNames){
                if(columnName.equalsIgnoreCase(storedColName))
                    return columnsMap.get(storedColName);
            }
            throw new ColumnNotFoundException();
        } catch (Exception e) {
            throw new ColumnNotFoundException();
        }
    }

    public void setPkGenerator(int initialValue) {
        pkgen = new PrimaryKeyGenerator(initialValue);
    }

    public boolean hasColumn(String name) {
        for (String columnName : columnsMap.keySet()) {
            if (name.equalsIgnoreCase(columnName)) return true;
        }
        return false;
    }
}
