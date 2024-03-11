package edu.uob.Controller;

import edu.uob.AllEnums.AlterationType;
import edu.uob.AllEnums.BoolOperator;
import edu.uob.AllEnums.SQLComparator;
import edu.uob.AllExceptions.DBExceptions.DBException;
import edu.uob.AllExceptions.DBExceptions.DuplicateColumnNameException;
import edu.uob.Model.Database;
import edu.uob.Model.NameValuePair;
import edu.uob.Model.Value;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DBController {
    private static Database activeDB;
    private IOController ioController;
    public DBController(){
        ioController = new IOController();
        activeDB = null;
    }

    public Database getActiveDB() {
        return activeDB;
    }

    public void setActiveDBName(String activeDB) {
        activeDB = activeDB;
    }

    public void setActiveDB(String activeDBName) throws DBException {
        // TODO some file IO shit to change activeDB
        try {
            activeDB = ioController.loadDatabase(activeDBName);
        }
        catch(Exception e){
            throw new DBException(e.getMessage());
        }
    }

    public void dropDB(String dbName) throws DBException{
        ioController.dropDB(dbName);
    }

    public void dropTable(String tableName) throws DBException{
        ioController.dropTable(activeDB.getName(), tableName);
    }

    public Database createDB(String dbName) throws DBException{
        ioController.createDB(dbName);
        return new Database(dbName);
    }

    public void createTable(String tableName) throws DBException{
        ioController.createTable(activeDB.getName(), tableName);
    }

    public void createTable(String tableName, List<String> attbrList) throws DBException{
        Set<String> columnVals = new HashSet<>();
        for(String columnVal : attbrList){
            if(!columnVals.add(columnVal) || columnVal.equalsIgnoreCase("id")) throw new DuplicateColumnNameException();
        }
        ioController.addColumnNames(activeDB.getName(), tableName, attbrList);
    }

    public void alterTable(String tableName, AlterationType alterationType) {
    }

    public void joinTables(String tableName1, String tableName2, String attributeName1, String attributeName2) {
    }

    public void insertValues(String tableName, List<Value> valueList) {
    }

    public HashSet<Value> filter(String attributeName, SQLComparator sqlComparator, Value value) {
        return null;
    }

    public HashSet<Value> filter(HashSet<Value> condition1Values, BoolOperator operator, HashSet<Value>condition2Values){
        return null;
    }

    public void deleteValuesFromTable(String tableName, HashSet<Value> valuesToDelete) {
    }

    public void update(String tableName, List<NameValuePair> nameValuePairList, HashSet<Value> resultSet) {
    }

    public void select(String tableName, List<String> wildAttributes) {
    }

    public void select(String tableName, List<String> wildAttributes, HashSet<Value> filtereredValues) {
    }
}
