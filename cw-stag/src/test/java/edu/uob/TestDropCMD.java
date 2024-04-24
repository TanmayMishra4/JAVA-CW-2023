package edu.uob;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Paths;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

public class TestDropCMD {
    GameServer server;

    @BeforeEach
    void setup() {
        File entitiesFile = Paths.get("config" + File.separator + "extended-entities.dot").toAbsolutePath().toFile();
        File actionsFile = Paths.get("config" + File.separator + "extended-actions.xml").toAbsolutePath().toFile();
        server = new GameServer(entitiesFile, actionsFile);
    }

    String sendCommandToServer(String command) {
        // Try to send a command to the server - this call will timeout if it takes too long (in case the server enters an infinite loop)
        final String finalCommand = "simon: "+command;
        return assertTimeoutPreemptively(Duration.ofMillis(1000), () -> { return server.handleCommand(finalCommand);},
                "Server took too long to respond (probably stuck in an infinite loop)");
    }

    @Test
    void testDropCMDSimple(){
        String response = "get coin";
        response = sendCommandToServer(response);
        response = sendCommandToServer("drop coin");
        assertTrue(response.contains("dropped"), "Failed");
        response = sendCommandToServer("inv");
        assertFalse(response.contains("coin"), "Failed");
        response = sendCommandToServer("look");
        assertTrue(response.contains("coin"), "Failed");
    }

    @Test
    void testDropCMDEmpty(){
        String response = "get coin";
        response = sendCommandToServer(response);
        response = sendCommandToServer("drop");
        assertTrue(response.contains("No entity"), "Failed");
        response = sendCommandToServer("inv");
        assertTrue(response.contains("coin"), "Failed");
        response = sendCommandToServer("look");
        assertFalse(response.contains("coin"), "Failed");
    }

    @Test
    void testDropCMDMultiple(){
        String response = "get coin";
        response = sendCommandToServer(response);
        response = sendCommandToServer("drop coin, axe");
        assertTrue(response.contains("not allowed"), "Failed");
        response = sendCommandToServer("inv");
        assertTrue(response.contains("coin"), "Failed");
        response = sendCommandToServer("look");
        assertFalse(response.contains("coin"), "Failed");

        response = sendCommandToServer("drop coin and axe");
        assertTrue(response.contains("not allowed"), "Failed");
        response = sendCommandToServer("inv");
        assertTrue(response.contains("coin"), "Failed");
        response = sendCommandToServer("look");
        assertFalse(response.contains("coin"), "Failed");
    }

    @Test
    void testDropCMDInvalid(){
        String response = "get coin";
        response = sendCommandToServer(response);
        response = sendCommandToServer("drop axe");
        assertTrue(response.contains("does not have"), "Failed");
        response = sendCommandToServer("inv");
        assertTrue(response.contains("coin"), "Failed");
        response = sendCommandToServer("look");
        assertFalse(response.contains("coin"), "Failed");
    }

    @Test
    void testDropCMDCaseInsensitive(){
        String response = "get COIN";
        response = sendCommandToServer(response);
        response = sendCommandToServer("dROP cOiN");
        assertTrue(response.contains("dropped"), "Failed");
        response = sendCommandToServer("inv");
        assertFalse(response.contains("coin"), "Failed");
        response = sendCommandToServer("look");
        assertTrue(response.contains("coin"), "Failed");

        response = "GEt COIN";
        response = sendCommandToServer(response);
        response = sendCommandToServer("DRoP COiN");
        assertTrue(response.contains("dropped"), "Failed");
        response = sendCommandToServer("inv");
        assertFalse(response.contains("coin"), "Failed");
        response = sendCommandToServer("look");
        assertTrue(response.contains("coin"), "Failed");
    }
}
