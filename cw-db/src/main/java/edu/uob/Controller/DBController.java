package edu.uob.Controller;

import edu.uob.AllEnums.AlterationType;
import edu.uob.AllEnums.BoolOperator;
import edu.uob.AllEnums.SQLComparator;
import edu.uob.Model.Database;
import edu.uob.Model.NameValuePair;
import edu.uob.Model.Value;

import java.util.HashSet;
import java.util.List;

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

    public void setActiveDB(String activeDBName){
        // TODO some file IO shit to change activeDB
        try {
            activeDB = ioController.loadDatabase(activeDBName);
        }
        catch(Exception e){
            System.out.println("inside setActiveDB methods in DBCOntroller class");
            System.out.println(e.getMessage());
        }
    }

    public void deleteDB(String dbName) {
    }

    public void deleteTable(String tableName) {
    }

    public void createDB(String dbName) {
    }

    public void createTable() {
    }

    public void createTable(List<String> attbrList) {
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
