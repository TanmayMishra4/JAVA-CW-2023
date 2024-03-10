package edu.uob.Model;

import edu.uob.DBExceptions.DBException;
import edu.uob.DBExceptions.DuplicateTableException;
import edu.uob.DBExceptions.TableNotFoundException;

import java.util.HashMap;

public class Database {
    private String name;
    private HashMap<String, Table> tables;
    public Database(String name){
        this.name = name;
        tables = new HashMap<>();
    }

    public String getName() {
        return name;
    }
    public HashMap<String, Table> getTables() {
        return tables;
    }

    public void addTable(String tableName) throws DBException{
        if(tables.containsKey(tableName)) throw new DuplicateTableException();
        Table tableEntry = new Table(tableName);
        this.tables.put(tableName, tableEntry);
    }

    public void addColumnToTable(String tableName, String columnName) throws DBException{
        if(!tables.containsKey(tableName)) throw new TableNotFoundException();
        Table table = tables.get(tableName);
        table.addColumn(columnName);
    }

    public void addDataToTable(String tableName, String[] columnValues) throws DBException {
        if(!tables.containsKey(tableName)) throw new TableNotFoundException();
        Table table = tables.get(tableName);
        table.addData(columnValues);
    }

}
