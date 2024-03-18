package edu.uob;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Paths;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class UpdateTest {
    DBServer dbServer;
    @BeforeAll
    public static void makeFolder(){
        File file = new File(Paths.get("databases").toUri());
        if(!file.exists()) file.mkdir();
    }
    @BeforeEach
    public void setup(){
        if(dbServer == null)
            dbServer = new DBServer();
    }

    String generateRandomName() {
        StringBuilder randomName = new StringBuilder();
        for (int i = 0; i < 10; i++) randomName.append((char) (97 + (Math.random() * 25.0)));
        return randomName.toString();
    }

    @AfterAll
    public static void cleanFolder() {
        File file = new File(Paths.get("databases").toAbsolutePath().toString());
        File[] fileList = file.listFiles();
        if (fileList == null) return;
        for (File internalDirectory : fileList) {
            File[] internalFileList = internalDirectory.listFiles();
            if (internalFileList == null) continue;
            for (File f : internalFileList) f.delete();
            internalDirectory.delete();
        }
    }
    // TODO remove all instances of testDB testing
    @Test
    public void testUpdate(){
        // UPDATE marks SET age = 35 WHERE name == 'Simon';
        String randomDBName = generateRandomName();
        String randomTableName = generateRandomName();
        dbServer.handleCommand("CREATE DATABASE "+randomDBName+";");
        dbServer.handleCommand("use "+randomDBName.toUpperCase()+";");
        dbServer.handleCommand("CREATE TABLE "+randomTableName+"(name, mark, pass);");
        dbServer.handleCommand("INSERT INTO " +randomTableName.toUpperCase()+ " VALUES ('Simon', 65, TRUE);"); // test for insert into table when columns not present
        dbServer.handleCommand("INSERT INTO " +randomTableName.toUpperCase()+ " VALUES ('Sion', 55, TRUE);");
        dbServer.handleCommand("INSERT INTO " +randomTableName.toUpperCase()+ " VALUES ('Rob', 35, FALSE);");
        dbServer.handleCommand("INSERT INTO " +randomTableName.toUpperCase()+ " VALUES ('Chris', 20, FALSE);");
        String response = "UPDATE "+randomTableName.toUpperCase()+ " SET mark = 38 WHERE name == 'Chris';";
        response = dbServer.handleCommand(response);
        assert(response.contains("[OK]"));
    }

    @Test
    public void testUpdateNonExistentTable(){
        // UPDATE marks SET age = 35 WHERE name == 'Simon';
        String randomDBName = generateRandomName();
        String randomTableName = generateRandomName();
        dbServer.handleCommand("CREATE DATABASE "+randomDBName+";");
        dbServer.handleCommand("use "+randomDBName.toUpperCase()+";");
        dbServer.handleCommand("CREATE TABLE "+randomTableName+"(name, mark, pass);");
        dbServer.handleCommand("INSERT INTO " +randomTableName.toUpperCase()+ " VALUES ('Simon', 65, TRUE);"); // test for insert into table when columns not present
        dbServer.handleCommand("INSERT INTO " +randomTableName.toUpperCase()+ " VALUES ('Sion', 55, TRUE);");
        dbServer.handleCommand("INSERT INTO " +randomTableName.toUpperCase()+ " VALUES ('Rob', 35, FALSE);");
        dbServer.handleCommand("INSERT INTO " +randomTableName.toUpperCase()+ " VALUES ('Chris', 20, FALSE);");
        String response = "UPDATE "+randomTableName.toUpperCase()+"nskc"+ " SET mark = 38 WHERE name == 'Chris';";
        response = dbServer.handleCommand(response);
        assert(response.contains("[ERROR]"));
    }

    @Test
    public void testUpdateNonExistentValue(){
        // UPDATE marks SET age = 35 WHERE name == 'Simon';
        String randomDBName = generateRandomName();
        String randomTableName = generateRandomName();
        dbServer.handleCommand("CREATE DATABASE "+randomDBName+";");
        dbServer.handleCommand("use "+randomDBName.toUpperCase()+";");
        dbServer.handleCommand("CREATE TABLE "+randomTableName+"(name, mark, pass);");
        dbServer.handleCommand("INSERT INTO " +randomTableName.toUpperCase()+ " VALUES ('Simon', 65, TRUE);"); // test for insert into table when columns not present
        dbServer.handleCommand("INSERT INTO " +randomTableName.toUpperCase()+ " VALUES ('Sion', 55, TRUE);");
        dbServer.handleCommand("INSERT INTO " +randomTableName.toUpperCase()+ " VALUES ('Rob', 35, FALSE);");
        dbServer.handleCommand("INSERT INTO " +randomTableName.toUpperCase()+ " VALUES ('Chris', 20, FALSE);");
        String response = "UPDATE "+randomTableName.toUpperCase()+ " SET mark = 138 WHERE name == 'John';";
        response = dbServer.handleCommand(response);
        assert(response.contains("[OK]"));
    }

    @Test
    public void testUpdate2(){
        // UPDATE marks SET age = 35 WHERE name == 'Simon';
        String randomDBName = generateRandomName();
        String randomTableName = generateRandomName();
        dbServer.handleCommand("CREATE DATABASE "+randomDBName+";");
        dbServer.handleCommand("use "+randomDBName.toUpperCase()+";");
        dbServer.handleCommand("CREATE TABLE "+randomTableName+"(name, age, pass);");
        dbServer.handleCommand("INSERT INTO " +randomTableName.toUpperCase()+ " VALUES ('Simon', 65, TRUE);"); // test for insert into table when columns not present
        dbServer.handleCommand("INSERT INTO " +randomTableName.toUpperCase()+ " VALUES ('Sion', 55, TRUE);");
        dbServer.handleCommand("INSERT INTO " +randomTableName.toUpperCase()+ " VALUES ('Rob', 35, FALSE);");
        dbServer.handleCommand("INSERT INTO " +randomTableName.toUpperCase()+ " VALUES ('Chris', 20, FALSE);");
        String response = "UPDATE "+randomTableName.toUpperCase()+ " SET AGE = 35 WHERE NAME == 'Simon';";
        response = dbServer.handleCommand(response);
        assert(response.contains("[OK]"));
    }

    @Test
    public void testUpdate3(){
        // UPDATE marks SET age = 35 WHERE name == 'Simon';
        String randomDBName = generateRandomName();
        String randomTableName = generateRandomName();
        dbServer.handleCommand("CREATE DATABASE "+randomDBName+";");
        dbServer.handleCommand("use "+randomDBName.toUpperCase()+";");
        dbServer.handleCommand("CREATE TABLE "+randomTableName+"(name, age, pass);");
        dbServer.handleCommand("INSERT INTO " +randomTableName.toUpperCase()+ " VALUES ('Simon', 65, TRUE);"); // test for insert into table when columns not present
        dbServer.handleCommand("INSERT INTO " +randomTableName.toUpperCase()+ " VALUES ('Sion', 55, TRUE);");
        dbServer.handleCommand("INSERT INTO " +randomTableName.toUpperCase()+ " VALUES ('Rob', 35, FALSE);");
        dbServer.handleCommand("INSERT INTO " +randomTableName.toUpperCase()+ " VALUES ('Chris', 20, FALSE);");
        String response = "UPDATE "+randomTableName.toUpperCase()+ " SET AGE = 35 WHERE ((NAME == 'Simon') OR (AGE == 55)) OR ((NAME == 'Rob') OR (pass == FALSE));";
        response = dbServer.handleCommand(response);
        assert(response.contains("[OK]"));
    }

    @Test
    public void testUpdateMissingCondition(){
        // UPDATE marks SET age = 35 WHERE name == 'Simon';
        String randomDBName = generateRandomName();
        String randomTableName = generateRandomName();
        dbServer.handleCommand("CREATE DATABASE "+randomDBName+";");
        dbServer.handleCommand("use "+randomDBName.toUpperCase()+";");
        dbServer.handleCommand("CREATE TABLE "+randomTableName+"(name, age, pass);");
        dbServer.handleCommand("INSERT INTO " +randomTableName.toUpperCase()+ " VALUES ('Simon', 65, TRUE);"); // test for insert into table when columns not present
        dbServer.handleCommand("INSERT INTO " +randomTableName.toUpperCase()+ " VALUES ('Sion', 55, TRUE);");
        dbServer.handleCommand("INSERT INTO " +randomTableName.toUpperCase()+ " VALUES ('Rob', 35, FALSE);");
        dbServer.handleCommand("INSERT INTO " +randomTableName.toUpperCase()+ " VALUES ('Chris', 20, FALSE);");
        String response = "UPDATE "+randomTableName.toUpperCase()+ " SET AGE = 35 WHERE ;";
        response = dbServer.handleCommand(response);
        assert(response.contains("[ERROR]"));
    }
}

