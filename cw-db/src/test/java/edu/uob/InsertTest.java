package edu.uob;

import edu.uob.Model.NULLObject;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Paths;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class InsertTest {
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

    @Test
    public void testInsert(){
        String randomName = generateRandomName();
        String response = "CREATE DATABASE "+randomName+";";
        dbServer.handleCommand(response);
        response = "use "+randomName+";";
        dbServer.handleCommand(response);
        String randomTableName = generateRandomName();
        response = "CREATE TABLE "+randomTableName+" (name, mark, pass, age, other);";
        dbServer.handleCommand(response);
        String name = generateRandomName();
        double mark = 86.23;
        boolean pass = true;
        int age = 21;
        response = "Insert into "+randomTableName+" values ('"+name+"',"+mark+","+pass+","+age+","+"NULL);";
        response = dbServer.handleCommand(response);
        assert(response.contains("[OK]"));
        response = "Select  * from "+randomTableName+" ;";
        response = dbServer.handleCommand(response);
        assert(response.contains(name+"\t86.23\tTRUE\t21"));
    }

    @Test
    public void testInsertCaseInsensitive(){
        String randomName = generateRandomName();
        String response = "CREATE DATABASE "+randomName+";";
        dbServer.handleCommand(response);
        response = "use "+randomName+";";
        dbServer.handleCommand(response);
        String randomTableName = generateRandomName();
        response = "CREATE TABLE "+randomTableName+" (name, mark, pass, age, other);";
        dbServer.handleCommand(response);
        String name = generateRandomName();
        double mark = 86.23;
        boolean pass = true;
        int age = 21;
        response = "Insert into "+randomTableName.substring(0, 4).toUpperCase()+randomTableName.substring(4)+" values ('"+name+"',"+mark+","+pass+","+age+","+"NULL);";
        response = dbServer.handleCommand(response);
        assert(response.contains("[OK]"));
        response = "Select  * from "+randomTableName+" ;";
        response = dbServer.handleCommand(response);
        assert(response.contains(name+"\t86.23\tTRUE\t21"));
    }

    @Test
    public void testInsertMultiple(){
        String randomName = generateRandomName();
        String response = "CREATE DATABASE "+randomName+";";
        dbServer.handleCommand(response);
        response = "use "+randomName+";";
        dbServer.handleCommand(response);
        String randomTableName = generateRandomName();
        response = "CREATE TABLE "+randomTableName+" (name, mark, pass, age, other);";
        dbServer.handleCommand(response);
        String name = generateRandomName();
        double mark = 86.23;
        boolean pass = true;
        int age = 21;
        response = "Insert into "+randomTableName.substring(0, 4).toUpperCase()+randomTableName.substring(4)+" values ('"+name+"',"+mark+","+pass+","+age+","+"NULL);";
        String command = response;
        response = dbServer.handleCommand(response);
        assert(response.contains("[OK]"));
        response = dbServer.handleCommand(command);
        assert(response.contains("[OK]"));
        response = dbServer.handleCommand(command);
        assert(response.contains("[OK]"));
        response = dbServer.handleCommand(command);
        assert(response.contains("[OK]"));
        response = "Select  * from "+randomTableName+" ;";
        response = dbServer.handleCommand(response);
        assert(response.contains(name+"\t86.23\tTRUE\t21"));
    }

    @Test
    public void testInsertWithoutColumns(){
        String randomName = generateRandomName();
        String response = "CREATE DATABASE "+randomName+";";
        dbServer.handleCommand(response);
        response = "use "+randomName+";";
        dbServer.handleCommand(response);
        String randomTableName = generateRandomName();
        response = "CREATE TABLE "+randomTableName+";";
        dbServer.handleCommand(response);
        String name = generateRandomName();
        response = "Insert into "+randomTableName.toUpperCase()+" values (30, 'sjksfi', TRUE);";
        response = dbServer.handleCommand(response);
        assert(response.contains("[ERROR]"));
    }

    @Test
    public void testInsertWithSpacesInString(){ // TODO take column name from table column list
        String randomName = generateRandomName();
        String response = "CREATE DATABASE "+randomName+";";
        dbServer.handleCommand(response);
        response = "use "+randomName+";";
        dbServer.handleCommand(response);
        String randomTableName = generateRandomName();
        response = "CREATE TABLE "+randomTableName+"(mark, name, pass);";
        dbServer.handleCommand(response);
        String name = generateRandomName();
        response = "Insert into "+randomTableName.toUpperCase()+" values (30, 'sjksfi akjdnk ', TRUE);";
        response = dbServer.handleCommand(response);
        assert(response.contains("[OK]"));
        response = dbServer.handleCommand("Select NaMe from "+randomTableName+";");
        String[] lines = response.split("\n");
        assert(lines.length == 3);

    }
}
