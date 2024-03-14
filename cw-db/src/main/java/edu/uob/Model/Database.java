package edu.uob.Model;

import edu.uob.AllExceptions.DBExceptions.ColumnNotFoundException;
import edu.uob.AllExceptions.DBExceptions.DBException;
import edu.uob.AllExceptions.DBExceptions.DuplicateTablesException;
import edu.uob.AllExceptions.DBExceptions.TableDoesNotExistException;
import edu.uob.Utils.PrimaryKeyGenerator;

import java.util.*;
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
        if(hasTable(tableName)) throw new DuplicateTablesException();
        Table tableEntry = new Table(tableName);
        this.tables.put(tableName, tableEntry);
    }

    public void addColumnToTable(String tableName, String columnName) throws DBException {
        if(!hasTable(tableName)) throw new TableDoesNotExistException();
        Table table = tables.get(tableName);
        table.addColumn(columnName);
    }

    public void addDataToTable(String tableName, String[] rowOfValues) throws DBException {
        if(!hasTable(tableName)) throw new TableDoesNotExistException();
        Table table = tables.get(tableName);
        table.addData(rowOfValues);
    }

    public void loadDataToTable(String tableName, String[] rowOfValues) throws DBException {
        if(!tables.containsKey(tableName)) throw new TableDoesNotExistException();
        Table table = tables.get(tableName);
        table.loadDataRows(rowOfValues);
        Optional<Integer> largestPk = table.getPrimaryKeys().stream().max(Integer::compareTo);
        largestPk.ifPresent(integer -> table.setPkGenerator(integer + 1));
    }

    public boolean hasTable(String tableName){
        for(String name : tables.keySet()){
            if(tableName.equalsIgnoreCase(name)) return true;
        }
        return false;
    }

    public void dropTable(String tableName) throws DBException{
        if(hasTable(tableName)){
           tables.remove(tableName);
        }
        else throw new TableDoesNotExistException();
    }

    public void removeColumnFromTable(String tableName, String columnName) throws DBException{
        if(!hasTable(tableName)) throw new TableDoesNotExistException();
        tables.get(tableName).removeColumn(columnName);
    }

    public void deleteFromTable(String tableName, List<Integer> indexesToDelete) throws DBException{
        if(!hasTable(tableName)) throw new TableDoesNotExistException();
        tables.get(tableName).removeRowsWithIndex(indexesToDelete);
    }

    public String selectQuery(String tableName, List<String> wildAttributes) throws DBException{
        if(!hasTable(tableName)) throw new TableDoesNotExistException();
        try {
            return tables.get(tableName).selectQuery(wildAttributes);
        }
        catch(Exception e){
            throw new DBException("SELECT Query Cannot be executed");
        }
    }

    public String selectQuery(String tableName, List<String> wildAttributes, List<Integer> filteredValues) throws DBException{
        if(!hasTable(tableName)) throw new TableDoesNotExistException();
        try {
            return tables.get(tableName).selectQuery(wildAttributes, new HashSet<>(filteredValues));
        }
        catch(Exception e){
            throw new DBException("SELECT Query Cannot be executed");
        }
    }

    public void update(String tableName, List<NameValuePair> nameValuePairList, List<Integer> resultSet) throws DBException{
        if(!hasTable(tableName)) throw new TableDoesNotExistException();
        try {
            tables.get(tableName).update(nameValuePairList, resultSet);
        }
        catch(Exception e){
            throw new DBException("UPDATE Query Cannot be executed");
        }
    }

    public String join(String tableName1, String tableName2, String columnName1, String columnName2) throws DBException{
        if(!hasTable(tableName1) || !hasTable(tableName2)) throw new TableDoesNotExistException();
        Table table1 =  tables.get(tableName1);
        Table table2 = tables.get(tableName2);
        if(!table1.hasColumn(columnName1) || !table2.hasColumn(columnName2)) throw new ColumnNotFoundException();
        List<Column> columnList1 = table1.getColumnList();
        List<Column> columnList2 = table2.getColumnList();
        List<Integer> primaryKeysMatch = new ArrayList<>();
        try {
            Column column1 = columnList1.stream().filter((a) -> a.name.equalsIgnoreCase(columnName1)).toList().get(0);
            Column column2 = columnList2.stream().filter((a) -> a.name.equalsIgnoreCase(columnName2)).toList().get(0);
            Set<Integer> column2Set = column2.getValues().keySet();
            for(var val1 : column1.getValues().entrySet()){
                int primaryKey1 = val1.getKey();
                if(column2Set.contains(primaryKey1)){
                    primaryKeysMatch.add(primaryKey1);
                }
            }
            primaryKeysMatch.sort(Integer::compareTo);
        }
        catch(Exception e) {throw new DBException("Cannot join tables, error occurred !!");}
        return joinOnPrimaryKeys(primaryKeysMatch, table1, table2, columnName1, columnName2);
    }

    private String joinOnPrimaryKeys(List<Integer> primaryKeysMatch, Table table1, Table table2, String attributeName1, String attributeName2) throws DBException{
        StringBuilder sb = new StringBuilder();
        PrimaryKeyGenerator pkGen = new PrimaryKeyGenerator();

        try{writeColumnNamesJoin(table1, table2, attributeName1, attributeName2, sb);}
        catch(Exception ignored){throw new DBException("Error while writing columnNames");}

        for(int primaryKeyToMatch : primaryKeysMatch){
            Integer primaryKeyToWrite = pkGen.getPrimaryKey().getIntVal();
            writeColumnValuesJoin(primaryKeyToMatch, table1, attributeName1, sb, primaryKeyToWrite);
            writeColumnValuesJoin(primaryKeyToMatch, table2, attributeName2, sb, null);
            int lastIndex = sb.length()-1;
            if(sb.charAt(lastIndex) == '\t'){
                sb.deleteCharAt(lastIndex);
            }
            sb.append("\n");
        }
//        int lastIndex = sb.length()-1;
//        if(sb.charAt(lastIndex))
        return sb.toString();
    }

    private void writeColumnValuesJoin(int primaryKeyToMatch, Table table, String ignoreColumn, StringBuilder sb, Integer joinPK) throws DBException {
        if(joinPK != null) sb.append(joinPK).append("\t");
        for(String columnName : table.getColumnNames()){
            if(columnName.equalsIgnoreCase(ignoreColumn) || columnName.equals("id")) continue;
            Column column = table.getColumn(columnName);
            sb.append(column.getValue(primaryKeyToMatch));
            sb.append("\t");
        }
    }

    private void writeColumnNamesJoin(Table table1, Table table2, String attributeName1, String attributeName2, StringBuilder sb) throws Exception{
        sb.append("id");

        for(String columnName : table1.getColumnNames()){
            if(columnName.equalsIgnoreCase(attributeName1) || columnName.equalsIgnoreCase("id")) continue;
            sb.append("\t");
            sb.append(table1.getName()).append(".").append(columnName);
        }
        for(String columnName : table2.getColumnNames()){
            if(columnName.equalsIgnoreCase(attributeName2) || columnName.equalsIgnoreCase("id")) continue;
            sb.append("\t");
            sb.append(table2.getName()).append(".").append(columnName);
        }
        sb.append("\n");
    }
}
