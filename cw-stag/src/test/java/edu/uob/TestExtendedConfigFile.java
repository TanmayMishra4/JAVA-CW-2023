package edu.uob;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Paths;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

public class TestExtendedConfigFile {
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
    void testDrinkCommand(){
        String response = "drink potion";
        response = sendCommandToServer(response);
        assertTrue(response.contains("drink the potion"), "Failed attempt to use 'look' command");
        response = "look";
        response = sendCommandToServer(response);
        assertFalse(response.contains("potion"));
        response = "health";
        response = sendCommandToServer(response);
        assertTrue(response.contains("4"));
    }

    @Test
    void testDrinkCommand2(){
        String response = "drink";
        response = sendCommandToServer(response);
        assertFalse(response.contains("drink the potion"), "Failed attempt to use 'look' command");
    }

    @Test
    void testUnlockCMD(){
        String response = "goto forest";
        sendCommandToServer(response);
        sendCommandToServer("get key");
        sendCommandToServer("goto cabin");
        response = sendCommandToServer("unlock trapdoor with key");
        assertTrue(response.contains("unlock the door"), "Failed attempt to use 'look' command");
        response = sendCommandToServer("look");
        assertTrue(response.contains("trapdoor"));
    }

    @Test
    void testOpenCMD(){
        String response = "goto forest";
        sendCommandToServer(response);
        sendCommandToServer("get key");
        sendCommandToServer("goto cabin");
        response = sendCommandToServer("open trapdoor with key");
        assertTrue(response.contains("unlock the door"), "Failed attempt to use 'look' command");
        response = sendCommandToServer("look");
        assertTrue(response.contains("cellar"));
    }

    @Test
    void testUnlockShortenedCMD(){
        String response = "goto forest";
        sendCommandToServer(response);
        sendCommandToServer("get key");
        sendCommandToServer("goto cabin");
        response = sendCommandToServer("open trapdoor");
        assertTrue(response.contains("unlock the door"), "Failed attempt to use 'look' command");
        response = sendCommandToServer("look");
        assertTrue(response.contains("cellar"));
    }

    @Test
    void testUnlockShortenedCMD2(){ // TODO check if trapdoor should still be visible after unlock CMD or should be replaced with cellar
        String response = "goto forest";
        sendCommandToServer(response);
        sendCommandToServer("get key");
        sendCommandToServer("goto cabin");
        response = sendCommandToServer("unlock with key");
        assertTrue(response.contains("unlock the door"), "Failed attempt to use 'look' command");
        response = sendCommandToServer("look");
        assertTrue(response.contains("cellar"));
    }

    @Test
    void testUnlockCMDInvalid(){
        String response = "unlock trapdoor";
        response = sendCommandToServer(response);
        assertFalse(response.contains("unlock with key"));
        response = sendCommandToServer("look");
        assertFalse(response.contains("cellar"));
    }
}
