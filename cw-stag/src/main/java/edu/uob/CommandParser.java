package edu.uob;

import edu.uob.Commands.*;
import edu.uob.Model.Player;
import edu.uob.Utils.ClassContainer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

public class CommandParser {

    private String command;
    private GameEngine gameEngine;
    private String response;
    private String playerName;

    private ArrayList<String> tokenizedCMD;

    public CommandParser(String command, GameEngine gameEngine) throws Exception{
        this.command = command;
        response = "";
        ClassContainer classContainer = ClassContainer.getInstance();
        classContainer.setGameEngine(gameEngine);
        this.gameEngine = gameEngine;
        String[] separatedCommand = command.split(":");
        if(separatedCommand.length == 0) throw new Exception("Invalid command, does not include ':' character");
        playerName = separatedCommand[0];
        String remainingCmd = separatedCommand[1].toLowerCase().trim();
        tokenizedCMD = tokenize(remainingCmd);
//        executeCommand(playerName, tokenizedCMD);
    }

    public String getPlayerName(){
        return playerName;
    }

    public ArrayList<String> getTokenizedCMD(){
        return tokenizedCMD;
    }

    private ArrayList<String> tokenize(String remainingCmd) {
        // TODO look for a more robust way to code this
        remainingCmd = remainingCmd.replaceAll("\\s+", " ");
        remainingCmd = remainingCmd.replaceAll("\\t", " ");
        return new ArrayList<>(Arrays.asList(remainingCmd.split(" ")));
    }

    public void executeCommand(String playerName, ArrayList<String> command) throws Exception{
        Player player = gameEngine.getPlayerByName(playerName);
        if(executeBasicCMD(player, command)){
            return;
        }
        else if(executeAdvancedCMD(player, command)){
            return;
        }
        else{
            response = "Invalid Command : "+command;
        }
    }

    private boolean executeAdvancedCMD(Player player, ArrayList<String> command) throws Exception{
        // TODO what to do when subjects avbl to player but the entity to be consumed is absent?
        HashSet<GameEntity> entitySet = extractEntities(player, command);
        GameAction action = extractActionOperation(player, command, entitySet);
        gameEngine.performAction(player, action, entitySet);
        return true;
    }

    private HashSet<GameEntity> extractEntities(Player player, ArrayList<String> command) throws Exception {
        HashSet<GameEntity> entities = new HashSet<>();
        for(String word : command){
            if(isEntity(word)){
                entities.add(gameEngine.getEntityByName(word));
            }
        }
        return entities;
    }

    public boolean isEntity(String word){
        return gameEngine.hasEntity(word);
    }

    public GameAction extractActionOperation(Player player, ArrayList<String> command, HashSet<GameEntity> subjects) throws Exception{
        GameAction action = null;
        for(String word : command){
            if(isAction(word)){
                if(action != null) throw new Exception("Multiple Actions in single command not allowed");
                HashSet<GameAction> associatedActions = gameEngine.getActions().get(word);
                action = matchCorrectAction(associatedActions, player, subjects);
            }
        }
        if(action == null) throw new Exception("Proper Action not specified");
        return action;
    }

    private GameAction matchCorrectAction(HashSet<GameAction> associatedActions, Player player, HashSet<GameEntity> subjects) throws Exception{
        for(GameAction action : associatedActions){
            HashSet<GameEntity> avblSubjects = player.getAvailableSubjects();
            if(isSuperSet(avblSubjects, subjects) && isSuperSet(avblSubjects, action.getSubjects())){
                return action;
            }
        }
        throw new Exception("No matching action found or cant satisfy conditions for the command");
    }

    private boolean isSuperSet(HashSet<GameEntity> avblSubjects, HashSet<GameEntity> subjects) {
        for(GameEntity subject : subjects){
            if(!avblSubjects.contains(subject)) return false;
        }
        return true;
    }

    public boolean isAction(String word) {
        return gameEngine.getActions().containsKey(word);
    }

    private boolean executeBasicCMD(Player player, ArrayList<String> commands) throws Exception{
        String firstCommand = commands.get(0);
        GenericCMD genericCMD;
        if(commands.contains("look")) genericCMD = new LookCMD(player, commands, gameEngine);
        else if(commands.contains("goto")) genericCMD = new GotoCMD(player, commands, gameEngine);
        else if(commands.contains("get")) genericCMD = new GetCMD(player, commands, gameEngine);
        else if(commands.contains("inventory") || commands.contains("inv")) genericCMD = new InventoryCMD(player, commands, gameEngine);
        else if(commands.contains("drop")) genericCMD = new DropCMD(player, commands, gameEngine);
        else if(commands.contains("health")) genericCMD = new HealthCMD(commands, gameEngine, player);
        else return false;

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
