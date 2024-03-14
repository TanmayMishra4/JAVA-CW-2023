package edu.uob;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Paths;

public class UseTest {
    DBServer dbServer;
    String generateRandomName() {
        StringBuilder randomName = new StringBuilder();
        for (int i = 0; i < 10; i++) randomName.append((char) (97 + (Math.random() * 25.0)));
        return randomName.toString();
    }

    @BeforeEach
    public void setup(){
        if(dbServer == null)
            dbServer = new DBServer();
    }

    @AfterAll
    public static void cleanFolder(){
        File file = new File(Paths.get("databases").toAbsolutePath().toString());
        for(File internalDirectory : file.listFiles()){
            if(internalDirectory.getName().equalsIgnoreCase("testDB")) continue;
            for(File f : internalDirectory.listFiles()) f.delete();
            internalDirectory.delete();
        }
    }

    @Test
    public void testUse(){
        String randomName = generateRandomName();
        String response = "CREATE DATABASE " + randomName + ";";
        dbServer.handleCommand(response);
        response = "USE " + randomName + ";";
        response = dbServer.handleCommand(response);
        assert(response.contains("[OK]"));
    }

    @Test
    public void testUseCaseInsensitive(){
        String randomName = generateRandomName();
        String response = "CREATE DATABASE " + randomName + ";";
        dbServer.handleCommand(response);
        response = "UsE " + randomName.toUpperCase() + ";";
        response = dbServer.handleCommand(response);
        assert(response.contains("[OK]"));
    }

    @Test
    public void testUseWithSpaces(){
        String randomName = generateRandomName();
        String response = "CREATE DATABASE " + randomName + ";";
        dbServer.handleCommand(response);
        response = "        USE          " + randomName + "  ;   ";
        response = dbServer.handleCommand(response);
        assert(response.contains("[OK]"));
    }

    @Test
    public void testUseSpacesAndCase(){
        String randomName = generateRandomName();
        String response = "CREATE DATABASE " + randomName + ";";
        dbServer.handleCommand(response);
        response = "  Use   " + randomName + "            ;  ";
        response = dbServer.handleCommand(response);
        assert(response.contains("[OK]"));
    }

    @Test
    public void testUseNonExistingDB(){
        String randomName = generateRandomName();
        String response = "  Use   " + randomName + "            ;  ";
        response = dbServer.handleCommand(response);
        assert(response.contains("[ERROR]"));
    }

    @Test
    public void testMissingSemiColon(){
        String randomName = generateRandomName();
        String response = "  Use   " + randomName + "             ";
        response = dbServer.handleCommand(response);
        assert(response.contains("[ERROR]"));
    }
}
