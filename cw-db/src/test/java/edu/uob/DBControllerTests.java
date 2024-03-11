package edu.uob;

import edu.uob.AllExceptions.DBExceptions.DBException;
import edu.uob.Controller.DBController;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class DBControllerTests extends ExampleDBTests{
    // TODO populate dummy databases for testing
    private DBController dbController;
    @BeforeEach
    public void setup(){
        dbController = new DBController();
    }
    @AfterEach
    public void afterTest(){
        deleteFolder();
    }

    @Test
    public void testSetActiveDB(){
        createFolder();
        assertDoesNotThrow(()->dbController.setActiveDB(dbName));
    }
    @Test
    public void testSetActiveDBFail(){
        assertThrows(DBException.class, ()->dbController.setActiveDB("randomDBDoeskajsndfn"));
    }
    @Test
    public void testDeleteDB(){
        createFolder();
        assertDoesNotThrow(()->dbController.dropDB("testDB"));
    }
    @Test
    public void testDeleteDBFail(){
        assertThrows(DBException.class, ()->dbController.dropDB(dbName));
    }

    @Test
    public void testCreateDB(){
        assertDoesNotThrow(()->dbController.createDB(dbName));
    }

    // TODO test case for duplicate column names

    @Test
    public void testCreateDBFail(){
        createFolder();
        assertThrows(DBException.class, ()->dbController.createDB(dbName));
    }

    @Test
    public void testDropTable(){
        assertDoesNotThrow(()->dbController.setActiveDB("testDB"));
        assertDoesNotThrow(()->dbController.dropTable("people"));
    }

    @Test
    public void testDropTableFail(){
        assertDoesNotThrow(()->dbController.setActiveDB("testDB"));
        assertThrows(DBException.class, ()->dbController.dropTable("kansd"));
    }

    @Test
    public void testCreateTable(){
        assertDoesNotThrow(()->dbController.setActiveDB("testDB"));
        assertDoesNotThrow(()->dbController.createTable("testTable"));
    }

    @Test
    public void testCreateTableWithAttrb(){
        assertDoesNotThrow(()->dbController.setActiveDB("testDB"));
        assertDoesNotThrow(()->dbController.createTable("testTable", Arrays.asList("name", "age", "Uni")));
    }
}
