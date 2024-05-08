package edu.uob.Utils;

import edu.uob.Commands.*;
import edu.uob.GameEngine;
import edu.uob.Model.GameAction;
import edu.uob.Model.GameEntity;
import edu.uob.Model.Player;

import java.lang.reflect.Array;
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
        for(char a : playerName.toCharArray()) {
            if (Character.isLetter(a) || a == ' ' || a == '\'' || a == '-') continue;
            else throw new Exception("Invalid player name");
        }
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
        remainingCmd = remainingCmd.replaceAll("[^A-Za-z0-9]", " ");
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
        for(int i=0;i<command.size();i++){
            String word = command.get(i);
            int[] it = new int[]{0};
            HashSet<GameAction> associatedActions = isAction(i, command, it);
            if(associatedActions != null){
                GameAction newAction = matchCorrectAction(associatedActions, player, subjects);
                if(action != null && !action.equals(newAction)) throw new Exception("Multiple Actions in single command not allowed");
                action = newAction;
                i += (it[0] - 1);
            }
        }
        if(action == null) throw new Exception("Proper Action not specified");
        return action;
    }

    private GameAction matchCorrectAction(HashSet<GameAction> associatedActions, Player player, HashSet<GameEntity> subjects) throws Exception{
        GameAction resultAction = null;
        for(GameAction action : associatedActions){
            boolean flag = false;
            for(GameEntity subject : subjects){
                if(!action.getSubjects().contains(subject)){
                    flag = true;
                    break;
                }
            }
            if(flag) continue;
            HashSet<GameEntity> avblSubjects = player.getAvailableSubjects();
            if(isSuperSet(avblSubjects, subjects) && isSuperSet(avblSubjects, action.getSubjects())){
                if(resultAction == null) resultAction = action;
                else throw new Exception("Multiple Actions match the command");
            }
        }
        if(resultAction != null) return resultAction;
        throw new Exception("No matching action found or cant satisfy conditions for the command");
    }

    private boolean isSuperSet(HashSet<GameEntity> avblSubjects, HashSet<GameEntity> subjects) {
        for(GameEntity subject : subjects){
            if(!avblSubjects.contains(subject)) return false;
        }
        return true;
    }

    public HashSet<GameAction> isAction(int index, ArrayList<String> command, int[] it){
        String word = command.get(index);
        for(String actionWord : gameEngine.getActions().keySet()){
            if(actionWord.startsWith(word)){
                int len = actionWord.split(" ").length;
                String newWord = "";
                for(int i=index;i<index+len && i<command.size();i++){
                    newWord += (command.get(i) + " ");
                }
                newWord = newWord.substring(0, newWord.length()-1);
                it[0] = len;
                if(newWord.equals(actionWord)) return gameEngine.getActions().get(actionWord);
            }
        }
        return null;
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
