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

    @Test
    public void testDelete(){
        
    }
}
