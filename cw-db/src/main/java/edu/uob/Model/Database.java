package edu.uob.Model;

import edu.uob.AllExceptions.DBExceptions.DBException;
import edu.uob.AllExceptions.DBExceptions.DuplicateTablesException;
import edu.uob.AllExceptions.DBExceptions.TableDoesNotExistException;

import javax.xml.crypto.Data;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class Database {
    private String name;
    private HashMap<String, Table> tables;

    public Database(String name, HashMap<String, Table> tables){
        this.name = name;
        this.tables = tables;
    }
    public Database(String name){
        this(name, new HashMap<>());
    }

    public String getName() {
        return name;
    }
    public HashMap<String, Table> getTables() {
        return tables;
    }

    public void addTable(String tableName) throws DBException {
        if(tables.containsKey(tableName)) throw new DuplicateTablesException();
        Table tableEntry = new Table(tableName);
        this.tables.put(tableName, tableEntry);
    }

    public void addColumnToTable(String tableName, String columnName) throws DBException {
        if(!tables.containsKey(tableName)) throw new TableDoesNotExistException();
        Table table = tables.get(tableName);
        table.addColumn(columnName);
    }

    public void addDataToTable(String tableName, String[] rowofValues) throws DBException {
        if(!tables.containsKey(tableName)) throw new TableDoesNotExistException();
        Table table = tables.get(tableName);
        table.addData(rowofValues);
    }

    public void loadDataToTable(String tableName, String[] rowofValues) throws DBException {
        if(!tables.containsKey(tableName)) throw new TableDoesNotExistException();
        Table table = tables.get(tableName);
        table.loadDataRows(rowofValues);
    }

    public boolean hasTable(String tableName){
        return tables.containsKey(tableName);
    }

    public void removeTable(String tableName) throws DBException {
        try {
            tables.remove(tableName);
        }
        catch(Exception e){
            throw new TableDoesNotExistException();
        }
    }

    public void dropTable(String tableName) throws DBException{
        if(tables.containsKey(tableName)){
           tables.remove(tableName);
        }
        else throw new TableDoesNotExistException();
    }

    public void removeColumnFromTable(String tableName, String columnName) throws DBException{
        if(!tables.containsKey(tableName)) throw new TableDoesNotExistException();
        tables.get(tableName).removeColumn(columnName);
    }

    public void deleteFromTable(String tableName, HashSet<Integer> indexesToDelete) throws DBException{
        if(!tables.containsKey(tableName)) throw new TableDoesNotExistException();
        tables.get(tableName).removeRowsWithIndex(indexesToDelete);
    }

    public String selectQuery(String tableName, List<String> wildAttributes) throws DBException{
        if(!tables.containsKey(tableName)) throw new TableDoesNotExistException();
        try {
            return tables.get(tableName).selectQuery(wildAttributes);
        }
        catch(Exception e){
            throw new DBException("SELECT Query Cannot be executed");
        }
    }
}
