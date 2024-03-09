package edu.uob.Controller;

import edu.uob.AlterationType;
import edu.uob.Database;

import java.util.List;

public class DBController {
    private static Database activeDBName;
    public DBController(){
        activeDBName = null;
    }

    public Database getActiveDBName() {
        return activeDBName;
    }

    public void setActiveDBName(String activeDB) {
        activeDB = activeDB;
    }

    public void setActiveDB(String activeDBName){
        setActiveDBName(activeDBName);
        // TODO do some file IO shit to change activeDB
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
}
