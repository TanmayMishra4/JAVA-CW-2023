package edu.uob;

import edu.uob.Commands.*;
import edu.uob.Model.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandParser {

    private String command;
    private GameEngine gameEngine;
    private String response;

    public CommandParser(String command, GameEngine gameEngine) throws Exception{
        this.command = command;
        response = "";
        this.gameEngine = gameEngine;
        String[] separatedCommand = command.split(":");
        if(separatedCommand.length == 0) throw new Exception("Invalid command, does not include ':' character");
        String playerName = separatedCommand[0];
        String remainingCmd = separatedCommand[1].toLowerCase().trim();
        ArrayList<String> tokenizedCMD = tokenize(remainingCmd);
        executeCommand(playerName, tokenizedCMD);
    }

    private ArrayList<String> tokenize(String remainingCmd) {
        // TODO look for a more robust way to code this
        remainingCmd = remainingCmd.replaceAll("\\s+", " ");
        remainingCmd = remainingCmd.replaceAll("\\t", " ");
        return new ArrayList<>(Arrays.asList(remainingCmd.split(" ")));
    }

    private void executeCommand(String playerName, ArrayList<String> command) throws Exception{
        Player player = gameEngine.getPlayerByName(playerName);
        if(executeBasicCMD(player, command)){
            return;
        }
        executeAdvancedCMD(player, command);
    }

    private void executeAdvancedCMD(Player player, ArrayList<String> command) {
    }

    private boolean executeBasicCMD(Player player, ArrayList<String> commands) throws Exception{
        String firstCommand = commands.get(0);
        GenericCMD genericCMD;
        switch(firstCommand){
            case "look":
                genericCMD = new LookCMD(player, commands, gameEngine);
                break;
            case "goto":
                genericCMD = new GotoCMD(player, commands, gameEngine);
                break;
            case "get":
                genericCMD = new GetCMD(player, commands, gameEngine);
                break;
            case "inventory":
            case "inv":
                genericCMD = new InventoryCMD(player, commands, gameEngine);
                break;
            case "drop":
                genericCMD = new DropCMD(player, commands, gameEngine);
                break;
            default : return false;
        }
        response = genericCMD.getResponse();
        return true;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }
}
