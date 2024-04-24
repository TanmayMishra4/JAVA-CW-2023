package edu.uob;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Paths;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

public class TestGetCMD {

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
    void testGetCMDSimple(){
        String response = "get coin";
        response = sendCommandToServer(response);
        assertTrue(response.contains("coin"), "Failed attempt to use 'look' command");
        response = sendCommandToServer("inv");
        assertTrue(response.contains("coin"), "Failed attempt to use 'look' command");
        response = sendCommandToServer("look");
        assertFalse(response.contains("coin"), "Failed");
    }

    @Test
    void testGetCMDMultipleItems(){
        String response = "get coin and axe";
        response = sendCommandToServer(response);
        assertTrue(response.contains("not allowed"), "Failed attempt to use 'look' command");
        response = "get coin, axe";
        response = sendCommandToServer(response);
        assertTrue(response.contains("not allowed"), "Failed attempt to use 'look' command");
    }

    @Test
    void testGetCMDEmpty(){
        String response = "get ";
        response = sendCommandToServer(response);
        assertTrue(response.contains("No entity"), "Failed attempt to use 'look' command");
    }

    @Test
    void testGetCMDInvalidEntity(){
        String response = "get computer";
        response = sendCommandToServer(response);
        assertTrue(response.contains("No entity"), "Failed attempt to use 'look' command");
    }

    @Test
    void testGetCMDInvalidEntity2(){
        String response = "get key from forest";
        response = sendCommandToServer(response);
        assertTrue(response.contains("not allowed"), "Failed attempt to use 'look' command");
    }
}
