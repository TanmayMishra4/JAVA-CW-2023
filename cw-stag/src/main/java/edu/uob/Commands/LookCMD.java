package edu.uob.Commands;

import edu.uob.Utils.CommandParser;
import edu.uob.GameEngine;
import edu.uob.Model.GameEntity;
import edu.uob.Model.Location;
import edu.uob.Model.Player;
import edu.uob.Utils.ClassContainer;

import java.util.List;

public class LookCMD extends GenericCMD{
    public LookCMD(Player player, List<String> commands, GameEngine gameEngine) throws Exception{
        super(commands, gameEngine, player);
        CommandParser cmdParser = ClassContainer.getInstance().getCmdParser();
        for(String word : commands){
            if(cmdParser.isAction(word)) throw new Exception("Another action word not allowed in command");
            else if(cmdParser.isEntity(word)) throw new Exception("Entity not allowed in look command");
        }
        execute(player);
    }

    private void execute(Player player) { // TODO see the correct format for displaying these
        Location currentLocation =  player.getCurrentLocation();
        StringBuilder sb =  new StringBuilder();
        sb.append("You are in a ").append(currentLocation.getDescription()).append(". You can see:\n");
        for(GameEntity artefact : currentLocation.getArtefacts().values()){
            sb.append(artefact.getName()).append(" [ ").append(artefact.getDescription()).append(" ]\n");
        }
        for(GameEntity furniture1 : currentLocation.getFurniture().values()){
            sb.append(furniture1.getName()).append(" [ ").append(furniture1.getDescription()).append(" ]\n");
        }
        for(GameEntity gameCharacter : currentLocation.getGameCharacters().values()){
            if(gameCharacter.getName().equals(player.getName())) continue;
            sb.append(gameCharacter.getName()).append(" [ ").append(gameCharacter.getDescription()).append(" ]\n");
        }
        sb.append("You can access from here:\n");
        for(String name : currentLocation.getToLocations().keySet()){
            sb.append(name).append("\n");
        }
        sb.deleteCharAt(sb.length()-1);
        setResponse(sb.toString());
    }
}
