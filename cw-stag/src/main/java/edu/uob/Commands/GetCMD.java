package edu.uob.Commands;

import edu.uob.Utils.CommandParser;
import edu.uob.GameEngine;
import edu.uob.Model.GameEntity;
import edu.uob.Model.EntityType;
import edu.uob.Model.Player;
import edu.uob.Utils.ClassContainer;
import edu.uob.Utils.UtilityClass;

import java.util.List;

public class GetCMD extends GenericCMD{
    // TODO deal with decorative commands in simple commands

    public GetCMD(Player player, List<String> commands, GameEngine gameEngine) throws Exception {
        super(commands, gameEngine, player);
        GameEntity artefact = null;
        StringBuilder result = new StringBuilder();
        CommandParser cmdParser = ClassContainer.getInstance().getCmdParser();
        for(String token : commands){// TODO check if multiple artefacts check in get should be done or not
            if(gameEngine.hasEntity(token) && (artefact == null || artefact.getName().equalsIgnoreCase(token))) {
                artefact = gameEngine.getEntityByName(token);
                if(artefact.getEntityType() != EntityType.ARTEFACT) {
                    throw new Exception("Cannot pick up " + token);
                }
            }
            else if(cmdParser.isAction(token) || UtilityClass.checkIfNormalActionWord(token, "get"))
                throw new Exception("Action words not allowed in get cmd");
            else if(gameEngine.hasEntity(token) && artefact != null && !artefact.getName().equalsIgnoreCase(token))
                throw new Exception("Composite commands not allowed");
        }
        if(artefact != null) gameEngine.pickArtefact(player, artefact);
        else throw new Exception("No entity specified to pick up");
        result.append("You picked up a ").append(artefact != null ? artefact.getName() : "");
        setResponse(result.toString());
    }
}
