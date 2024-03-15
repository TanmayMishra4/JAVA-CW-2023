package edu.uob.Controller;

import edu.uob.AllExceptions.DBExceptions.*;
import edu.uob.Model.Column;
import edu.uob.Model.Database;
import edu.uob.Model.Table;
import edu.uob.Model.Value;
import edu.uob.Utils.Utils;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class IOController {
    // TODO check if identifier names are case insensitive in query
    private final static String extension = ".tab";
    private static final String tabString = "\t";

    public Database loadDatabase(String dbName) throws DBException {
        try {
            String fileName = Utils.getDBFilePathName(dbName);
            File dbFile = new File(fileName);
            if(!dbFile.exists() || !dbFile.isDirectory()) throw new DBException("Cannot Access Database");
            Database database = new Database(dbName);
            File[] tabFiles = dbFile.listFiles();
            if (tabFiles == null) return database;
            for (File tabFile : tabFiles) {
                if(!tabFile.getName().endsWith(extension)) continue;
                String tableName = getTableName(tabFile.getName());
                database.addTable(tableName);
                loadTable(database, tabFile);
            }
            return database;
        } catch (Exception e) {
            throw new DBException(e.getMessage());
        }
    }

    public void loadTable(Database db, File tabFile) throws Exception {
        BufferedReader br = new BufferedReader(new FileReader(tabFile));
        String line = br.readLine();
        String tableName = getTableName(tabFile.getName());
        String[] columnNames = line.split("\t");
        for (String columnName : columnNames) db.addColumnToTable(tableName, columnName);

        line = br.readLine();
        while (line != null && !line.isEmpty()) {
            String[] columnValues = line.split(tabString);
            db.loadDataToTable(tableName, columnValues);
            line = br.readLine();
        }
        br.close();
    }

    private String getTableName(String tabFileName) {
        return tabFileName.split(extension)[0];
    }

    public void dropDB(String dbName) throws DBException {
        String dbFilePathName = Utils.getDBFilePathName(dbName);
        try {
            File dbFile = new File(dbFilePathName);
            File[] tabFiles = dbFile.listFiles();
            if (!dbFile.exists() || !dbFile.isDirectory() || tabFiles == null) throw new FileNotFoundException();

            for (File tabFile : tabFiles) {
                boolean isDeleted = tabFile.delete();
                if (!isDeleted) throw new DBException("Cannot delete file " + tabFile.getName());
            }
            boolean isDBDeleted = dbFile.delete();
            if (!isDBDeleted) throw new DBException("Cannot delete Database");
        } catch (FileNotFoundException e) {
            throw new DBDoesNotExistException();
        }
    }

    public void saveDB(Database db) throws DBException{
        String dbPathName = Utils.getDBFilePathName(db.getName());
        File dbFile = new File(dbPathName);
        dbFile.mkdir();
        HashMap<String, Table> tables = db.getTables();
        for (Table table : tables.values()) {
            saveTable(db, table);
        }
    }

    private void saveTable(Database db, Table table) throws DBException {
        String tableFilePath = Utils.getDBFilePathTable(db.getName(), table.getName());
        File file = new File(tableFilePath);
        try {
            file.delete();
            file.createNewFile();
            file.setReadable(true);file.setWritable(true);
            BufferedWriter bw = new BufferedWriter(new FileWriter(file));
            List<String> columnNames = table.getColumnNames();
            List<Integer> primaryKeys = new ArrayList<>(table.getPrimaryKeys());
            primaryKeys.sort(Integer::compareTo);
            for (int index = 0; index < columnNames.size(); index++) {
                String columnName = columnNames.get(index);
                bw.write(columnName);
                if (index != columnNames.size() - 1) bw.write("\t");
            }
            HashMap<String, Column> columnsMap = table.getColumnsMap();

            for (int index : primaryKeys) {
                bw.write("\n");
                for (int indexCol = 0; indexCol < columnNames.size(); indexCol++) {
                    String columnName = columnNames.get(indexCol);
                    Column column = columnsMap.get(columnName);
                    Value val = column.getValue(index);
                    bw.write(val.getStringVal());
                    if (indexCol != columnNames.size() - 1) bw.write("\t");
                }
            }
            bw.flush();
            bw.close();
        } catch (Exception e) {
            throw new DBException("Exception during storing table");
        }
    }
}
