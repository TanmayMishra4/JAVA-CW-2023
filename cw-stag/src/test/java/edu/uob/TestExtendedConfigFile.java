package edu.uob;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestTemplate;

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
        assertTrue(response.contains("3"));
    }

    @Test
    void testDrinkCommand2(){
        String response = "drink";
        response = sendCommandToServer(response);
        assertFalse(response.contains("drink the potion"), "Failed attempt to use 'look' command");
    }

    @Test
    void testDrinkCommand3(){ // TODO extraneous entities
        String response = sendCommandToServer("get axe");
        response = sendCommandToServer("drink potion with axe");
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
    void testUnlockShortenedCMD2(){
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

    @Test
    void testChopCMD(){
        String response = "get axe";
        response = sendCommandToServer(response);
        sendCommandToServer("goto forest");
        response = sendCommandToServer("chop tree");
        assertTrue(response.contains("cut down the tree"));
        response = sendCommandToServer("look");
        assertFalse(response.contains("tree"));
        assertTrue(response.contains("log"));
    }

    @Test
    void testDeadPlayer(){
        String response = "goto forest";
        response = sendCommandToServer(response);
        response = sendCommandToServer("get key");
        response = sendCommandToServer("goto cabin");
        response = sendCommandToServer("get potion");
        response = sendCommandToServer("open trapdoor");
        response = sendCommandToServer("inv");
        assertFalse(response.contains("key"));
        response = sendCommandToServer("goto cellar");
        response = sendCommandToServer("fight elf");
        response = sendCommandToServer("fight elf");
        response = sendCommandToServer("health");
        assertTrue(response.contains("1"));
        response = sendCommandToServer("inv");
        assertTrue(response.contains("potion"));
        response = sendCommandToServer("fight elf");
        assertTrue(response.contains("died"));
        response = sendCommandToServer("look");
        assertTrue(response.contains("cabin"));
        response = sendCommandToServer("health");
        assertTrue(response.contains("3"));
        response = sendCommandToServer("inv");
        assertFalse(response.contains("key"));
        response = sendCommandToServer("goto cellar");
        response = sendCommandToServer("look");
        assertTrue(response.contains("potion"));
    }

    @Test
    void testInceaseHealthOfPlayer(){
        String response = "goto forest";
        response = sendCommandToServer(response);
        response = sendCommandToServer("get key");
        response = sendCommandToServer("goto cabin");
        response = sendCommandToServer("open trapdoor");
        response = sendCommandToServer("goto cellar");
        response = sendCommandToServer("fight elf");
        response = sendCommandToServer("fight elf");
        response = sendCommandToServer("health");
        assertTrue(response.contains("1"));
        response = sendCommandToServer("goto cabin");
        response = sendCommandToServer("drink potion");
        assertTrue(response.contains("drink the potion"));
        response = sendCommandToServer("health");
        assertTrue(response.contains("2"));
    }

    @Test
    void testDoubleDrinkPotion(){
        String response = "goto forest";
        response = sendCommandToServer(response);
        response = sendCommandToServer("get key");
        response = sendCommandToServer("goto cabin");
        response = sendCommandToServer("open trapdoor");
        response = sendCommandToServer("goto cellar");
        response = sendCommandToServer("fight elf");
        response = sendCommandToServer("fight elf");
        response = sendCommandToServer("health");
        assertTrue(response.contains("1"));
        response = sendCommandToServer("goto cabin");
        response = sendCommandToServer("drink potion");
        assertTrue(response.contains("drink the potion"));
        response = sendCommandToServer("health");
        assertTrue(response.contains("2"));
        response = sendCommandToServer("look");
        assertFalse(response.contains("potion"));
        response = sendCommandToServer("drink potion");
        assertFalse(response.contains("drink the potion"));
        response = sendCommandToServer("health");
        assertTrue(response.contains("2"));
    }

    @Test
    void testIncreaseHealth(){
        String response = "drink potion";
        response = sendCommandToServer(response);
        response = sendCommandToServer("health");
        assertTrue(response.contains("3"));
    }

    @Test
    void testConsumedArtefactsKey(){
        String response = "goto forest";
        response = sendCommandToServer(response);
        response = sendCommandToServer("get key");
        response = sendCommandToServer("goto cabin");
        response = sendCommandToServer("unlock with key");
        response = sendCommandToServer("inv");
        assertFalse(response.contains("key"));
    }
}
