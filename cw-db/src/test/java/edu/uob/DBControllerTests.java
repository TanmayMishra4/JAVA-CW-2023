package edu.uob;

import edu.uob.AllExceptions.DBExceptions.DBException;
import edu.uob.Controller.DBController;
import edu.uob.Controller.IOController;
import edu.uob.Model.Database;
import edu.uob.Model.Value;
import edu.uob.Utils.Utils;
import org.junit.jupiter.api.*;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class DBControllerTests {
    // TODO populate dummy databases for testing
    private DBController dbController;
    final String dbName = "randomNonExistentDB";

    @BeforeAll
    public static void makeFolder(){
        File file = new File(Paths.get("databases").toUri());
        if(!file.exists()) file.mkdir();
    }
    @BeforeEach
    public void setup(){
        dbController = new DBController();
    }
    @AfterEach
    public void afterTest(){
        deleteFolder(dbName);
        deleteFolder("markbook");
    }

    @AfterAll
    public static void cleanFolder(){
        File file = new File(Paths.get("databases").toAbsolutePath().toString());
        File[] fileList = file.listFiles();
        if(fileList == null) return;
        for(File internalDirectory : file.listFiles()){
            if(internalDirectory.getName().equalsIgnoreCase("testDB")) continue;
            File[] internalFileList = internalDirectory.listFiles();
            if(internalFileList == null) continue;
            for(File f : internalDirectory.listFiles()) f.delete();
            internalDirectory.delete();
        }
    }

    @Test
    public void testSetActiveDB(){
        createFolder();
        assertDoesNotThrow(()->dbController.setActiveDB(dbName));
    }
    @Test
    public void testSetActiveDBFail(){
        assertThrows(DBException.class, ()->dbController.setActiveDB(dbName));
    }
    @Test
    public void testDeleteDB(){
        createFolder();
        assertDoesNotThrow(()->dbController.dropDB(dbName));
    }
    @Test
    public void testDeleteDBFail(){
        assertThrows(DBException.class, ()->dbController.dropDB(dbName));
    }

    @Test
    public void testCreateDB(){
        assertDoesNotThrow(()->dbController.createDB(dbName));
    }

    // TODO test case for duplicate column names

    @Test
    public void testCreateDBFail(){
        createFolder();
        assertThrows(DBException.class, ()->dbController.createDB(dbName));
    }

    @Test
    public void testDropTable() throws DBException {
        createFolder();
        assertDoesNotThrow(()->dbController.setActiveDB(dbName));
        dbController.createTable("people");
        assertDoesNotThrow(()->dbController.dropTable("people"));
    }

    @Test
    public void testDropTableFail(){
        createFolder();
        assertDoesNotThrow(()->dbController.setActiveDB(dbName));
        assertThrows(DBException.class, ()->dbController.dropTable("kansd"));
    }

    @Test
    public void testCreateTable(){
        createFolder();
        assertDoesNotThrow(()->dbController.setActiveDB(dbName));
        assertDoesNotThrow(()->dbController.createTable("testTable"));
    }

    @Test
    public void testCreateTableWithAttrb(){
        createFolder();
        assertDoesNotThrow(()->dbController.setActiveDB(dbName));
        assertDoesNotThrow(()->dbController.createTable("testTable", Arrays.asList("name", "age", "Uni")));
    }

    void deleteFolder(String dbName){
        String dbPathName = Utils.getDBFilePathName(dbName);
        File file = new File(dbPathName.substring(0, dbPathName.length()));
        try{
            for(File f : file.listFiles()){
                f.delete();
            }
            file.delete();
        }catch(Exception ignored){}
    }

    void createFolder(){
        String dbPathName = Utils.getDBFilePathName(dbName);
        File file = new File(dbPathName.substring(0, dbPathName.length()));
        try{
            boolean val = file.mkdir();
        } catch (Exception ignored){}
    }

    @Test
    public void testSaveDB() throws DBException{
        Database d = new Database(dbName);
        d.addTable("people");
        d.addColumnToTable("people", "name");
        d.addColumnToTable("people", "score");
        d.addColumnToTable("people", "email");
        d.addColumnToTable("people", "phone");
        d.addDataToTable("people", new String[]{"hbjad", "5125.89", "134", "FALSE"});
        d.addDataToTable("people", new String[]{"asdfkjaf", "324.812", "989", "TRUE"});
        d.addDataToTable("people", new String[]{"askdjfb", "989.1823", "7856", "FALSE"});
        d.addDataToTable("people", new String[]{"ASBDUA", "812.51273", "2345", "TRUE"});
        IOController ioController = new IOController();
        ioController.saveDB(d);
    }

    @Test
    public void testloadDatabase() throws DBException {
        createFolder();
        dbController.setActiveDB(dbName);
        dbController.createTable("people", Arrays.asList("andk", "dnjna", "asjdfn"));
        IOController ioController = new IOController();
        Database d = ioController.loadDatabase(dbName);
        assert(d.getName().equals(dbName));
        assert(d.getTables().size() == 1);
    }
//    @Test
//    public void testDropDatabase() throws DBException {
//        createFolder();
//        dbController.setActiveDB(dbName);
//        dbController.createTable("people", Arrays.asList("andk", "dnjna", "asjdfn"));
//        dbController.createTable("jabnds", Arrays.asList("adavn", "ajsd"," clmnoao"));
//        dbController.insertValues("people", List.of(new Value[]{new Value("sjdnf"), new Value("823489"), new Value("829")}));
//        IOController ioController = new IOController();
//        ioController.dropDB(dbName);
//    }
//
    @Test
    public void testSelect() throws DBException {
        dbController.setActiveDB("testDB");
        List<String> l = new ArrayList<>();
        l.add("Name");l.add("Age");
        String response = dbController.select("people", l);
        assert(response != null);
    }

    @Test
    public void testSelect2(){
        DBServer dbServer = new DBServer();
        dbServer.handleCommand("use testDB;");
        String response = "select * from people;";
        response = dbServer.handleCommand(response);
        assert(response.contains("[OK]"));
    }

    @Test
    public void testSelect3(){
        DBServer dbServer = new DBServer();
        dbServer.handleCommand("use testDB;");
        String response = "select id, Email from people;";
        response = dbServer.handleCommand(response);
        assert(response.contains("[OK]"));
    }

    @Test
    public void seriesOfCommands(){
        DBServer dbServer = new DBServer();
        String response = "CREATE DATABASE markbook;";
        response = dbServer.handleCommand(response);
        assert(response.contains("[OK]"));
//
        response = "USE markbook;";
        response = dbServer.handleCommand(response);
        assert(response.contains("[OK]"));
        response = "CREATE TABLE marks (name, mark, pass);";
        response = dbServer.handleCommand(response);
        assert(response.contains("[OK]"));
        response = "INSERT INTO marks VALUES ('Simon', 65, TRUE);";
        response = dbServer.handleCommand(response);
        assert(response.contains("[OK]"));
        response = "INSERT INTO marks VALUES ('Sion', 55, TRUE);";
        response = dbServer.handleCommand(response);
        assert(response.contains("[OK]"));
        response = "INSERT INTO marks VALUES ('Rob', 35, FALSE);";
        response = dbServer.handleCommand(response);
        assert(response.contains("[OK]"));
        response = "INSERT INTO marks VALUES ('Chris', 20, FALSE);";
        response = dbServer.handleCommand(response);
        assert(response.contains("[OK]"));
        response = "SELECT * FROM marks;";
        response = dbServer.handleCommand(response);
        assert(response.contains("[OK]"));
        response = "SELECT * FROM marks WHERE name != 'Sion';";
        response = dbServer.handleCommand(response);
        assert(response.contains("[OK]"));
        response = "SELECT * FROM marks WHERE pass == TRUE;";
        response = dbServer.handleCommand(response);
        assert(response.contains("[OK]"));
        response = "CREATE table coursework (task, submission);";
        response = dbServer.handleCommand(response);
        assert(response.contains("[OK]"));
        response = "INsert into coursework Values ('OXO', 3) ;";
        response = dbServer.handleCommand(response);
        assert(response.contains("[OK]"));
        response = "INsert into coursework Values ('DB', 1) ;";
        response = dbServer.handleCommand(response);
        assert(response.contains("[OK]"));
        response = "INsert into coursework Values ('OXO', 4) ;";
        response = dbServer.handleCommand(response);
        assert(response.contains("[OK]"));
        response = "INsert into coursework Values ('STAG', 2) ;";
        response = dbServer.handleCommand(response);
        assert(response.contains("[OK]"));
        response = "sELect * from coursework ;";
        response = dbServer.handleCommand(response);
        assert(response.contains("[OK]"));
        response = "JOIN coursework AND marks ON submission AND id;";
        response = dbServer.handleCommand(response);
        assert(response.contains("[OK]"));
        response = "UPDATE marks SET mark = 38 WHERE name == 'Chris';";
        response = dbServer.handleCommand(response);
        assert(response.contains("[OK]"));
        response = "SELECT * FROM marks WHERE name == 'Chris';";
        response = dbServer.handleCommand(response);
        assert(response.contains("[OK]"));
        response = "DELETE FROM marks WHERE name == 'Sion';";
        response = dbServer.handleCommand(response);
        assert(response.contains("[OK]"));
        response = "SELECT * FROM marks;";
        response = dbServer.handleCommand(response);
        assert(response.contains("[OK]"));
        response = "SELECT * FROM marks WHERE (pass == FALSE) AND (mark > 35);";
        response = dbServer.handleCommand(response);
        assert(response.contains("[OK]"));
        response = "SELECT * FROM marks WHERE name LIKE 'i';";
        response = dbServer.handleCommand(response);
        assert(response.contains("[OK]"));
        response = "SELECT id FROM marks WHERE pass == FALSE;";
        response = dbServer.handleCommand(response);
        assert(response.contains("[OK]"));
        response = "SELECT name FROM marks WHERE mark>60;";
        response = dbServer.handleCommand(response);
        assert(response.contains("[OK]"));
        response = "DELETE FROM marks WHERE mark<40;";
        response = dbServer.handleCommand(response);
        assert(response.contains("[OK]"));
        response = "SELECT * FROM marks;";
        response = dbServer.handleCommand(response);
        assert(response.contains("[OK]"));
        response = "ALTER TABLE marks ADD age;";
        response = dbServer.handleCommand(response);
        assert(response.contains("[OK]"));
        response = "SELECT * FROM marks;";
        response = dbServer.handleCommand(response);
        assert(response.contains("[OK]"));
        response = "UPDATE marks SET age = 35 WHERE name == 'Simon';";
        response = dbServer.handleCommand(response);
        assert(response.contains("[OK]"));
        response = "SELECT * FROM marks;";
        response = dbServer.handleCommand(response);
        assert(response.contains("[OK]"));
        response = "ALTER TABLE marks DROP pass;";
        response = dbServer.handleCommand(response);
        assert(response.contains("[OK]"));
        response = "SELECT * FROM marks;";
        response = dbServer.handleCommand(response);
        assert(response.contains("[OK]"));
        response = "SELECT * FROM crew;";
        response = dbServer.handleCommand(response);
        assert(response.contains("[ERROR]"));
        response = "SELECT height FROM marks WHERE name == 'Chris';";
        response = dbServer.handleCommand(response);
        assert(response.contains("[ERROR]"));
        response = "DROP TABLE marks;";
        response = dbServer.handleCommand(response);
        assert(response.contains("[OK]"));
        response = "DROP DATABASE markbook;";
        response = dbServer.handleCommand(response);
        assert(response.contains("[OK]"));
    }

//    @Test
//    public void testNestedChecks(){
//        String response = "select * from marks where ((mark == 55.0) OR (pass == FALSE));";
//        DBServer dbServer = new DBServer();
//        dbServer.handleCommand("use markbook;");
//        response = dbServer.handleCommand(response);
//        assert(response.contains("[OK]"));
//    }
}
