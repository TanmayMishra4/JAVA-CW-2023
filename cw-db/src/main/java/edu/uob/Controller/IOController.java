package edu.uob.Controller;

import edu.uob.DBExceptions.DBException;
import edu.uob.Model.Database;
import java.io.*;
import java.nio.file.Paths;
public class IOController {
    private final static String dbPath = Paths.get("databases").toAbsolutePath().toString();
    private final static String extension = ".tab";
    private static final String tabString = "\t";

    public Database loadDatabase(String dbName) throws Exception {
        try {
            String fileName = dbPath + File.separator + dbName + File.separator;
            File dbFile = new File(fileName);
            Database database = new Database(dbName);
            File[] tabFiles = dbFile.listFiles();
            if(tabFiles == null) throw new IOException("Error opening Database, Directory does not exist");
            for(File tabFile : dbFile.listFiles()){
                String tableName = getTableName(tabFile.getName());
                database.addTable(tableName);
                loadTable(database, tabFile);
            }
            return database;
        }
        catch (FileNotFoundException e) {
            throw new DBException("Cannot Open File");
        }
    }

    public void loadTable(Database db, File tabFile) throws Exception{
        BufferedReader br = new BufferedReader(new FileReader(tabFile));
        String line = br.readLine();
        String tableName = getTableName(tabFile.getName());
        String[] columnNames = line.split("\t");
        for(String columnName : columnNames) db.addColumnToTable(tableName, columnName);

        while(line != null){
            line = br.readLine();
            String[] columnValues = line.split(tabString);
            db.addDataToTable(tableName, columnValues);
        }
        br.close();
    }

    public void storeDB(Database db){

    }

    private String getTableName(String tabFileName){
        return tabFileName.split(extension)[0];
    }

}
