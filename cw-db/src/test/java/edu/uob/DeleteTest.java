package edu.uob;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Paths;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class DeleteTest {
    DBServer dbServer;

    @BeforeAll
    public static void makeFolder() {
        File file = new File(Paths.get("databases").toUri());
        if (!file.exists()) file.mkdir();
    }

    @BeforeEach
    public void setup() {
        if (dbServer == null)
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
    public void testDelete() {
        String randomDBName = generateRandomName();
        String randomTableName = generateRandomName();
        dbServer.handleCommand("CREATE DATABASE " + randomDBName + ";");
        dbServer.handleCommand("use " + randomDBName.toUpperCase() + ";");
        dbServer.handleCommand("CREATE TABLE " + randomTableName + "(name, age, pass);");
        dbServer.handleCommand("INSERT INTO " + randomTableName.toUpperCase() + " VALUES ('Simon', 65, TRUE);"); // test for insert into table when columns not present
        dbServer.handleCommand("INSERT INTO " + randomTableName.toUpperCase() + " VALUES ('Sion', 55, TRUE);");
        dbServer.handleCommand("INSERT INTO " + randomTableName.toUpperCase() + " VALUES ('Rob', 35, FALSE);");
        dbServer.handleCommand("INSERT INTO " + randomTableName.toUpperCase() + " VALUES ('Chris', 20, FALSE);");
        String response = "DELETE FROM " + randomTableName + " WHERE name == 'Sion';";
        response = dbServer.handleCommand(response);
        assert (response.contains("[OK]"));
    }

    @Test
    public void testDeleteCaseInsensitive() {
        String randomDBName = generateRandomName();
        String randomTableName = generateRandomName();
        dbServer.handleCommand("CREATE DATABASE " + randomDBName + ";");
        dbServer.handleCommand("use " + randomDBName.toUpperCase() + ";");
        dbServer.handleCommand("CREATE TABLE " + randomTableName + "(name, age, pass);");
        dbServer.handleCommand("INSERT INTO " + randomTableName.toUpperCase() + " VALUES ('Simon', 65, TRUE);"); // test for insert into table when columns not present
        dbServer.handleCommand("INSERT INTO " + randomTableName.toUpperCase() + " VALUES ('Sion', 55, TRUE);");
        dbServer.handleCommand("INSERT INTO " + randomTableName.toUpperCase() + " VALUES ('Rob', 35, FALSE);");
        dbServer.handleCommand("INSERT INTO " + randomTableName.toUpperCase() + " VALUES ('Chris', 20, FALSE);");
        String response = "DELETE FROM " + randomTableName.toUpperCase() + " WHERE name == 'Sion';";
        response = dbServer.handleCommand(response);
        assert (response.contains("[OK]"));
        response = dbServer.handleCommand("Select * from "+randomTableName+";");
        assert(response.split("\n").length == 5);
    }

    @Test
    public void testDeleteNonExistentTable() {
        String randomDBName = generateRandomName();
        String randomTableName = generateRandomName();
        dbServer.handleCommand("CREATE DATABASE " + randomDBName + ";");
        dbServer.handleCommand("use " + randomDBName.toUpperCase() + ";");
        dbServer.handleCommand("CREATE TABLE " + randomTableName + "(name, age, pass);");
        dbServer.handleCommand("INSERT INTO " + randomTableName.toUpperCase() + " VALUES ('Simon', 65, TRUE);"); // test for insert into table when columns not present
        dbServer.handleCommand("INSERT INTO " + randomTableName.toUpperCase() + " VALUES ('Sion', 55, TRUE);");
        dbServer.handleCommand("INSERT INTO " + randomTableName.toUpperCase() + " VALUES ('Rob', 35, FALSE);");
        dbServer.handleCommand("INSERT INTO " + randomTableName.toUpperCase() + " VALUES ('Chris', 20, FALSE);");
        String response = "DELETE FROM " + randomTableName.toUpperCase()+"sndfj" + " WHERE name == 'Sion';";
        response = dbServer.handleCommand(response);
        assert (response.contains("[ERROR]"));
        response = dbServer.handleCommand("Select * from "+randomTableName+";");
        assert(response.split("\n").length == 6);
    }

    @Test
    public void testDeleteWithNoValueMatch() {
        String randomDBName = generateRandomName();
        String randomTableName = generateRandomName();
        dbServer.handleCommand("CREATE DATABASE " + randomDBName + ";");
        dbServer.handleCommand("use " + randomDBName.toUpperCase() + ";");
        dbServer.handleCommand("CREATE TABLE " + randomTableName + "(name, age, pass);");
        dbServer.handleCommand("INSERT INTO " + randomTableName.toUpperCase() + " VALUES ('Simon', 65, TRUE);"); // test for insert into table when columns not present
        dbServer.handleCommand("INSERT INTO " + randomTableName.toUpperCase() + " VALUES ('Sion', 55, TRUE);");
        dbServer.handleCommand("INSERT INTO " + randomTableName.toUpperCase() + " VALUES ('Rob', 35, FALSE);");
        dbServer.handleCommand("INSERT INTO " + randomTableName.toUpperCase() + " VALUES ('Chris', 20, FALSE);");
        String response = "DELETE FROM " + randomTableName.toUpperCase()+ " WHERE name == 'John';";
        response = dbServer.handleCommand(response);
        assert (response.contains("[OK]"));
        response = dbServer.handleCommand("Select * from "+randomTableName+";");
        assert(response.split("\n").length == 6);
    }

    @Test
    public void testDeleteNoCondition() {
        String randomDBName = generateRandomName();
        String randomTableName = generateRandomName();
        dbServer.handleCommand("CREATE DATABASE " + randomDBName + ";");
        dbServer.handleCommand("use " + randomDBName.toUpperCase() + ";");
        dbServer.handleCommand("CREATE TABLE " + randomTableName + "(name, age, pass);");
        dbServer.handleCommand("INSERT INTO " + randomTableName.toUpperCase() + " VALUES ('Simon', 65, TRUE);"); // test for insert into table when columns not present
        dbServer.handleCommand("INSERT INTO " + randomTableName.toUpperCase() + " VALUES ('Sion', 55, TRUE);");
        dbServer.handleCommand("INSERT INTO " + randomTableName.toUpperCase() + " VALUES ('Rob', 35, FALSE);");
        dbServer.handleCommand("INSERT INTO " + randomTableName.toUpperCase() + " VALUES ('Chris', 20, FALSE);");
        String response = "DELETE FROM " + randomTableName.toUpperCase()+ " WHERE ();";
        response = dbServer.handleCommand(response);
        assert (response.contains("[ERROR]"));
    }

    @Test
    public void testDeleteAllValues() {
        String randomDBName = generateRandomName();
        String randomTableName = generateRandomName();
        dbServer.handleCommand("CREATE DATABASE " + randomDBName + ";");
        dbServer.handleCommand("use " + randomDBName.toUpperCase() + ";");
        dbServer.handleCommand("CREATE TABLE " + randomTableName + "(name, age, pass);");
        dbServer.handleCommand("INSERT INTO " + randomTableName.toUpperCase() + " VALUES ('Simon', 65, TRUE);"); // test for insert into table when columns not present
        dbServer.handleCommand("INSERT INTO " + randomTableName.toUpperCase() + " VALUES ('Sion', 55, TRUE);");
        dbServer.handleCommand("INSERT INTO " + randomTableName.toUpperCase() + " VALUES ('Rob', 35, FALSE);");
        dbServer.handleCommand("INSERT INTO " + randomTableName.toUpperCase() + " VALUES ('Chris', 20, FALSE);");
        String response = "DELETE FROM " + randomTableName.toUpperCase()+ " WHERE (pass == true or pass == false);";
        response = dbServer.handleCommand(response);
        assert (response.contains("[OK]"));
        response = dbServer.handleCommand("Select * from "+randomTableName+";");
        assert(response.split("\n").length == 2);
    }
}


