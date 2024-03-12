package edu.uob.Controller;

import edu.uob.AllEnums.AlterationType;
import edu.uob.AllEnums.BoolOperator;
import edu.uob.AllEnums.SQLComparator;
import edu.uob.AllExceptions.DBExceptions.DBException;
import edu.uob.AllExceptions.DBExceptions.DuplicateColumnNameException;
import edu.uob.Model.Database;
import edu.uob.Model.NameValuePair;
import edu.uob.Model.Value;
import edu.uob.Utils.Utils;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static edu.uob.AllEnums.AlterationType.ADD;

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
        try {
            activeDB = ioController.loadDatabase(activeDBName);
        }
        catch(Exception e){
            throw new DBException(e.getMessage());
        }
    }

    public void dropDB(String dbName) throws DBException{
        ioController.dropDB(dbName);
        if(activeDB != null && dbName.equals(activeDB.getName())) activeDB = null;
    }

    public void dropTable(String tableName) throws DBException {
        activeDB.dropTable(tableName);
        ioController.saveDB(activeDB);
    }

    public Database createDB(String dbName) throws DBException{
        String dbPathName = Utils.getDBFilePathName(dbName);
        File file = new File(dbPathName);
        if(file.exists()) throw new DBException("Database Already exists");
        Database db = new  Database(dbName);
        ioController.saveDB(db);
        return db;
    }

    public void createTable(String tableName) throws DBException {
        activeDB.addTable(tableName);
        ioController.saveDB(activeDB);
    }

    public void createTable(String tableName, List<String> attbrList) throws DBException {
        Set<String> columnVals = new HashSet<>();
        for (String columnVal : attbrList) {
            if (!columnVals.add(columnVal) || columnVal.equalsIgnoreCase("id"))
                throw new DuplicateColumnNameException();
        }
        createTable(tableName);
        for (String data : attbrList) {
            activeDB.addColumnToTable(tableName, data);
        }
        ioController.saveDB(activeDB);
    }
    public void alterTable(String tableName, AlterationType alterationType, String attributeName) throws DBException {
        if(alterationType == ADD){
            activeDB.addColumnToTable(tableName, attributeName);
        }
        else{
            activeDB.removeColumnFromTable(tableName, attributeName);
        }
    }

    public void joinTables(String tableName1, String tableName2, String attributeName1, String attributeName2) {
    }

    public void insertValues(String tableName, List<Value> valueList) throws DBException {
        String[] values = new String[valueList.size()];
        for(int index=0;index<valueList.size();index++){
            values[index] = valueList.get(index).toString();
        }
        activeDB.addDataToTable(tableName, values);
    }

    public HashSet<Integer> filter(String attributeName, SQLComparator sqlComparator, Value value) {
        return null;
    }

    public HashSet<Integer> filter(HashSet<Integer> condition1Values, BoolOperator operator, HashSet<Integer>condition2Values){
        return null;
    }

    public void deleteValuesFromTable(String tableName, HashSet<Integer> indexesToDelete) throws DBException {
        activeDB.deleteFromTable(tableName, indexesToDelete);
    }

    public void update(String tableName, List<NameValuePair> nameValuePairList, HashSet<Integer> resultSet) {
    }

    public String select(String tableName, List<String> wildAttributes) throws DBException{
        return activeDB.selectQuery(tableName, wildAttributes);
    }

    public void select(String tableName, List<String> wildAttributes, HashSet<Integer> filteredValues) {
    }
}
