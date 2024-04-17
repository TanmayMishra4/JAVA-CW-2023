package edu.uob.Commands;

import edu.uob.GameEngine;
import edu.uob.GameEntity;
import edu.uob.Model.EntityType;
import edu.uob.Model.Player;

import java.util.List;

public class DropCMD extends GenericCMD{
    public DropCMD(Player player, List<String> commands, GameEngine gameEngine) throws Exception {
        super(commands, gameEngine, player);
        GameEntity artefact = null;
        StringBuilder result = new StringBuilder();
        for(String token : commands){// TODO check if multiple artefacts check in get should be done or not
            if(gameEngine.hasEntity(token) && artefact == null) {
                artefact = gameEngine.getEntityByName(token);
                if(artefact.getEntityType() != EntityType.ARTEFACT) {
                    artefact = null;
                    throw new Exception("Cannot drop " + token);
                }
                gameEngine.dropArtefact(player, artefact);
            }
        }
        result.append("You dropped a ").append(artefact != null ? artefact.getName() : "");
        setResponse(result.toString());
    }
}