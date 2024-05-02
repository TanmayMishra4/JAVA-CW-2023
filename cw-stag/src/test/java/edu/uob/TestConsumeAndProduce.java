package edu.uob;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Paths;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

public class TestConsumeAndProduce {
    GameServer server;

    @BeforeEach
    void setup() {
        File entitiesFile = Paths.get("config" + File.separator + "extended-entities4.dot").toAbsolutePath().toFile();
        File actionsFile = Paths.get("config" + File.separator + "extended-actions4.xml").toAbsolutePath().toFile();
        server = new GameServer(entitiesFile, actionsFile);
    }

    String sendCommandToServer(String command) {
        // Try to send a command to the server - this call will timeout if it takes too long (in case the server enters an infinite loop)
        return assertTimeoutPreemptively(Duration.ofMillis(1000), () -> { return server.handleCommand(command);},
                "Server took too long to respond (probably stuck in an infinite loop)");
    }

    @Test
    void testConsumeEntityNotWithAnotherPerson(){
        String response = "simon: goto forest";
        response = sendCommandToServer(response);
        response = sendCommandToServer("simon: get key");
        response = sendCommandToServer("simon: goto cabin");
        response = sendCommandToServer("simon: open trapdoor");
        response = sendCommandToServer("simon: goto cellar");
        response = sendCommandToServer("simon: fight elf");
        assertTrue(response.contains("attack the elf"));
        response = sendCommandToServer("simon: goto cabin");
        response = sendCommandToServer("simon: look");
        assertFalse(response.contains("potion"));
    }

    @Test
    void testConsumeEntityWithAnotherPerson(){
        String response = "simon: goto forest";
        response = sendCommandToServer(response);
        response = sendCommandToServer("simon: get key");
        response = sendCommandToServer("luke: get potion");
        response = sendCommandToServer("simon: goto cabin");
        response = sendCommandToServer("simon: open trapdoor");
        response = sendCommandToServer("simon: goto cellar");
        response = sendCommandToServer("simon: fight elf");
        assertFalse(response.contains("attack the elf"));
        response = sendCommandToServer("simon: goto cabin");
        response = sendCommandToServer("simon: look");
        assertFalse(response.contains("potion"));
    }
}
