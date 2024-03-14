package edu.uob.Controller;

import edu.uob.AllEnums.AlterationType;
import edu.uob.AllEnums.BoolOperator;
import edu.uob.AllEnums.SQLComparator;
import edu.uob.AllExceptions.DBExceptions.DBException;
import edu.uob.AllExceptions.DBExceptions.DuplicateColumnNameException;
import edu.uob.AllExceptions.DBExceptions.TableDoesNotExistException;
import edu.uob.Model.Database;
import edu.uob.Model.NameValuePair;
import edu.uob.Model.Table;
import edu.uob.Model.Value;
import edu.uob.Utils.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static edu.uob.AllEnums.AlterationType.ADD;
import static edu.uob.AllEnums.BoolOperator.AND;

public class DBController {
    private static Database activeDB;
    private IOController ioController;
    public DBController(){
        ioController = new IOController();
        activeDB = null;
    }

    public void setActiveDB(String activeDBName) throws DBException {
        activeDBName = activeDBName.toLowerCase();
        try {
            activeDB = ioController.loadDatabase(activeDBName);
        }
        catch(Exception e){
            throw new DBException(e.getMessage());
        }
    }

    public void dropDB(String dbName) throws DBException{
        dbName = dbName.toLowerCase();
        ioController.dropDB(dbName);
        if(activeDB != null && dbName.equals(activeDB.getName())) activeDB = null;
    }

    public void dropTable(String tableName) throws DBException {
        tableName = tableName.toLowerCase();
        activeDB.dropTable(tableName);
        ioController.saveDB(activeDB);
    }

    public Database createDB(String dbName) throws DBException{
        dbName = dbName.toLowerCase();
        String dbPathName = Utils.getDBFilePathName(dbName);
        File file = new File(dbPathName);
        if(file.isDirectory()) throw new DBException("Database Already exists");
        Database db = new  Database(dbName);
        ioController.saveDB(db);
        return db;
    }

    public void createTable(String tableName) throws DBException {
        tableName = tableName.toLowerCase();
        activeDB.addTable(tableName);
        ioController.saveDB(activeDB);
    }

    public void createTable(String tableName, List<String> attbrList) throws DBException {
        tableName = tableName.toLowerCase();
        Set<String> columnVals = new HashSet<>();
        for (String columnVal : attbrList) {
            if (!columnVals.add(columnVal.toLowerCase()) || columnVal.equalsIgnoreCase("id"))
                throw new DuplicateColumnNameException();
        }
        createTable(tableName);
        for (String data : attbrList) {
            activeDB.addColumnToTable(tableName, data);
        }
        ioController.saveDB(activeDB);
    }
    public void alterTable(String tableName, AlterationType alterationType, String attributeName) throws DBException {
        tableName = tableName.toLowerCase();
        if(alterationType == ADD){
            activeDB.addColumnToTable(tableName, attributeName);
        }
        else{
            activeDB.removeColumnFromTable(tableName, attributeName);
        }
        ioController.saveDB(activeDB);
    }

    public String joinTables(String tableName1, String tableName2, String attributeName1, String attributeName2) throws DBException{
        tableName2 = tableName2.toLowerCase();
        tableName1 = tableName1.toLowerCase();
        return activeDB.join(tableName1, tableName2, attributeName1, attributeName2);
    }

    public void insertValues(String tableName, List<Value> valueList) throws DBException {
        tableName = tableName.toLowerCase();
        String[] values = new String[valueList.size()];
        for(int index=0;index<valueList.size();index++){
            values[index] = valueList.get(index).toString();
        }
        activeDB.addDataToTable(tableName, values);
        ioController.saveDB(activeDB);
    }

    public List<Integer> filter(String tableName, String attributeName, SQLComparator sqlComparator, Value value) throws DBException{
        tableName = tableName.toLowerCase();
        if(!activeDB.hasTable(tableName)) throw new TableDoesNotExistException();
        Table table = activeDB.getTables().get(tableName);
        return table.filter(attributeName, sqlComparator, value);
    }

    public List<Integer> filter(String tableName, List<Integer> condition1Values, BoolOperator operator, List<Integer> condition2Values) throws DBException{
        tableName = tableName.toLowerCase();
        if(!activeDB.hasTable(tableName)) throw new TableDoesNotExistException();
        HashSet<Integer> firstSet =  new HashSet<>(condition1Values);
        HashSet<Integer> secondSet =  new HashSet<>(condition1Values);
        HashSet<Integer> resultSet;
        if(operator == AND){
            resultSet = new HashSet<>();
            for(Integer val :  firstSet){
                if(secondSet.contains(val)) resultSet.add(val);
            }
        }
        else{
            resultSet = new HashSet<>(secondSet);
            resultSet.addAll(firstSet);
        }
        return new ArrayList<>(resultSet);
    }

    public void deleteValuesFromTable(String tableName, List<Integer> indexesToDelete) throws DBException {
        tableName = tableName.toLowerCase();
        activeDB.deleteFromTable(tableName, indexesToDelete);
        ioController.saveDB(activeDB);
    }

    public void update(String tableName, List<NameValuePair> nameValuePairList, List<Integer> resultSet) throws DBException{
        tableName = tableName.toLowerCase();
        activeDB.update(tableName, nameValuePairList, resultSet);
        ioController.saveDB(activeDB);
    }

    public String select(String tableName, List<String> wildAttributes) throws DBException{
        tableName = tableName.toLowerCase();
        return activeDB.selectQuery(tableName, wildAttributes);
    }

    public String select(String tableName, List<String> wildAttributes, List<Integer> filteredValues) throws DBException {
        tableName = tableName.toLowerCase();
        return activeDB.selectQuery(tableName, wildAttributes, filteredValues);
    }

    public Table getTable(String tableName) throws DBException{
        if(!activeDB.hasTable(tableName)) throw new TableDoesNotExistException();
        return activeDB.getTables().get(tableName);
    }
    // TODO check if column name is same as table name
}
