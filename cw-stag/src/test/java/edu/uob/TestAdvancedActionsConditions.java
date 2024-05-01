package edu.uob;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Paths;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

public class TestAdvancedActionsConditions {
    GameServer server;

    @BeforeEach
    void setup() {
        File entitiesFile = Paths.get("config" + File.separator + "extended-entities2.dot").toAbsolutePath().toFile();
        File actionsFile = Paths.get("config" + File.separator + "extended-actions2.xml").toAbsolutePath().toFile();
        server = new GameServer(entitiesFile, actionsFile);
    }

    String sendCommandToServer(String command) {
        // Try to send a command to the server - this call will timeout if it takes too long (in case the server enters an infinite loop)
        return assertTimeoutPreemptively(Duration.ofMillis(1000), () -> { return server.handleCommand(command);},
                "Server took too long to respond (probably stuck in an infinite loop)");
    }

    @Test
    void testSubjectConditions(){
        String response = "simon: get axe";
        response = sendCommandToServer(response);
        response = sendCommandToServer("simon: goto forest");
        response = sendCommandToServer("simon: chop tree");
        assertFalse(response.contains("cut down"), "Failed attempt to use 'look' command");
    }

    @Test
    void testSubjectConditions2(){
        String response = "simon: get axe";
        response = sendCommandToServer(response);
        sendCommandToServer("simon: get coin");
        response = sendCommandToServer("simon: goto forest");
        response = sendCommandToServer("simon: drop coin");
        response = sendCommandToServer("simon: chop tree");
        assertTrue(response.contains("cut down"), "Failed attempt to use 'look' command");
    }

    @Test
    void testSubjectConditionsMultiplePlayers(){
        String response = "simon: get axe";
        response = sendCommandToServer(response);
        sendCommandToServer("simon: get coin");
        response = sendCommandToServer("simon: goto forest");
        response = sendCommandToServer("simon: drop coin");
        response = sendCommandToServer("luke: goto forest");
        response = sendCommandToServer("luke: get coin");
        response = sendCommandToServer("simon: chop tree");
        assertFalse(response.contains("cut down"), "Failed attempt to use 'look' command");
    }

    @Test
    void testSubjectConditionsMultiplePlayers3(){
        String response = "simon: get axe";
        response = sendCommandToServer(response);
        sendCommandToServer("simon: goto forest");
        response = sendCommandToServer("simon: get key");
        response = sendCommandToServer("simon: goto cabin");
        response = sendCommandToServer("simon: open trapdoor");
        response = sendCommandToServer("simon: goto cellar");
        response = sendCommandToServer("simon: fight elf");
        assertFalse(response.contains("attack"), "Failed attempt to use 'look' command");
    }
}
