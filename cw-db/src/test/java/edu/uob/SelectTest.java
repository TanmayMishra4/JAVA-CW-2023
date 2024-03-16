package edu.uob;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Paths;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class SelectTest {
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
    public void testSelect(){
        String randomName = generateRandomName();
        String response = "CREATE DATABASE "+randomName+";";
        dbServer.handleCommand(response);
        String randomTableName = generateRandomName();
        dbServer.handleCommand("use "+randomName+";");
        response = "CREATE TABLE "+randomTableName + "(name, mark, age, pass, other);";
        dbServer.handleCommand(response);
        response = "Insert into "+randomTableName+" values ('John', 86.23, 12, TRUE, NULL);";
        dbServer.handleCommand(response);
        response = "Insert into "+randomTableName+" values ('ALice', 50.23, 80, FALSE, NULL);";
        dbServer.handleCommand(response);
        response = "Insert into "+randomTableName+" values ('Bob', 12.23, 125, FALSE, NULL);";
        dbServer.handleCommand(response);
        response = "Insert into "+randomTableName+" values ('WhoCares', 90.23, 84, TRUE, NULL);";
        dbServer.handleCommand(response);
        response = "Select * from "+randomTableName+";";
        response = dbServer.handleCommand(response);
        assert(response.contains("[OK]"));
        String[] lines = response.split("\n");
        assert(lines.length == 6);
        assert(lines[2].equals("0\tJohn\t86.23\t12\tTRUE\t"));
        assert(lines[5].equals("3\tWhoCares\t90.23\t84\tTRUE\t"));
    }

    @Test
    public void testSelectCaseInsensitive(){
        String randomName = generateRandomName();
        String response = "CREATE DATABASE "+randomName+";";
        dbServer.handleCommand(response);
        String randomTableName = generateRandomName();
        dbServer.handleCommand("use "+randomName+";");
        response = "CREATE TABLE "+randomTableName.toLowerCase() + "(name, mark, age, pass, other);";
        dbServer.handleCommand(response);
        response = "Insert into "+randomTableName+" values ('John', 86.23, 12, TRUE, NULL);";
        dbServer.handleCommand(response);
        response = "Insert into "+randomTableName+" values ('ALice', 50.23, 80, FALSE, NULL);";
        dbServer.handleCommand(response);
        response = "Insert into "+randomTableName+" values ('Bob', 12.23, 125, FALSE, NULL);";
        dbServer.handleCommand(response);
        response = "Insert into "+randomTableName+" values ('WhoCares', 90.23, 84, TRUE, NULL);";
        dbServer.handleCommand(response);
        response = "Select * from "+randomTableName.toUpperCase()+";";
        response = dbServer.handleCommand(response);
        assert(response.contains("[OK]"));
        String[] lines = response.split("\n");
        assert(lines.length == 6);
        assert(lines[2].equals("0\tJohn\t86.23\t12\tTRUE\t"));
        assert(lines[5].equals("3\tWhoCares\t90.23\t84\tTRUE\t"));
    }

    @Test
    public void testSelectNonExistingTable(){
        String randomName = generateRandomName();
        String response = "CREATE DATABASE "+randomName+";";
        dbServer.handleCommand(response);
        String randomTableName = generateRandomName();
        dbServer.handleCommand("use "+randomName+";");
        response = "Select * from "+randomTableName.toUpperCase()+";";
        response = dbServer.handleCommand(response);
        assert(response.contains("[ERROR]"));
    }

    @Test
    public void testSelectOrderOfColumns(){
        String randomName = generateRandomName();
        String response = "CREATE DATABASE "+randomName+";";
        dbServer.handleCommand(response);
        String randomTableName = generateRandomName();
        dbServer.handleCommand("use "+randomName+";");
        response = "CREATE TABLE "+randomTableName.toLowerCase() + "(name, mark, age, pass, other);";
        dbServer.handleCommand(response);
        response = "Insert into "+randomTableName+" values ('John', 86.23, 12, TRUE, NULL);";
        dbServer.handleCommand(response);
        response = "Insert into "+randomTableName+" values ('ALice', 50.23, 80, FALSE, NULL);";
        dbServer.handleCommand(response);
        response = "Insert into "+randomTableName+" values ('Bob', 12.23, 125, FALSE, NULL);";
        dbServer.handleCommand(response);
        response = "Insert into "+randomTableName+" values ('WhoCares', 90.23, 84, TRUE, NULL);";
        dbServer.handleCommand(response);
        response = "Select pass, mark, age from "+randomTableName.toUpperCase()+";";
        response = dbServer.handleCommand(response);
        assert(response.contains("[OK]"));
        String[] lines = response.split("\n");
        assert(lines.length == 6);
        assert(lines[2].equals("TRUE\t86.23\t12"));
        assert(lines[4].equals("FALSE\t12.23\t125"));
    }

    @Test
    public void testSelectConditional(){
        String randomName = generateRandomName();
        String response = "CREATE DATABASE "+randomName+";";
        dbServer.handleCommand(response);
        String randomTableName = generateRandomName();
        dbServer.handleCommand("use "+randomName+";");
        response = "CREATE TABLE "+randomTableName.toLowerCase() + "(name, mark, age, pass, other);";
        dbServer.handleCommand(response);
        response = "Insert into "+randomTableName+" values ('John', 86.23, 12, TRUE, NULL);";
        dbServer.handleCommand(response);
        response = "Insert into "+randomTableName+" values ('Alice', 50.23, 80, FALSE, NULL);";
        dbServer.handleCommand(response);
        response = "Insert into "+randomTableName+" values ('Bob', 12.23, 125, FALSE, NULL);";
        dbServer.handleCommand(response);
        response = "Insert into "+randomTableName+" values ('WhoCares', 90.23, 84, TRUE, NULL);";
        dbServer.handleCommand(response);
        response = "Select pass, mark, age from "+randomTableName.toUpperCase()+" where name like 'Alice';";
        response = dbServer.handleCommand(response);
        assert(response.contains("[OK]"));
        String[] lines = response.split("\n");
        assert(lines.length == 3);
        assert(lines[2].equals("FALSE\t50.23\t80"));
    }

    @Test
    public void testSelectConditionalImproperStringValue(){
        String randomName = generateRandomName();
        String response = "CREATE DATABASE "+randomName+";";
        dbServer.handleCommand(response);
        String randomTableName = generateRandomName();
        dbServer.handleCommand("use "+randomName+";");
        response = "CREATE TABLE "+randomTableName.toLowerCase() + "(name, mark, age, pass, other);";
        dbServer.handleCommand(response);
        response = "Insert into "+randomTableName+" values ('John', 86.23, 12, TRUE, NULL);";
        dbServer.handleCommand(response);
        response = "Insert into "+randomTableName+" values ('Alice', 50.23, 80, FALSE, NULL);";
        dbServer.handleCommand(response);
        response = "Insert into "+randomTableName+" values ('Bob', 12.23, 125, FALSE, NULL);";
        dbServer.handleCommand(response);
        response = "Insert into "+randomTableName+" values ('WhoCares', 90.23, 84, TRUE, NULL);";
        dbServer.handleCommand(response);
        response = "Select pass, mark, age from "+randomTableName.toUpperCase()+" where name like 'alice';";
        response = dbServer.handleCommand(response);
        assert(response.contains("[OK]"));
        String[] lines = response.split("\n");
        assert(lines.length == 2);
    }

    @Test
    public void testSelectConditionalNULL(){
        String randomName = generateRandomName();
        String response = "CREATE DATABASE "+randomName+";";
        dbServer.handleCommand(response);
        String randomTableName = generateRandomName();
        dbServer.handleCommand("use "+randomName+";");
        response = "CREATE TABLE "+randomTableName.toLowerCase() + "(name, mark, age, pass, other);";
        dbServer.handleCommand(response);
        response = "Insert into "+randomTableName+" values ('John', 86.23, 12, TRUE, NULL);";
        dbServer.handleCommand(response);
        response = "Insert into "+randomTableName+" values ('Alice', 50.23, 80, FALSE, NULL);";
        dbServer.handleCommand(response);
        response = "Insert into "+randomTableName+" values ('Bob', 12.23, 125, FALSE, NULL);";
        dbServer.handleCommand(response);
        response = "Insert into "+randomTableName+" values ('WhoCares', 90.23, 84, TRUE, NULL);";
        dbServer.handleCommand(response);
        response = "Select pass, mark, age, name from "+randomTableName.toUpperCase()+" where other == NULL;";
        response = dbServer.handleCommand(response);
        assert(response.contains("[OK]"));
    }

    @Test
    public void testSelectMultipleConditional(){
        String randomName = generateRandomName();
        String response = "CREATE DATABASE "+randomName+";";
        dbServer.handleCommand(response);
        String randomTableName = generateRandomName();
        dbServer.handleCommand("use "+randomName+";");
        response = "CREATE TABLE "+randomTableName.toLowerCase() + "(name, mark, age, pass, other);";
        dbServer.handleCommand(response);
        response = "Insert into "+randomTableName+" values ('John', 86.23, 12, TRUE, NULL);";
        dbServer.handleCommand(response);
        response = "Insert into "+randomTableName+" values ('Alice', 50.23, 80, FALSE, NULL);";
        dbServer.handleCommand(response);
        response = "Insert into "+randomTableName+" values ('Bob', 12.23, 125, FALSE, NULL);";
        dbServer.handleCommand(response);
        response = "Insert into "+randomTableName+" values ('WhoCares', 90.23, 84, TRUE, NULL);";
        dbServer.handleCommand(response);
        response = "Select pass, mark, age from "+randomTableName.toUpperCase()+" where pass == TRUE or (name like 'o');";
        response = dbServer.handleCommand(response);
        assert(response.contains("[OK]"));
        String[] lines = response.split("\n");
        assert(lines.length == 5);
        assert(lines[2].equals("TRUE\t86.23\t12"));
        assert(lines[3].equals("FALSE\t12.23\t125"));
        assert(lines[4].equals("TRUE\t90.23\t84"));
    }

    @Test
    public void testSelectMultipleConditionalFunnyBrackets(){ // TODO not working
        String randomName = generateRandomName();
        String response = "CREATE DATABASE "+randomName+";";
        dbServer.handleCommand(response);
        String randomTableName = generateRandomName();
        dbServer.handleCommand("use "+randomName+";");
        response = "CREATE TABLE "+randomTableName.toLowerCase() + "(name, mark, age, pass, other);";
        dbServer.handleCommand(response);
        response = "Insert into "+randomTableName+" values ('John', 86.23, 12, TRUE, NULL);";
        dbServer.handleCommand(response);
        response = "Insert into "+randomTableName+" values ('Alice', 50.23, 80, FALSE, NULL);";
        dbServer.handleCommand(response);
        response = "Insert into "+randomTableName+" values ('Bob', 12.23, 125, FALSE, NULL);";
        dbServer.handleCommand(response);
        response = "Insert into "+randomTableName+" values ('WhoCares', 90.23, 84, TRUE, NULL);";
        dbServer.handleCommand(response);
        response = "Select pass, mark, age from "+randomTableName.toUpperCase()+" where (pass == TRUE or (name like 'o'));";
        response = dbServer.handleCommand(response);
        assert(response.contains("[OK]"));
        String[] lines = response.split("\n");
        assert(lines.length == 5);
        assert(lines[2].equals("TRUE\t86.23\t12"));
        assert(lines[3].equals("FALSE\t12.23\t125"));
        assert(lines[4].equals("TRUE\t90.23\t84"));
        response = "Select pass, mark, age from "+randomTableName.toUpperCase()+" where (pass == TRUE) or (name like 'o');";
        response = dbServer.handleCommand(response);
        assert(response.contains("[OK]"));
        lines = response.split("\n");
        assert(lines.length == 5);
        assert(lines[2].equals("TRUE\t86.23\t12"));
        assert(lines[3].equals("FALSE\t12.23\t125"));
        assert(lines[4].equals("TRUE\t90.23\t84"));
        response = "Select pass, mark, age from "+randomTableName.toUpperCase()+" where ((pass == TRUE) or (name like 'o'));";
        response = dbServer.handleCommand(response);
        assert(response.contains("[OK]"));
        lines = response.split("\n");
        assert(lines.length == 5);
        assert(lines[2].equals("TRUE\t86.23\t12"));
        assert(lines[3].equals("FALSE\t12.23\t125"));
        assert(lines[4].equals("TRUE\t90.23\t84"));
        response = "Select pass, mark, age from "+randomTableName.toUpperCase()+" where pass == TRUE or name like 'o';";
        response = dbServer.handleCommand(response);
        assert(response.contains("[OK]"));
        lines = response.split("\n");
        assert(lines.length == 5);
        assert(lines[2].equals("TRUE\t86.23\t12"));
        assert(lines[3].equals("FALSE\t12.23\t125"));
        assert(lines[4].equals("TRUE\t90.23\t84"));

        response = "Select pass, mark, age from "+randomTableName.toUpperCase()+" where (pass == TRUE or name like 'o') and mark > 50;";
        response = dbServer.handleCommand(response);
        assert(response.contains("[OK]"));
        lines = response.split("\n");
        assert(lines.length == 4);
        assert(lines[2].equals("TRUE\t86.23\t12"));
        assert(lines[3].equals("TRUE\t90.23\t84"));

        response = "Select pass, mark, age from "+randomTableName.toUpperCase()+" where pass == TRUE or (name like 'o' and mark > 50);";
        response = dbServer.handleCommand(response);
        assert(response.contains("[OK]"));
        lines = response.split("\n");
        assert(lines.length == 4);
        assert(lines[2].equals("TRUE\t86.23\t12"));
        assert(lines[3].equals("TRUE\t90.23\t84"));
    }

    @Test
    public void testSelectMultipleConditionalWrongBrackets(){ // TODO not working
        String randomName = generateRandomName();
        String response = "CREATE DATABASE "+randomName+";";
        dbServer.handleCommand(response);
        String randomTableName = generateRandomName();
        dbServer.handleCommand("use "+randomName+";");
        response = "CREATE TABLE "+randomTableName.toLowerCase() + "(name, mark, age, pass, other);";
        dbServer.handleCommand(response);
        response = "Insert into "+randomTableName+" values ('John', 86.23, 12, TRUE, NULL);";
        dbServer.handleCommand(response);
        response = "Insert into "+randomTableName+" values ('Alice', 50.23, 80, FALSE, NULL);";
        dbServer.handleCommand(response);
        response = "Insert into "+randomTableName+" values ('Bob', 12.23, 125, FALSE, NULL);";
        dbServer.handleCommand(response);
        response = "Insert into "+randomTableName+" values ('WhoCares', 90.23, 84, TRUE, NULL);";
        dbServer.handleCommand(response);
        response = "Select pass, mark, age from "+randomTableName.toUpperCase()+" where pass == TRUE or (name like 'o'));";
        response = dbServer.handleCommand(response);
        assert(response.contains("[ERROR]"));

        response = "Select pass, mark, age from "+randomTableName.toUpperCase()+" where (pass == TRUE or (name like 'o');";
        response = dbServer.handleCommand(response);
        assert(response.contains("[ERROR]"));

        response = "Select pass, mark, age from "+randomTableName.toUpperCase()+" where ((pass == TRUE) or (name like 'o');";
        response = dbServer.handleCommand(response);
        assert(response.contains("[ERROR]"));

    }
}

