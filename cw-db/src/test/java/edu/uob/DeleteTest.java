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
//    @Test
//    public void testAlter(){
//        String randomName = generateRandomName();
//        String response = "CREATE DATABASE " + randomName + ";";
//        dbServer.handleCommand(response);
//        response = "use " + randomName + ";";
//        response = dbServer.handleCommand(response);
//        String randomTableName = generateRandomName();
//        response = "CREATE TABLE " + randomTableName + ";";
//        response = dbServer.handleCommand(response);
//        response = "Alter Table " + randomTableName + " ADD age;";
//        response = dbServer.handleCommand(response);
//        assert(response.contains("[OK]"));
//        response = dbServer.handleCommand("SELECT age from "+randomTableName+";");
//        assert(response.contains("age"));
//    }
//
//    @Test
//    public void testAlterExtraSpaces(){
//        String randomName = generateRandomName();
//        String response = "CREATE DATABASE " + randomName + ";";
//        dbServer.handleCommand(response);
//        response = "use " + randomName + ";";
//        response = dbServer.handleCommand(response);
//        String randomTableName = generateRandomName();
//        response = "CREATE TABLE " + randomTableName + ";";
//        response = dbServer.handleCommand(response);
//        response = "          Alter  Table  " + randomTableName.substring(0, 4).toUpperCase() + randomTableName.substring(4) + "  ADD   age;";
//        response = dbServer.handleCommand(response);
//        assert(response.contains("[OK]"));
//        response = dbServer.handleCommand("SELECT age from "+randomTableName+";");
//        assert(response.contains("age"));
//    }
//
//    @Test
//    public void testAlterDrop(){
//        String randomName = generateRandomName();
//        String response = "CREATE DATABASE " + randomName + ";";
//        dbServer.handleCommand(response);
//        response = "use " + randomName + ";";
//        response = dbServer.handleCommand(response);
//        String randomTableName = generateRandomName();
//        response = "CREATE TABLE " + randomTableName + "(age);";
//        response = dbServer.handleCommand(response);
//        response = "ALTER TABLE "+ randomTableName+ " DROP age;";
//        response = dbServer.handleCommand(response);
//        assert(response.contains("[OK]"));
//        response = dbServer.handleCommand("SELECT age from "+randomTableName+";");
//        assert(!response.contains("age"));
//    }
//
//    @Test
//    public void testAlterTableNameMissing(){
//        String randomName = generateRandomName();
//        String response = "CREATE DATABASE " + randomName + ";";
//        dbServer.handleCommand(response);
//        response = "use " + randomName + ";";
//        response = dbServer.handleCommand(response);
//        String randomTableName = generateRandomName();
//        response = "CREATE TABLE " + randomTableName + "(age);";
//        response = dbServer.handleCommand(response);
//        response = "ALTER TABLE " + " DROP age;";
//        response = dbServer.handleCommand(response);
//        assert(response.contains("[ERROR]"));
//    }
//
//
//    @Test
//    public void testAlterTableMissing(){
//        String randomName = generateRandomName();
//        String response = "CREATE DATABASE " + randomName + ";";
//        dbServer.handleCommand(response);
//        response = "use " + randomName + ";";
//        response = dbServer.handleCommand(response);
//        String randomTableName = generateRandomName();
//        response = "CREATE TABLE " + randomTableName + "(age);";
//        response = dbServer.handleCommand(response);
//        response = "ALTER " +randomTableName+ " DROP age;";
//        response = dbServer.handleCommand(response);
//        assert(response.contains("[ERROR]"));
//    }
//
//    @Test
//    public void testAlterColumnMissing(){
//        String randomName = generateRandomName();
//        String response = "CREATE DATABASE " + randomName + ";";
//        dbServer.handleCommand(response);
//        response = "use " + randomName + ";";
//        response = dbServer.handleCommand(response);
//        String randomTableName = generateRandomName();
//        response = "CREATE " + randomTableName + "(age);";
//        response = dbServer.handleCommand(response);
//        response = "ALTER TABLE " +randomTableName+ " DROP;";
//        response = dbServer.handleCommand(response);
//        assert(response.contains("[ERROR]"));
//    }
//
//    @Test
//    public void testAlterAlterationTypeMissing(){
//        String randomName = generateRandomName();
//        String response = "CREATE DATABASE " + randomName + ";";
//        dbServer.handleCommand(response);
//        response = "use " + randomName + ";";
//        response = dbServer.handleCommand(response);
//        String randomTableName = generateRandomName();
//        response = "CREATE " + randomTableName + "(age);";
//        response = dbServer.handleCommand(response);
//        response = "ALTER TABLE " +randomTableName+ " age;";
//        response = dbServer.handleCommand(response);
//        assert(response.contains("[ERROR]"));
//    }
//
//    @Test
//    public void testAlterADDExistingColumn(){
//        String randomName = generateRandomName();
//        String response = "CREATE DATABASE " + randomName + ";";
//        dbServer.handleCommand(response);
//        response = "use " + randomName + ";";
//        response = dbServer.handleCommand(response);
//        String randomTableName = generateRandomName();
//        response = "CREATE " + randomTableName + "(age);";
//        response = dbServer.handleCommand(response);
//        response = "ALTER TABLE " +randomTableName+ " ADD age;";
//        response = dbServer.handleCommand(response);
//        assert(response.contains("[ERROR]"));
//    }
//
//    @Test
//    public void testAlterDropNonExistingColumn(){
//        String randomName = generateRandomName();
//        String response = "CREATE DATABASE " + randomName + ";";
//        dbServer.handleCommand(response);
//        response = "use " + randomName + ";";
//        response = dbServer.handleCommand(response);
//        String randomTableName = generateRandomName();
//        response = "CREATE " + randomTableName + "(marks);";
//        response = dbServer.handleCommand(response);
//        response = "ALTER TABLE " +randomTableName+ " ADD age;";
//        response = dbServer.handleCommand(response);
//        assert(response.contains("[ERROR]"));
//    }
}
