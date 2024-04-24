package edu.uob;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Paths;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class testGotoCMD {
    GameServer server;

    @BeforeEach
    void setup() {
        File entitiesFile = Paths.get("config" + File.separator + "basic-entities.dot").toAbsolutePath().toFile();
        File actionsFile = Paths.get("config" + File.separator + "basic-actions.xml").toAbsolutePath().toFile();
        server = new GameServer(entitiesFile, actionsFile);
    }

    String sendCommandToServer(String command) {
        // Try to send a command to the server - this call will timeout if it takes too long (in case the server enters an infinite loop)
        return assertTimeoutPreemptively(Duration.ofMillis(1000), () -> { return server.handleCommand(command);},
                "Server took too long to respond (probably stuck in an infinite loop)");
    }

    @Test
    void testGotoCMDSimple(){
        String response = "simon: goto forest";
        response = sendCommandToServer(response);
        assertTrue(response.contains("forest"), "Failed attempt to use 'look' command");
        response = sendCommandToServer("simon: look");
        assertTrue(response.contains("key"), "Failed attempt to use 'look' command");
        assertTrue(response.contains("tree"), "Failed attempt to use 'look' command");
        assertTrue(response.contains("cabin"), "Failed attempt to use 'look' command");
    }

    @Test
    void testGotoCMDSimpleEmpty(){
        String response = "simon: goto ";
        response = sendCommandToServer(response);
        assertTrue(response.contains("name not found"), "Failed attempt to use 'look' command");
    }

    @Test
    void testGotoCMDSimpleEmptyCaseInsenstitve(){
        String response = "simon: GOtO forest";
        response = sendCommandToServer(response);
        assertTrue(response.contains("forest"), "Failed attempt to use 'look' command");
        response = sendCommandToServer("simon: look");
        assertTrue(response.contains("key"), "Failed attempt to use 'look' command");
        assertTrue(response.contains("tree"), "Failed attempt to use 'look' command");
        assertTrue(response.contains("cabin"), "Failed attempt to use 'look' command");
    }

    @Test
    void testGotoCMDSimpleEmptyCaseInsenstitve2(){
        String response = "simon: GOtO FORest";
        response = sendCommandToServer(response);
        assertTrue(response.contains("forest"), "Failed attempt to use 'look' command");
        response = sendCommandToServer("simon: look");
        assertTrue(response.contains("key"), "Failed attempt to use 'look' command");
        assertTrue(response.contains("tree"), "Failed attempt to use 'look' command");
        assertTrue(response.contains("cabin"), "Failed attempt to use 'look' command");
    }

    @Test
    void testGotoCMDSimpleMultipleDest(){
        String response = "simon: GOtO FORest, cellar";
        response = sendCommandToServer(response);
        assertTrue(response.contains("Player cannot access cellar from cabin"), "Failed attempt to use 'look' command");
    }

    @Test
    void testGotoCMDSimpleReverseOrder(){
        String response = "simon: ForesT Goto";
        response = sendCommandToServer(response);
        assertTrue(response.contains("forest"), "Failed attempt to use 'look' command");
        response = sendCommandToServer("simon: look");
        assertTrue(response.contains("key"), "Failed attempt to use 'look' command");
        assertTrue(response.contains("tree"), "Failed attempt to use 'look' command");
        assertTrue(response.contains("cabin"), "Failed attempt to use 'look' command");
    }
}

