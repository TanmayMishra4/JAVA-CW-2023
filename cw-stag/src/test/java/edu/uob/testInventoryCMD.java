package edu.uob;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Paths;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

public class testInventoryCMD {

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
    void testLookInvCMD(){
        String response = "simon: inv";
        response = sendCommandToServer(response);
        assertTrue(response.contains("You have"), "Failed attempt to use 'look' command");
        response = "simon: Inventory";
        response = sendCommandToServer(response);
        assertTrue(response.contains("You have"), "Failed attempt to use 'look' command");
    }

    @Test
    void testLookInvCMDWrongSpell(){
        String response = "simon: inve";
        response = sendCommandToServer(response);
        assertFalse(response.contains("You have"), "Failed attempt to use 'look' command");
        response = "simon: Inventry";
        response = sendCommandToServer(response);
        assertFalse(response.contains("You have"), "Failed attempt to use 'look' command");
    }

    @Test
    void testLookInvCMDAfterPickingUp(){
        String response = "simon: get axe";
        sendCommandToServer(response);
        sendCommandToServer("simon: get coin");
        response = sendCommandToServer("simon: inv");
        assertTrue(response.contains("axe"), "Failed attempt to use 'look' command");
        assertTrue(response.contains("coin"), "Failed attempt to use 'look' command");
        response = sendCommandToServer("simon: Inventory");
        assertTrue(response.contains("axe"), "Failed attempt to use 'look' command");
        assertTrue(response.contains("coin"), "Failed attempt to use 'look' command");
        response = sendCommandToServer("simon: look");
        assertFalse(response.contains("coin"), "Failed");
        assertFalse(response.contains("axe"), "Failed");
    }

    @Test
    void testLookInvCMDAfterDropping(){
        String response = "simon: get axe";
        sendCommandToServer(response);
        sendCommandToServer("simon: get coin");
        sendCommandToServer("simon: drop coin");
        response = sendCommandToServer("look");
        assertFalse(response.contains("coin"), "Failed");
        assertFalse(response.contains("axe"), "Failed");
        response = sendCommandToServer("simon: inv");
        assertTrue(response.contains("axe"), "failed");
        assertFalse(response.contains("coin"), "failed");
        sendCommandToServer("simon: drop axe");
        response = sendCommandToServer("simon: look");
        assertTrue(response.contains("coin"), "Failed");
        assertTrue(response.contains("axe"), "Failed");
    }

    @Test
    void testLookInvCMDDecorative(){
        String response = "simon: get axe";
        sendCommandToServer(response);
        sendCommandToServer("simon: get coin");
        sendCommandToServer("simon: drop coin");
        response = sendCommandToServer("look");
        assertFalse(response.contains("coin"), "Failed");
        assertFalse(response.contains("axe"), "Failed");
        response = sendCommandToServer("simon: show me the inv");
        assertTrue(response.contains("axe"), "failed");
        assertFalse(response.contains("coin"), "failed");
        sendCommandToServer("simon: drop axe");
        response = sendCommandToServer("simon: look");
        assertTrue(response.contains("coin"), "Failed");
        assertTrue(response.contains("axe"), "Failed");
    }

    @Test
    void testLookInvCMDAdditionalEntities(){
        String response = "simon: get axe";
        sendCommandToServer(response);
        sendCommandToServer("simon: get coin");
        sendCommandToServer("simon: drop coin");
        response = sendCommandToServer("look");
        assertFalse(response.contains("coin"), "Failed");
        assertFalse(response.contains("axe"), "Failed");
        response = sendCommandToServer("simon: show me the inv in forest");
        assertTrue(response.contains("not allowed"), "failed");
    }
}
