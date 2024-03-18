package edu.uob;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Paths;

import static java.io.File.*;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class JoinTest {
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

    public void cleanFolder(String name){
        File file = new File(Paths.get("databases"+ separatorChar+name).toAbsolutePath().toString());
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
    public void testJoin(){
        dbServer.handleCommand("CREATE DATABASE markbook" + ";");
        dbServer.handleCommand("use markbook"  + ";");
        dbServer.handleCommand("CREATE TABLE marks" + "(name, mark, pass);");
        dbServer.handleCommand("INSERT INTO marks" + " VALUES ('Simon', 65, TRUE);"); // test for insert into table when columns not present
        dbServer.handleCommand("INSERT INTO marks" + " VALUES ('Sion', 55, TRUE);");
        dbServer.handleCommand("INSERT INTO marks" + " VALUES ('Rob', 35, FALSE);");
        dbServer.handleCommand("INSERT INTO marks" + " VALUES ('Chris', 20, FALSE);");
        dbServer.handleCommand("CREATE TABLE coursework" + "(task, submission);");
        dbServer.handleCommand("INSERT INTO coursework" + " VALUES ('OXO', 2);"); // test for insert into table when columns not present
        dbServer.handleCommand("INSERT INTO coursework" + " VALUES ('DB', 0);");
        dbServer.handleCommand("INSERT INTO coursework" + " VALUES ('OXO', 3);");
        dbServer.handleCommand("INSERT INTO coursework" + " VALUES ('STAG', 1);");
        String response = "JOIN coursework AND marks ON submission AND id;";
        response = dbServer.handleCommand(response);
        assert(response.contains("[OK]"));
        String[] lines = response.split("\n");
        assert(lines.length == 6);
        assert(lines[1].equals("id\tcoursework.task\tmarks.name\tmarks.mark\tmarks.pass"));
        assert(lines[2].equals("0\tOXO\tSimon\t65\tTRUE"));
        assert(lines[3].equals("1\tDB\tSion\t55\tTRUE"));
        assert(lines[4].equals("2\tOXO\tRob\t35\tFALSE"));
    }

    @Test
    public void testJoinNonExistentTable(){
        dbServer.handleCommand("CREATE DATABASE markbook" + ";");
        dbServer.handleCommand("use markbook"  + ";");
        dbServer.handleCommand("CREATE TABLE marks" + "(name, mark, pass);");
        dbServer.handleCommand("INSERT INTO marks" + " VALUES ('Simon', 65, TRUE);"); // test for insert into table when columns not present
        dbServer.handleCommand("INSERT INTO marks" + " VALUES ('Sion', 55, TRUE);");
        dbServer.handleCommand("INSERT INTO marks" + " VALUES ('Rob', 35, FALSE);");
        dbServer.handleCommand("INSERT INTO marks" + " VALUES ('Chris', 20, FALSE);");
        dbServer.handleCommand("CREATE TABLE coursework" + "(task, submission);");
        dbServer.handleCommand("INSERT INTO coursework" + " VALUES ('OXO', 2);"); // test for insert into table when columns not present
        dbServer.handleCommand("INSERT INTO coursework" + " VALUES ('DB', 0);");
        dbServer.handleCommand("INSERT INTO coursework" + " VALUES ('OXO', 3);");
        dbServer.handleCommand("INSERT INTO coursework" + " VALUES ('STAG', 1);");
        String response = "JOIN coursework AND aknc ON submission AND id;";
        response = dbServer.handleCommand(response);
        assert(response.contains("[ERROR]"));
    }

    @Test
    public void testJoinOnNonExistentColumns(){
        dbServer.handleCommand("CREATE DATABASE markbook" + ";");
        dbServer.handleCommand("use markbook"  + ";");
        dbServer.handleCommand("CREATE TABLE marks" + "(name, mark, pass);");
        dbServer.handleCommand("INSERT INTO marks" + " VALUES ('Simon', 65, TRUE);"); // test for insert into table when columns not present
        dbServer.handleCommand("INSERT INTO marks" + " VALUES ('Sion', 55, TRUE);");
        dbServer.handleCommand("INSERT INTO marks" + " VALUES ('Rob', 35, FALSE);");
        dbServer.handleCommand("INSERT INTO marks" + " VALUES ('Chris', 20, FALSE);");
        dbServer.handleCommand("CREATE TABLE coursework" + "(task, submission);");
        dbServer.handleCommand("INSERT INTO coursework" + " VALUES ('OXO', 2);"); // test for insert into table when columns not present
        dbServer.handleCommand("INSERT INTO coursework" + " VALUES ('DB', 0);");
        dbServer.handleCommand("INSERT INTO coursework" + " VALUES ('OXO', 3);");
        dbServer.handleCommand("INSERT INTO coursework" + " VALUES ('STAG', 1);");
        String response = "JOIN coursework AND marks ON ajsbcdk AND id;";
        response = dbServer.handleCommand(response);
        assert(response.contains("[ERROR]"));
    }

    @Test
    public void testSelfJoin(){
        dbServer.handleCommand("CREATE DATABASE markbook" + ";");
        dbServer.handleCommand("use markbook"  + ";");
        dbServer.handleCommand("CREATE TABLE marks" + "(name, mark, pass);");
        dbServer.handleCommand("INSERT INTO marks" + " VALUES ('Simon', 65, TRUE);"); // test for insert into table when columns not present
        dbServer.handleCommand("INSERT INTO marks" + " VALUES ('Sion', 55, TRUE);");
        dbServer.handleCommand("INSERT INTO marks" + " VALUES ('Rob', 35, FALSE);");
        dbServer.handleCommand("INSERT INTO marks" + " VALUES ('Chris', 20, FALSE);");
        dbServer.handleCommand("CREATE TABLE coursework" + "(task, submission);");
        dbServer.handleCommand("INSERT INTO coursework" + " VALUES ('OXO', 2);"); // test for insert into table when columns not present
        dbServer.handleCommand("INSERT INTO coursework" + " VALUES ('DB', 0);");
        dbServer.handleCommand("INSERT INTO coursework" + " VALUES ('OXO', 3);");
        dbServer.handleCommand("INSERT INTO coursework" + " VALUES ('STAG', 1);");
        String response = "JOIN marks AND marks ON id AND id;";
        response = dbServer.handleCommand(response);
        assert(response.contains("[OK]"));
        String[] lines = response.split("\n");
        assert(lines.length == 6);
        assert(lines[1].equals("id\tmarks.name\tmarks.mark\tmarks.pass\tmarks.name\tmarks.mark\tmarks.pass"));
        assert(lines[2].equals("0\tSimon\t65\tTRUE\tSimon\t65\tTRUE"));
        assert(lines[3].equals("1\tSion\t55\tTRUE\tSion\t55\tTRUE"));
        assert(lines[4].equals("2\tRob\t35\tFALSE\tRob\t35\tFALSE"));
    }

    @Test
    public void testJoinDifferentCaseColNames(){
        dbServer.handleCommand("CREATE DATABASE markbook" + ";");
        dbServer.handleCommand("use markbook"  + ";");
        dbServer.handleCommand("CREATE TABLE marks" + "(name, mark, pass);");
        dbServer.handleCommand("INSERT INTO marks" + " VALUES ('Simon', 65, TRUE);"); // test for insert into table when columns not present
        dbServer.handleCommand("INSERT INTO marks" + " VALUES ('Sion', 55, TRUE);");
        dbServer.handleCommand("INSERT INTO marks" + " VALUES ('Rob', 35, FALSE);");
        dbServer.handleCommand("INSERT INTO marks" + " VALUES ('Chris', 20, FALSE);");
        dbServer.handleCommand("CREATE TABLE coursework" + "(task, submission);");
        dbServer.handleCommand("INSERT INTO coursework" + " VALUES ('OXO', 2);"); // test for insert into table when columns not present
        dbServer.handleCommand("INSERT INTO coursework" + " VALUES ('DB', 0);");
        dbServer.handleCommand("INSERT INTO coursework" + " VALUES ('OXO', 3);");
        dbServer.handleCommand("INSERT INTO coursework" + " VALUES ('STAG', 1);");
        String response = "JOIN coursework AND marks ON SUbmissION AND ID;";
        response = dbServer.handleCommand(response);
        assert(response.contains("[OK]"));
        String[] lines = response.split("\n");
        assert(lines.length == 6);
        assert(lines[1].equals("id\tcoursework.task\tmarks.name\tmarks.mark\tmarks.pass"));
        assert(lines[2].equals("0\tOXO\tSimon\t65\tTRUE"));
        assert(lines[3].equals("1\tDB\tSion\t55\tTRUE"));
        assert(lines[4].equals("2\tOXO\tRob\t35\tFALSE"));
    }
}
