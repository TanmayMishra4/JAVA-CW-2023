package edu.uob.Commands;

import edu.uob.Utils.CommandParser;
import edu.uob.GameEngine;
import edu.uob.Model.GameEntity;
import edu.uob.Model.EntityType;
import edu.uob.Model.Player;
import edu.uob.Utils.ClassContainer;
import edu.uob.Utils.UtilityClass;

import java.util.List;

public class DropCMD extends GenericCMD{
    public DropCMD(Player player, List<String> commands, GameEngine gameEngine) throws Exception {
        super(commands, gameEngine, player);
        GameEntity artefact = null;
        StringBuilder result = new StringBuilder();
        CommandParser cmdParser = ClassContainer.getInstance().getCmdParser();
        for(String token : commands){
            if(gameEngine.hasEntity(token) && artefact == null) {
                artefact = gameEngine.getEntityByName(token);
                if(artefact.getEntityType() != EntityType.ARTEFACT) {
                    artefact = null;
                    throw new Exception("Cannot drop " + token);
                }
            }
            else if(cmdParser.isAction(token) || UtilityClass.checkIfNormalActionWord(token, "drop"))
                throw new Exception("Action words not allowed in drop cmd");
            else if(gameEngine.hasEntity(token) && artefact != null && !artefact.getName().equalsIgnoreCase(token))
                throw new Exception("Multiple entities not allowed in drop command");
        }
        if(artefact != null) gameEngine.dropArtefact(player, artefact);
        else throw new Exception("No entity specified to be dropped");
        result.append("You dropped a ").append(artefact != null ? artefact.getName() : "");
        setResponse(result.toString());
    }
}
