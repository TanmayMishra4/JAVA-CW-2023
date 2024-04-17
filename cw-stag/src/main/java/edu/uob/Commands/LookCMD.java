package edu.uob.Commands;

import edu.uob.GameEngine;
import edu.uob.GameEntity;
import edu.uob.Model.Location;
import edu.uob.Model.Player;

import java.util.List;

public class LookCMD extends GenericCMD{
    public LookCMD(Player player, List<String> commands, GameEngine gameEngine) throws Exception{
        super(commands, gameEngine, player);
        if(commands.size() > 1) throw new Exception("Illegal Look Command, length greater than one");
        execute(player);
    }

    private void execute(Player player) { // TODO see the correct format for displaying these
        Location currentLocation =  player.getCurrentLocation();
        StringBuilder sb =  new StringBuilder();
        sb.append("You are in a ").append(currentLocation.getDescription()).append(". You can see:\n");
        for(GameEntity artefact : currentLocation.getArtefacts().values()){
            sb.append(artefact.getName()).append(" [ ").append(artefact.getDescription()).append(" ]\n");
        }
        for(GameEntity artefact : currentLocation.getFurniture().values()){
            sb.append(artefact.getName()).append(" [ ").append(artefact.getDescription()).append(" ]\n");
        }
        for(GameEntity artefact : currentLocation.getGameCharacters().values()){
            sb.append(artefact.getName()).append(" [ ").append(artefact.getDescription()).append(" ]\n");
        }
        sb.append("You can access from here:\n");
        for(String name : currentLocation.getToLocations().keySet()){
            sb.append(name).append("\n");
        }
        sb.deleteCharAt(sb.length()-1);
        setResponse(sb.toString());
    }
}
