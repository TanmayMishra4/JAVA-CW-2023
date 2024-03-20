package edu.uob;

import edu.uob.Controller.DBController;
import org.junit.jupiter.api.*;

import java.io.File;
import java.nio.file.Paths;

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
    @AfterAll
    public static void cleanFolder(){
        File file = new File(Paths.get("databases").toAbsolutePath().toString());
        File[] fileList = file.listFiles();
        if(fileList == null) return;
        for(File internalDirectory : file.listFiles()){
            File[] internalFileList = internalDirectory.listFiles();
            if(internalFileList == null) continue;
            for(File f : internalDirectory.listFiles()) f.delete();
            internalDirectory.delete();
        }
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
}
