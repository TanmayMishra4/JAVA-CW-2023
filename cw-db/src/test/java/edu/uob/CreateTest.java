package edu.uob;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.nio.file.Paths;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class CreateTest {
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
    public void testCreateQuery(){
        String randomName = generateRandomName();
        String response = "CREATE DATABASE " +  randomName + ";";
        response = dbServer.handleCommand(response);
        assert(response.contains("[OK]"));
    }

    @Test
    public void testExtraSpaces(){
        String randomName = generateRandomName();
        String response = "   CREATE           DATABASE " +  randomName + "     ;   ";
        response = dbServer.handleCommand(response);
        assert(response.contains("[OK]"));
    }

    @Test
    public void testCaseInsensitive(){
        String randomName = generateRandomName();
        String response = "   CReaTe DAtabAsE " +  randomName.substring(0, 4).toUpperCase() + randomName.substring(4) + "     ;   ";
        response = dbServer.handleCommand(response);
        assert(response.contains("[OK]"));
    }

    @Test
    public void testCaseInsensitiveAndSpaces(){
        String randomName = generateRandomName();
        String response = "   CReaTe         DAtabAsE     " +  randomName.substring(0, 4).toUpperCase() + randomName.substring(4) + "     ;   ";
        response = dbServer.handleCommand(response);
        assert(response.contains("[OK]"));
    }

    @Test
    public void testWrongKeywordSpelling(){
        String randomName = generateRandomName();
        String response = "   Creat      DAtabAsE     " +  randomName.substring(0, 4).toUpperCase() + randomName.substring(4) + "     ;   ";
        response = dbServer.handleCommand(response);
        assert(response.contains("[ERROR]"));
    }

    @Test
    public void testWrongKeywordSpelling2(){
        String randomName = generateRandomName();
        String response = "   Create      Dastabase     " +  randomName.substring(0, 4).toUpperCase() + randomName.substring(4) + "     ;   ";
        response = dbServer.handleCommand(response);
        assert(response.contains("[ERROR]"));
    }

    @Test
    public void testSemiColonMissing(){
        String randomName = generateRandomName();
        String response = "   Create      Dastabase     " +  randomName.substring(0, 4).toUpperCase() + randomName.substring(4) + "       ";
        response = dbServer.handleCommand(response);
        assert(response.contains("[ERROR]"));
    }

    @Test
    public void testMissingDBName(){
        String randomName = generateRandomName();
        String response = "   Create      Dastabase     " + "   ;    ";
        response = dbServer.handleCommand(response);
        assert(response.contains("[ERROR]"));
    }

    @Test
    public void testEmptyCommand(){
        String randomName = generateRandomName();
        String response = "";
        response = dbServer.handleCommand(response);
        assert(response.contains("[ERROR]"));
    }

    @Test
    public void testEmptyCommand2(){
        String randomName = generateRandomName();
        String response = ";";
        response = dbServer.handleCommand(response);
        assert(response.contains("[ERROR]"));
    }

    @Test
    public void testEmptyCommand3(){
        String randomName = generateRandomName();
        String response = " ;";
        response = dbServer.handleCommand(response);
        assert(response.contains("[ERROR]"));
    }

    @Test
    public void testEmptyWrongCommandSpaces(){
        String randomName = generateRandomName();
        String response = " CRE TE Database wrongDB;";
        response = dbServer.handleCommand(response);
        assert(response.contains("[ERROR]"));
    }

    @Test
    public void testMultipleSemiColons(){
        String randomName = generateRandomName();
        String response = " CREATE Database wrongDB;;";
        response = dbServer.handleCommand(response);
        assert(response.contains("[ERROR]"));
    }

    @Test
    public void testCreateWithSameDBAndTableName(){
        String randomName = generateRandomName();
        String response = " CREATE Database testdb;";
        response = dbServer.handleCommand(response);
        dbServer.handleCommand("use testdb;");
        response = "CREATE TABLE testdb;";
        response = dbServer.handleCommand(response);
        assert(response.contains("[OK]"));
    }

    @Test
    public void testCreateWithSameDBAndTableName2(){
        String randomName = generateRandomName().toUpperCase();
        String response = " CREATE Database "+randomName+";";
        response = dbServer.handleCommand(response);
        response = dbServer.handleCommand("use "+randomName.toLowerCase()+";");
        assert(response.contains("[OK]"));
    }

    @Test
    public void testCreateDifferentCaseTableName(){
        String randomName = generateRandomName().toUpperCase();
        String response = " CREATE Database "+randomName+";";
        response = dbServer.handleCommand(response);
        response = dbServer.handleCommand("use "+randomName.toLowerCase()+";");
        assert(response.contains("[OK]"));
        randomName = generateRandomName();
        response = "CREATE TABLE "+randomName.toUpperCase()+";";
        response = dbServer.handleCommand(response);
        assert(response.contains("[OK]"));
        response = "select * from "+randomName.toLowerCase()+";";
        response = dbServer.handleCommand(response);
        assert(response.contains("[OK]"));
    }
}
