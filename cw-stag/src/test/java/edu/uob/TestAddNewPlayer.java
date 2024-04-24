package edu.uob;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Paths;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestAddNewPlayer {
    GameServer server;

    @BeforeEach
    void setup() {
        File entitiesFile = Paths.get("config" + File.separator + "extended-entities.dot").toAbsolutePath().toFile();
        File actionsFile = Paths.get("config" + File.separator + "extended-actions.xml").toAbsolutePath().toFile();
        server = new GameServer(entitiesFile, actionsFile);
    }

    String sendCommandToServer(String command) {
        // Try to send a command to the server - this call will timeout if it takes too long (in case the server enters an infinite loop)
        return assertTimeoutPreemptively(Duration.ofMillis(1000), () -> { return server.handleCommand(command);},
                "Server took too long to respond (probably stuck in an infinite loop)");
    }

    @Test
    void testAddPlayers(){
        String response = "simon: goto forest";
        response = sendCommandToServer(response);
        sendCommandToServer("john: goto forest");
        response = sendCommandToServer("alex: look");
        assertTrue(response.contains("cabin"), "Failed attempt to use 'look' command");
        assertTrue(response.contains("coin"), "Failed attempt to use 'look' command");
        assertTrue(response.contains("axe"), "Failed attempt to use 'look' command");
        response = sendCommandToServer("simon: look");
        assertTrue(response.contains("forest"), "Failed");
        assertTrue(response.contains("john"), "Failed");
        assertTrue(response.contains("key"), "Failed");
        assertTrue(response.contains("tree"), "Failed");
    }
}
