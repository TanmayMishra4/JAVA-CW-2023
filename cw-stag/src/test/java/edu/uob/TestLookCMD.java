package edu.uob;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Paths;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestLookCMD {

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
    void testLookCMDSimple(){
        String response = "simon: look";
        response = sendCommandToServer(response);
        assertTrue(response.contains("cabin"), "Failed attempt to use 'look' command");
        assertTrue(response.contains("axe"), "Failed attempt to use 'look' command");
        assertTrue(response.contains("potion"), "Failed attempt to use 'look' command");
        assertTrue(response.contains("trapdoor"), "Failed attempt to use 'look' command");
    }

    @Test
    void testLookCMDSimpleCasInsensitive(){
        String response = "simon: LooK";
        response = sendCommandToServer(response);
        assertTrue(response.contains("cabin"), "Failed attempt to use 'look' command");
        assertTrue(response.contains("axe"), "Failed attempt to use 'look' command");
        assertTrue(response.contains("potion"), "Failed attempt to use 'look' command");
        assertTrue(response.contains("trapdoor"), "Failed attempt to use 'look' command");
    }

    @Test
    void testLookCMDSimpleDecorative(){
        String response = "simon: LooK here";
        response = sendCommandToServer(response);
        assertTrue(response.contains("cabin"), "Failed attempt to use 'look' command");
        assertTrue(response.contains("axe"), "Failed attempt to use 'look' command");
        assertTrue(response.contains("potion"), "Failed attempt to use 'look' command");
        assertTrue(response.contains("trapdoor"), "Failed attempt to use 'look' command");
    }

    @Test
    void testLookCMDWrongSpelling(){
        String response = "simon: LoK here";
        response = sendCommandToServer(response);
        assertTrue(response.contains("Proper Action not specified"), "Failed");
    }

    @Test
    void testLookCMDAdditionalEntities(){
        String response = "simon: look in forest";
        response = sendCommandToServer(response);
        assertTrue(response.contains("not allowed"), "Failed");
    }
}
