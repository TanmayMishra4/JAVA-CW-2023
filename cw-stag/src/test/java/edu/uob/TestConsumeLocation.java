package edu.uob;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Paths;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

public class TestConsumeLocation {
    GameServer server;

    @BeforeEach
    void setup() {
        File entitiesFile = Paths.get("config" + File.separator + "extended-entities2.dot").toAbsolutePath().toFile();
        File actionsFile = Paths.get("config" + File.separator + "extended-actions2.xml").toAbsolutePath().toFile();
        server = new GameServer(entitiesFile, actionsFile);
    }

    String sendCommandToServer(String command) {
        // Try to send a command to the server - this call will timeout if it takes too long (in case the server enters an infinite loop)
        final String finalCommand = "simon: "+command;
        return assertTimeoutPreemptively(Duration.ofMillis(1000), () -> { return server.handleCommand(finalCommand);},
                "Server took too long to respond (probably stuck in an infinite loop)");
    }

    @Test
    void testConsumeLocation(){
        String response = "goto forest";
        sendCommandToServer(response);
        sendCommandToServer("get key");
        sendCommandToServer("goto cabin");
        sendCommandToServer("open trapdoor");
        response = sendCommandToServer("look");
        assertFalse(response.contains("forest"));
    }

    @Test
    void testProduceLocation(){
        String response = "drink potion";
        sendCommandToServer(response);
        response = sendCommandToServer("look");
        assertTrue(response.contains("riverbank"));
    }
}
