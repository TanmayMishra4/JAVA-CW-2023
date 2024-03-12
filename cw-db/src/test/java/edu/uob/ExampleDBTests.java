package edu.uob;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;

import edu.uob.Controller.IOController;
import edu.uob.Model.Database;
import edu.uob.Utils.Utils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.time.Duration;


public class ExampleDBTests {

    private DBServer server;
    final String dbName = "randomNonExistentDB";

    // Create a new server _before_ every @Test
    @BeforeEach
    public void setup() {
        server = new DBServer();
    }

    // Random name generator - useful for testing "bare earth" queries (i.e. where tables don't previously exist)
    private String generateRandomName() {
        String randomName = "";
        for (int i = 0; i < 10; i++) randomName += (char) (97 + (Math.random() * 25.0));
        return randomName;
    }

    private String sendCommandToServer(String command) {
        // Try to send a command to the server - this call will timeout if it takes too long (in case the server enters an infinite loop)
        return assertTimeoutPreemptively(Duration.ofMillis(1000), () -> {
                    return server.handleCommand(command);
                },
                "Server took too long to respond (probably stuck in an infinite loop)");
    }

    void deleteFolder(){
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

    // A basic test that creates a database, creates a table, inserts some test data, then queries it.
    // It then checks the response to see that a couple of the entries in the table are returned as expected
//    @Test
//    public void testBasicCreateAndQuery() {
//        String randomName = generateRandomName();
//        sendCommandToServer("CREATE DATABASE " + randomName + ";");
//        sendCommandToServer("USE " + randomName + ";");
//        sendCommandToServer("CREATE TABLE marks (name, mark, pass);");
//        sendCommandToServer("INSERT INTO marks VALUES ('Simon', 65, TRUE);");
//        sendCommandToServer("INSERT INTO marks VALUES ('Sion', 55, TRUE);");
//        sendCommandToServer("INSERT INTO marks VALUES ('Rob', 35, FALSE);");
//        sendCommandToServer("INSERT INTO marks VALUES ('Chris', 20, FALSE);");
//        String response = sendCommandToServer("SELECT * FROM marks;");
//        assertTrue(response.contains("[OK]"), "A valid query was made, however an [OK] tag was not returned");
//        assertFalse(response.contains("[ERROR]"), "A valid query was made, however an [ERROR] tag was returned");
//        assertTrue(response.contains("Simon"), "An attempt was made to add Simon to the table, but they were not returned by SELECT *");
//        assertTrue(response.contains("Chris"), "An attempt was made to add Chris to the table, but they were not returned by SELECT *");
//    }

    // A test to make sure that querying returns a valid ID (this test also implicitly checks the "==" condition)
    // (these IDs are used to create relations between tables, so it is essential that suitable IDs are being generated and returned !)
//    @Test
//    public void testQueryID() {
//        String randomName = generateRandomName();
//        sendCommandToServer("CREATE DATABASE " + randomName + ";");
//        sendCommandToServer("USE " + randomName + ";");
//        sendCommandToServer("CREATE TABLE marks (name, mark, pass);");
//        sendCommandToServer("INSERT INTO marks VALUES ('Simon', 65, TRUE);");
//        String response = sendCommandToServer("SELECT id FROM marks WHERE name == 'Simon';");
//        // Convert multi-lined responses into just a single line
//        String singleLine = response.replace("\n", " ").trim();
//        // Split the line on the space character
//        String[] tokens = singleLine.split(" ");
//        // Check that the very last token is a number (which should be the ID of the entry)
//        String lastToken = tokens[tokens.length - 1];
//        try {
//            Integer.parseInt(lastToken);
//        } catch (NumberFormatException nfe) {
//            fail("The last token returned by `SELECT id FROM marks WHERE name == 'Simon';` should have been an integer ID, but was " + lastToken);
//        }
//    }

    // A test to make sure that databases can be reopened after server restart
//    @Test
//    public void testTablePersistsAfterRestart() {
//        String randomName = generateRandomName();
//        sendCommandToServer("CREATE DATABASE " + randomName + ";");
//        sendCommandToServer("USE " + randomName + ";");
//        sendCommandToServer("CREATE TABLE marks (name, mark, pass);");
//        sendCommandToServer("INSERT INTO marks VALUES ('Simon', 65, TRUE);");
//        // Create a new server object
//        server = new DBServer();
//        sendCommandToServer("USE " + randomName + ";");
//        String response = sendCommandToServer("SELECT * FROM marks;");
//        assertTrue(response.contains("Simon"), "Simon was added to a table and the server restarted - but Simon was not returned by SELECT *");
//    }

    // Test to make sure that the [ERROR] tag is returned in the case of an error (and NOT the [OK] tag)
//    @Test
//    public void testForErrorTag() {
//        String randomName = generateRandomName();
//        sendCommandToServer("CREATE DATABASE " + randomName + ";");
//        sendCommandToServer("USE " + randomName + ";");
//        sendCommandToServer("CREATE TABLE marks (name, mark, pass);");
//        sendCommandToServer("INSERT INTO marks VALUES ('Simon', 65, TRUE);");
//        String response = sendCommandToServer("SELECT * FROM libraryfines;");
//        assertTrue(response.contains("[ERROR]"), "An attempt was made to access a non-existent table, however an [ERROR] tag was not returned");
//        assertFalse(response.contains("[OK]"), "An attempt was made to access a non-existent table, however an [OK] tag was returned");
//    }

    @Test
    public void testForUSE() {
        String randomName = generateRandomName();
        String response = sendCommandToServer("UsE               " + randomName + "  ; ");
        assertTrue(response.contains("[OK]"), "An attempt was made to access a non-existent table, however an [ERROR] tag was not returned");
        assertFalse(response.contains("[ERROR]"), "An attempt was made to access a non-existent table, however an [OK] tag was returned");
        response = sendCommandToServer("USE " + randomName + ";");
        assertTrue(response.contains("[OK]"), "An attempt was made to access a non-existent table, however an [ERROR] tag was not returned");
        assertFalse(response.contains("[ERROR]"), "An attempt was made to access a non-existent table, however an [OK] tag was returned");
    }

    @Test
    public void testForUSEFail() {
        String randomName = generateRandomName();
        String response = sendCommandToServer("UsEr               " + randomName + "  ; ");
        assertFalse(response.contains("[OK]"), "Checking for USE Fail case wrong spelling, expected -> [ERROR] returned -> [OK]");
        assertTrue(response.contains("[ERROR]"), "Checking for USE Fail case wrong spelling, expected -> [ERROR] returned -> [OK]");
        response = sendCommandToServer("USE TRUE " + ";");
        assertFalse(response.contains("[OK]"), "Checking for USE Keyword Fail case, expected -> [ERROR] returned -> [OK]");
        assertTrue(response.contains("[ERROR]"), "Checking for USE Keyword Fail case, expected -> [ERROR] returned -> [OK]");
    }

    @Test
    public void testForDrop() {
        String randomName = generateRandomName();
        String response = sendCommandToServer("DroP   DataBASe " + randomName + "  ; ");
        assertTrue(response.contains("[OK]"), "DROP DATABASE case not working for case insensitive");
        assertFalse(response.contains("[ERROR]"), "DROP DATABASE case not working for case insensitive, should not contain [ERROR] tag");
        randomName = generateRandomName();
        response = sendCommandToServer("DroP   TaBle " + randomName + "  ; ");
        assertTrue(response.contains("[OK]"), "DROP TABLE case not working for case insensitive");
        assertFalse(response.contains("[ERROR]"), "DROP TABLE case not working for case insensitive, should not contain [ERROR] tag");
    }

    @Test
    public void testForDropFail() {
        String randomName = generateRandomName();
        String response = sendCommandToServer("DroPer   DataBASe " + randomName + "  ; ");
        assertFalse(response.contains("[OK]"), "DROP DATABASE case not working for DROPER, found->[OK], expected->[ERROR]");
        assertTrue(response.contains("[ERROR]"), "DROP DATABASE case not working for DROPER, found->[ERROR], expected->[OK]");
        randomName = generateRandomName();
        response = sendCommandToServer("DroP   Tableer " + randomName + "  ; ");
        assertFalse(response.contains("[OK]"), "DROP DATABASE case not working for Tableer, found->[OK], expected->[ERROR]");
        assertTrue(response.contains("[ERROR]"), "DROP DATABASE case not working for Tableer, found->[ERROR], expected->[OK]");
    }

    @Test
    public void testCreateTableParse() {
        String randomName = generateRandomName();
        String response = sendCommandToServer(" CReaTE       TablE        " + randomName + "  ; ");
        assertTrue(response.contains("[OK]"), "An attempt was made to access a non-existent table, however an [ERROR] tag was not returned");
        assertFalse(response.contains("[ERROR]"), "An attempt was made to access a non-existent table, however an [OK] tag was returned");
        response = sendCommandToServer("CREATE TablE " + randomName + " (name, mark, pass);");
        assertTrue(response.contains("[OK]"), "An attempt was made to access a non-existent table, however an [ERROR] tag was not returned");
        assertFalse(response.contains("[ERROR]"), "An attempt was made to access a non-existent table, however an [OK] tag was returned");
    }

    @Test
    public void testCreateDatabaseParse() {
        String randomName = generateRandomName();
        String response = sendCommandToServer(" CReaTE       DAtabASE        " + randomName + "  ; ");
        assertTrue(response.contains("[OK]"), "An attempt was made to access a non-existent table, however an [ERROR] tag was not returned");
        assertFalse(response.contains("[ERROR]"), "An attempt was made to access a non-existent table, however an [OK] tag was returned");
    }

    @Test
    public void testCreateTableWithAttributes() {
        String randomName = generateRandomName();
        String response = sendCommandToServer(" CReaTE       TaBLe        " + randomName + " ( name, mark, pass ) ; ");
        assertTrue(response.contains("[OK]"), "An attempt was made to access a non-existent table, however an [ERROR] tag was not returned");
        assertFalse(response.contains("[ERROR]"), "An attempt was made to access a non-existent table, however an [OK] tag was returned");

        randomName = generateRandomName();
        response = sendCommandToServer(" CReaTE       TaBLe        " + randomName + " ; ");
        assertTrue(response.contains("[OK]"), "An attempt was made to access a non-existent table, however an [ERROR] tag was not returned");
        assertFalse(response.contains("[ERROR]"), "An attempt was made to access a non-existent table, however an [OK] tag was returned");

        randomName = generateRandomName();
        response = sendCommandToServer(" CReaTE       TaBLe        " + randomName + " ( name, mark, pass, AHSB, 78sbc9 ) ; ");
        assertTrue(response.contains("[OK]"), "An attempt was made to access a non-existent table, however an [ERROR] tag was not returned");
        assertFalse(response.contains("[ERROR]"), "An attempt was made to access a non-existent table, however an [OK] tag was returned");
    }

    @Test
    public void testCreateTableWithAttributesFail() {
        String randomName = generateRandomName();
        String response = sendCommandToServer(" CReaTE       TaBLe        " + randomName + " ( name, mark pass ) ; ");
        assertFalse(response.contains("[OK]"), "An attempt was made to access a non-existent table, however an [OK] tag was not returned");
        assertTrue(response.contains("[ERROR]"), "An attempt was made to access a non-existent table, however an [ERROR] tag was returned");

        randomName = generateRandomName();
        response = sendCommandToServer(" CReaTE       TaBLe        " + randomName + " ( name, mark pass  ; ");
        assertFalse(response.contains("[OK]"), "An attempt was made to access a non-existent table, however an [OK] tag was not returned");
        assertTrue(response.contains("[ERROR]"), "An attempt was made to access a non-existent table, however an [ERROR] tag was returned");

        randomName = generateRandomName();
        response = sendCommandToServer(" CReaTE       TaBLe        " + randomName + " ( ) ; ");
        assertFalse(response.contains("[OK]"), "An attempt was made to access a non-existent table, however an [OK] tag was not returned");
        assertTrue(response.contains("[ERROR]"), "An attempt was made to access a non-existent table, however an [ERROR] tag was returned");
    }

    @Test
    public void testJoin() {
        String response = sendCommandToServer("JOIN coursework AND marks ON submission AND id;");
        assertTrue(response.contains("[OK]"), "An attempt was made to access a non-existent table, however an [ERROR] tag was not returned");
        assertFalse(response.contains("[ERROR]"), "An attempt was made to access a non-existent table, however an [OK] tag was returned");

        response = sendCommandToServer("JoiN coursework AND marks On submission AnD id;");
        assertTrue(response.contains("[OK]"), "An attempt was made to access a non-existent table, however an [ERROR] tag was not returned");
        assertFalse(response.contains("[ERROR]"), "An attempt was made to access a non-existent table, however an [OK] tag was returned");
    }

    @Test
    public void testJoinFail() {
//        String response = sendCommandToServer("JOIN AND marks ON submission AND id;");
//        assertFalse(response.contains("[OK]"), "An attempt was made to access a non-existent table, however an [ERROR] tag was not returned 1");
//        assertTrue(response.contains("[ERROR]"), "An attempt was made to access a non-existent table, however an [OK] tag was returned 1");

        String response = sendCommandToServer("JoiN coursework AND marks submission AnD id;");
        assertFalse(response.contains("[OK]"), "An attempt was made to access a non-existent table, however an [ERROR] tag was not returned 2");
        assertTrue(response.contains("[ERROR]"), "An attempt was made to access a non-existent table, however an [OK] tag was returned 2");

        response = sendCommandToServer("JoiN coursework AND marks submission id;");
        assertFalse(response.contains("[OK]"), "An attempt was made to access a non-existent table, however an [ERROR] tag was not returned 3");
        assertTrue(response.contains("[ERROR]"), "An attempt was made to access a non-existent table, however an [OK] tag was returned 3");

        response = sendCommandToServer("JoiN coursework AND marks TRuE AnD id;");
        assertFalse(response.contains("[OK]"), "An attempt was made to access a non-existent table, however an [ERROR] tag was not returned 4");
        assertTrue(response.contains("[ERROR]"), "An attempt was made to access a non-existent table, however an [OK] tag was returned 4");
    }

    @Test
    public void testAlter() {
        String response = sendCommandToServer("ALTER TABLE marks DROP pass;");
        assertTrue(response.contains("[OK]"), "Tried Running Alter command, expected [OK] 1");
        assertFalse(response.contains("[ERROR]"), "Tried Running Alter command, expected [OK], but recieved [ERROR] 1");

        response = sendCommandToServer("ALteR TAbLe mSArks DroP paSs;");
        assertTrue(response.contains("[OK]"), "Tried Running Alter command, expected [OK] 2");
        assertFalse(response.contains("[ERROR]"), "Tried Running Alter command, expected [OK], but recieved [ERROR] 2");

        response = sendCommandToServer("ALTER TABLE marks ADD pass;");
        assertTrue(response.contains("[OK]"), "Tried Running Alter command, expected [OK] 3");
        assertFalse(response.contains("[ERROR]"), "Tried Running Alter command, expected [OK], but recieved [ERROR] 3");
    }

    @Test
    public void testAlterFail() {
        String response = sendCommandToServer("ALTER marks DROP pass;");
        assertFalse(response.contains("[OK]"), "Tried Running Alter command, expected [ERROR] 1");
        assertTrue(response.contains("[ERROR]"), "Tried Running Alter command, expected [ERROR], but recieved [OK] 1");

        response = sendCommandToServer("ALteR TAbLe mSArks DOP paSs;");
        assertFalse(response.contains("[OK]"), "Tried Running Alter command, expected [ERROR] 2");
        assertTrue(response.contains("[ERROR]"), "Tried Running Alter command, expected [ERROR], but recieved [OK] 2");

        response = sendCommandToServer("ALTER TABLE marks ADD ;");
        assertFalse(response.contains("[OK]"), "Tried Running Alter command, expected [ERROR] 3");
        assertTrue(response.contains("[ERROR]"), "Tried Running Alter command, expected [ERROR], but recieved [OK] 3");
    }

    @Test
    public void testInsert() {
        String response = sendCommandToServer("INSERT INTO marks VALUES ('Chris', 20, FALSE);");
        assertTrue(response.contains("[OK]"), "Tried Running INSERT command, expected [OK] 1");
        assertFalse(response.contains("[ERROR]"), "Tried Running INSERT command, expected [OK], but recieved [ERROR] 1");

        response = sendCommandToServer("INSERT INTO marks VALUES ('Chris', 20, TRUE);");
        assertTrue(response.contains("[OK]"), "Tried Running INSERT command, expected [OK] 2");
        assertFalse(response.contains("[ERROR]"), "Tried Running INSERT command, expected [OK], but recieved [ERROR] 2");

        response = sendCommandToServer("INSERT INTO marks VALUES ('Chri@#$ s', '20', FALSE);");
        assertTrue(response.contains("[OK]"), "Tried Running INSERT command, expected [OK] 3");
        assertFalse(response.contains("[ERROR]"), "Tried Running INSERT command, expected [OK], but recieved [ERROR] 3");
    }

    @Test
    public void testInsertFail() {
        String response = sendCommandToServer("INSERT INTO marks VALUES ('Chris\"', '20', FALSE);");
        assertFalse(response.contains("[OK]"), "Tried Running INSERT command, expected [OK] 1");
        assertTrue(response.contains("[ERROR]"), "Tried Running INSERT command, expected [OK], but recieved [ERROR] 1");

        response = sendCommandToServer("INSERT INTO marks VALUES ('Chris\'', 20 TRUE);");
        assertFalse(response.contains("[OK]"), "Tried Running INSERT command, expected [OK] 2");
        assertTrue(response.contains("[ERROR]"), "Tried Running INSERT command, expected [OK], but recieved [ERROR] 2");

        response = sendCommandToServer("INSERT INTO marks VALUES 'Chri@#$ s', 20, FALSE);");
        assertFalse(response.contains("[OK]"), "Tried Running INSERT command, expected [OK] 3");
        assertTrue(response.contains("[ERROR]"), "Tried Running INSERT command, expected [OK], but recieved [ERROR] 3");

        response = sendCommandToServer("INSERT INTO marks VALUES ( );");
        assertFalse(response.contains("[OK]"), "Tried Running INSERT command, expected [OK] 4");
        assertTrue(response.contains("[ERROR]"), "Tried Running INSERT command, expected [OK], but recieved [ERROR] 4");

        response = sendCommandToServer("INSERT INTO marks VALUES ('Chris'', 20 TRUE);");
        assertFalse(response.contains("[OK]"), "Tried Running INSERT command, expected [OK] 5");
        assertTrue(response.contains("[ERROR]"), "Tried Running INSERT command, expected [OK], but recieved [ERROR] 5");

        response = sendCommandToServer("INSERT INTO marks VALUES ('Chris', 20 TRUE);");
        assertFalse(response.contains("[OK]"), "Tried Running INSERT command, expected [OK] 6");
        assertTrue(response.contains("[ERROR]"), "Tried Running INSERT command, expected [OK], but recieved [ERROR] 6");
    }

    @Test
    public void testIOController(){
        IOController ioc = new IOController();
        Database db = null;
        try {
            db = ioc.loadDatabase("testDB");
        }
        catch (Exception e){
            System.out.println("Exception occured");
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void testUpdate(){
//        UPDATE marks SET mark = 38 WHERE name == 'Chris';
        String response = sendCommandToServer("UPDATE marks SET mark = 38 WHERE name == 'Chris';");
        assertTrue(response.contains("[OK]"), "Tried Running UPDATE command, expected [OK] 1");
        assertFalse(response.contains("[ERROR]"), "Tried Running UPDATE command, expected [OK], but recieved [ERROR] 1");

//        response = sendCommandToServer("INSERT INTO marks VALUES ('Chris', 20, TRUE);");
//        assertTrue(response.contains("[OK]"), "Tried Running INSERT command, expected [OK] 2");
//        assertFalse(response.contains("[ERROR]"), "Tried Running INSERT command, expected [OK], but recieved [ERROR] 2");
//
//        response = sendCommandToServer("INSERT INTO marks VALUES ('Chri@#$ s', '20', FALSE);");
//        assertTrue(response.contains("[OK]"), "Tried Running INSERT command, expected [OK] 3");
//        assertFalse(response.contains("[ERROR]"), "Tried Running INSERT command, expected [OK], but recieved [ERROR] 3");
    }

    @Test
    public void testSELECT(){
        String response = sendCommandToServer("SELECT * FROM marks WHERE name == 'Chris';");
        assertTrue(response.contains("[OK]"), "Tried Running SELECT command, expected [OK] 1");
        assertFalse(response.contains("[ERROR]"), "Tried Running SELECT command, expected [OK], but recieved [ERROR] 1");

        response = sendCommandToServer("SELECT * FROM marks WHERE (pass == FALSE) AND (mark > 35);");
        assertTrue(response.contains("[OK]"), "Tried Running SELECT command, expected [OK] 2");
        assertFalse(response.contains("[ERROR]"), "Tried Running SELECT command, expected [OK], but recieved [ERROR] 2");
//
//        response = sendCommandToServer("INSERT INTO marks VALUES ('Chri@#$ s', '20', FALSE);");
//        assertTrue(response.contains("[OK]"), "Tried Running INSERT command, expected [OK] 3");
//        assertFalse(response.contains("[ERROR]"), "Tried Running INSERT command, expected [OK], but recieved [ERROR] 3");
    }

    @Test
    public void testAllDocCommandsParse(){
        String response = null;
        response = sendCommandToServer("CREATE DATABASE markbook;");
        assertTrue(response.contains("[OK]"), "Tried Running SELECT command, expected [OK] 2");
        assertFalse(response.contains("[ERROR]"), "Tried Running SELECT command, expected [OK], but recieved [ERROR] 2");
        response = sendCommandToServer("USE markbook;");
        assertTrue(response.contains("[OK]"), "Tried Running SELECT command, expected [OK] 2");
        assertFalse(response.contains("[ERROR]"), "Tried Running SELECT command, expected [OK], but recieved [ERROR] 2");
        response = sendCommandToServer("CREATE TABLE marks (name, mark, pass);");
        assertTrue(response.contains("[OK]"), "Tried Running SELECT command, expected [OK] 2");
        assertFalse(response.contains("[ERROR]"), "Tried Running SELECT command, expected [OK], but recieved [ERROR] 2");
        response = sendCommandToServer("INSERT INTO marks VALUES ('Simon', 65, TRUE);");
        assertTrue(response.contains("[OK]"), "Tried Running SELECT command, expected [OK] 2");
        assertFalse(response.contains("[ERROR]"), "Tried Running SELECT command, expected [OK], but recieved [ERROR] 2");
        response = sendCommandToServer("INSERT INTO marks VALUES ('Sion', 55, TRUE);");
        assertTrue(response.contains("[OK]"), "Tried Running SELECT command, expected [OK] 2");
        assertFalse(response.contains("[ERROR]"), "Tried Running SELECT command, expected [OK], but recieved [ERROR] 2");
        response = sendCommandToServer("INSERT INTO marks VALUES ('Rob', 35, FALSE);");
        assertTrue(response.contains("[OK]"), "Tried Running SELECT command, expected [OK] 2");
        assertFalse(response.contains("[ERROR]"), "Tried Running SELECT command, expected [OK], but recieved [ERROR] 2");
        response = sendCommandToServer("INSERT INTO marks VALUES ('Chris', 20, FALSE);");
        assertTrue(response.contains("[OK]"), "Tried Running SELECT command, expected [OK] 2");
        assertFalse(response.contains("[ERROR]"), "Tried Running SELECT command, expected [OK], but recieved [ERROR] 2");
        response = sendCommandToServer("SELECT * FROM marks;");
        assertTrue(response.contains("[OK]"), "Tried Running SELECT command, expected [OK] 2");
        assertFalse(response.contains("[ERROR]"), "Tried Running SELECT command, expected [OK], but recieved [ERROR] 2");
        response = sendCommandToServer("SELECT * FROM marks WHERE name != 'Sion';");
        assertTrue(response.contains("[OK]"), "Tried Running SELECT command, expected [OK] 2");
        assertFalse(response.contains("[ERROR]"), "Tried Running SELECT command, expected [OK], but recieved [ERROR] 2");
        response = sendCommandToServer("SELECT * FROM marks WHERE pass == TRUE;");
        assertTrue(response.contains("[OK]"), "Tried Running SELECT command, expected [OK] 2");
        assertFalse(response.contains("[ERROR]"), "Tried Running SELECT command, expected [OK], but recieved [ERROR] 2");
        response = sendCommandToServer("SELECT * FROM coursework;");
        assertTrue(response.contains("[OK]"), "Tried Running SELECT command, expected [OK] 2");
        assertFalse(response.contains("[ERROR]"), "Tried Running SELECT command, expected [OK], but recieved [ERROR] 2");
        response = sendCommandToServer("JOIN coursework AND marks ON submission AND id;");
        assertTrue(response.contains("[OK]"), "Tried Running SELECT command, expected [OK] 2");
        assertFalse(response.contains("[ERROR]"), "Tried Running SELECT command, expected [OK], but recieved [ERROR] 2");
        response = sendCommandToServer("UPDATE marks SET mark = 38 WHERE name == 'Chris';");
        assertTrue(response.contains("[OK]"), "Tried Running SELECT command, expected [OK] 2");
        assertFalse(response.contains("[ERROR]"), "Tried Running SELECT command, expected [OK], but recieved [ERROR] 2");
        response = sendCommandToServer("SELECT * FROM marks WHERE name == 'Chris';");
        assertTrue(response.contains("[OK]"), "Tried Running SELECT command, expected [OK] 2");
        assertFalse(response.contains("[ERROR]"), "Tried Running SELECT command, expected [OK], but recieved [ERROR] 2");
        response = sendCommandToServer("DELETE FROM marks WHERE name == 'Sion';");
        assertTrue(response.contains("[OK]"), "Tried Running SELECT command, expected [OK] 2");
        assertFalse(response.contains("[ERROR]"), "Tried Running SELECT command, expected [OK], but recieved [ERROR] 2");
        response = sendCommandToServer("SELECT * FROM marks;");
        assertTrue(response.contains("[OK]"), "Tried Running SELECT command, expected [OK] 2");
        assertFalse(response.contains("[ERROR]"), "Tried Running SELECT command, expected [OK], but recieved [ERROR] 2");
        response = sendCommandToServer("SELECT * FROM marks WHERE (pass == FALSE) AND (mark > 35);");
        assertTrue(response.contains("[OK]"), "Tried Running SELECT command, expected [OK] 2");
        assertFalse(response.contains("[ERROR]"), "Tried Running SELECT command, expected [OK], but recieved [ERROR] 2");
        response = sendCommandToServer("SELECT * FROM marks WHERE name LIKE 'i';");
        assertTrue(response.contains("[OK]"), "Tried Running SELECT command, expected [OK] 2");
        assertFalse(response.contains("[ERROR]"), "Tried Running SELECT command, expected [OK], but recieved [ERROR] 2");
        response = sendCommandToServer("SELECT id FROM marks WHERE pass == FALSE;");
        assertTrue(response.contains("[OK]"), "Tried Running SELECT command, expected [OK] 2");
        assertFalse(response.contains("[ERROR]"), "Tried Running SELECT command, expected [OK], but recieved [ERROR] 2");
        response = sendCommandToServer("SELECT name FROM marks WHERE mark>60;");
        assertTrue(response.contains("[OK]"), "Tried Running SELECT command, expected [OK] 2");
        assertFalse(response.contains("[ERROR]"), "Tried Running SELECT command, expected [OK], but recieved [ERROR] 2");
        response = sendCommandToServer("DELETE FROM marks WHERE mark<40;");
        assertTrue(response.contains("[OK]"), "Tried Running SELECT command, expected [OK] 2");
        assertFalse(response.contains("[ERROR]"), "Tried Running SELECT command, expected [OK], but recieved [ERROR] 2");
        response = sendCommandToServer("SELECT * FROM marks;");
        assertTrue(response.contains("[OK]"), "Tried Running SELECT command, expected [OK] 2");
        assertFalse(response.contains("[ERROR]"), "Tried Running SELECT command, expected [OK], but recieved [ERROR] 2");
        response = sendCommandToServer("ALTER TABLE marks ADD age;");
        assertTrue(response.contains("[OK]"), "Tried Running SELECT command, expected [OK] 2");
        assertFalse(response.contains("[ERROR]"), "Tried Running SELECT command, expected [OK], but recieved [ERROR] 2");
        response = sendCommandToServer("SELECT * FROM marks;");
        assertTrue(response.contains("[OK]"), "Tried Running SELECT command, expected [OK] 2");
        assertFalse(response.contains("[ERROR]"), "Tried Running SELECT command, expected [OK], but recieved [ERROR] 2");
        response = sendCommandToServer("UPDATE marks SET age = 35 WHERE name == 'Simon';");
        assertTrue(response.contains("[OK]"), "Tried Running SELECT command, expected [OK] 2");
        assertFalse(response.contains("[ERROR]"), "Tried Running SELECT command, expected [OK], but recieved [ERROR] 2");
        response = sendCommandToServer("SELECT * FROM marks;");
        assertTrue(response.contains("[OK]"), "Tried Running SELECT command, expected [OK] 2");
        assertFalse(response.contains("[ERROR]"), "Tried Running SELECT command, expected [OK], but recieved [ERROR] 2");
        response = sendCommandToServer("ALTER TABLE marks DROP pass;");
        assertTrue(response.contains("[OK]"), "Tried Running SELECT command, expected [OK] 2");
        assertFalse(response.contains("[ERROR]"), "Tried Running SELECT command, expected [OK], but recieved [ERROR] 2");
        response = sendCommandToServer("SELECT * FROM marks;");
        assertTrue(response.contains("[OK]"), "Tried Running SELECT command, expected [OK] 2");
        assertFalse(response.contains("[ERROR]"), "Tried Running SELECT command, expected [OK], but recieved [ERROR] 2");
    }
}
