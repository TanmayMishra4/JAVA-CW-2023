package edu.uob.Controller;

import edu.uob.AllExceptions.DBExceptions.CannotCreateTableException;
import edu.uob.AllExceptions.DBExceptions.CannotDropTableException;
import edu.uob.AllExceptions.DBExceptions.DBDoesNotExistException;
import edu.uob.AllExceptions.DBExceptions.DBException;
import edu.uob.Model.Database;
import edu.uob.Utils.Utils;

import java.io.*;
import java.nio.Buffer;
import java.util.List;

public class IOController {
    private final static String extension = ".tab";
    private static final String tabString = "\t";

    public Database loadDatabase(String dbName) throws Exception {
        try {
            String fileName = Utils.getDBFilePathName(dbName);
            File dbFile = new File(fileName);
            Database database = new Database(dbName);
            File[] tabFiles = dbFile.listFiles();
            if(tabFiles == null) throw new DBDoesNotExistException();
            for(File tabFile : tabFiles){
                String tableName = getTableName(tabFile.getName());
                database.addTable(tableName);
                loadTable(database, tabFile);
            }
            return database;
        }
        catch (FileNotFoundException e) {
            throw new DBException(e.getMessage());
        }
    }

    public void loadTable(Database db, File tabFile) throws Exception{
        BufferedReader br = new BufferedReader(new FileReader(tabFile));
        String line = br.readLine();
        String tableName = getTableName(tabFile.getName());
        String[] columnNames = line.split("\t");
        for(String columnName : columnNames) db.addColumnToTable(tableName, columnName);

        line = br.readLine();
        while(!line.isEmpty()){
            String[] columnValues = line.split(tabString);
            db.addDataToTable(tableName, columnValues);
            line = br.readLine();
        }
        br.close();
    }

    public void storeDB(Database db){

    }

    private String getTableName(String tabFileName){
        return tabFileName.split(extension)[0];
    }

    public void dropDB(String dbName) throws DBException {
        String dbFilePathName = Utils.getDBFilePathName(dbName);
        try {
            File dbFile = new File(dbFilePathName);
            File[] tabFiles = dbFile.listFiles();
            if(!dbFile.exists() || !dbFile.isDirectory() || tabFiles == null) throw new FileNotFoundException();

            for(File tabFile : tabFiles){
                boolean isDeleted = tabFile.delete();
                if(!isDeleted) throw new DBException("Cannot delete file " + tabFile.getName());
            }
            boolean isDBDeleted = dbFile.delete();
            if(!isDBDeleted) throw new DBException("Cannot delete Database");
        }
        catch(FileNotFoundException e){
            throw new DBDoesNotExistException();
        }
    }

    public void createDB(String dbName) throws DBException{
        String dbFilePathName = Utils.getDBFilePathName(dbName);
        dbFilePathName = dbFilePathName.substring(0, dbFilePathName.length());
        try{
            File dbFile = new File(dbFilePathName);
            boolean result = dbFile.mkdir();
            result = result && dbFile.setReadable(true);
            result = result && dbFile.setWritable(true);
            if(!result) throw new DBException("Cannot Create Database or Database already exists !!");
        }
        catch(Exception e){
            throw new DBException(e.getMessage());
        }
    }

    public void dropTable(String dbName, String tableName) throws DBException{
        String tableFileName = Utils.getDBFilePathTable(dbName, tableName);
        File file = new File(tableFileName);
        try{
            if(!file.delete()){
                throw new CannotDropTableException();
            }
        }
        catch(Exception ignored){
            throw new CannotDropTableException();
        }
    }

    public void createTable(String dbName, String tableName) throws DBException{
        String tableFileName = Utils.getDBFilePathTable(dbName, tableName);
        File file = new File(tableFileName);
        try{
            if(!file.createNewFile()){
                throw new CannotCreateTableException();
            }
        }
        catch(Exception e){
            throw new CannotCreateTableException();
        }
    }

    public void addColumnNames(String dbName, String tableName, List<String> attbrList) throws DBException{
        String tableFileName = Utils.getDBFilePathTable(dbName, tableName);
        File file = new File(tableFileName);
        createTable(dbName, tableName);
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(file));
            bw.write("id");
            for(String columnName : attbrList) bw.write("\t"+columnName);
            bw.flush();
            bw.close();
        }
        catch(Exception e){
            throw new CannotCreateTableException();
        }
    }

}
