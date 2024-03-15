package edu.uob;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Paths;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class DropDatabase {
    DBServer dbServer;
    @BeforeEach
    public void setup(){
        if(dbServer == null) dbServer = new DBServer();
    }
    @BeforeAll
    public static void makeFolder(){
        File file = new File(Paths.get("databases").toUri());
        if(!file.exists()) file.mkdir();
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
            if(internalDirectory.getName().equalsIgnoreCase("testDB")) continue;
            File[] internalFileList = internalDirectory.listFiles();
            if(internalFileList == null) continue;
            for(File f : internalDirectory.listFiles()) f.delete();
            internalDirectory.delete();
        }
    }

    @Test
    public void testSimple(){
        String randomName = generateRandomName();
        String response = "CREATE DATABASE " + randomName + ";";
        dbServer.handleCommand(response);
        response = "DROP DATABASE " + randomName + ";";
        response = dbServer.handleCommand(response);
        assert(response.contains("[OK]"));
        assert(dbServer.handleCommand("USE "+randomName+";").contains("[ERROR]"));
    }

    @Test
    public void testCaseInsensitive(){
        String randomName = generateRandomName();
        String response = "CREATE DATABASE " + randomName + ";";
        dbServer.handleCommand(response);
        response = "DroP DAtabASE " + randomName.toUpperCase() + ";";
        response = dbServer.handleCommand(response);
        assert(response.contains("[OK]"));
        assert(dbServer.handleCommand("USE "+randomName+";").contains("[ERROR]"));
    }

    @Test
    public void testSpaces(){
        String randomName = generateRandomName();
        String response = "CREATE DATABASE " + randomName + ";";
        dbServer.handleCommand(response);
        response = "   DroP    DAtabASE    " + randomName.toUpperCase() + "  ;  ";
        response = dbServer.handleCommand(response);
        assert(response.contains("[OK]"));
        assert(dbServer.handleCommand("USE "+randomName+";").contains("[ERROR]"));
    }

    @Test
    public void testNonExistentDB(){
        String randomName = generateRandomName();
        String response = "   DroP    DAtabASE    " + randomName.toUpperCase() + "  ;  ";
        response = dbServer.handleCommand(response);
        assert(response.contains("[ERROR]"));
        assert(dbServer.handleCommand("USE "+randomName+";").contains("[ERROR]"));
    }

    @Test
    public void testWrongSpelling(){
        String randomName = generateRandomName();
        dbServer.handleCommand("CREATE DATABASE "+randomName+";");
        String response = "   DRp    DAtabASE    " + randomName.toUpperCase() + "  ;  ";
        response = dbServer.handleCommand(response);
        assert(response.contains("[ERROR]"));
    }

    @Test
    public void testMissingDBName(){
        String randomName = generateRandomName();
        dbServer.handleCommand("CREATE DATABASE "+randomName+";");
        String response = "   DRp    DAtabASE    " + "  ;  ";
        response = dbServer.handleCommand(response);
        assert(response.contains("[ERROR]"));
    }

    @Test
    public void testAdditionalFields(){
        String randomName = generateRandomName();
        dbServer.handleCommand("CREATE DATABASE "+randomName+";");
        String response = "   DRp    DAtabASE    " +randomName+ "  VALUES (89, 37) ;  ";
        response = dbServer.handleCommand(response);
        assert(response.contains("[ERROR]"));
    }
}
