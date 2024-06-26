package edu.uob.Commands;

import edu.uob.Utils.CommandParser;
import edu.uob.GameEngine;
import edu.uob.Model.GameEntity;
import edu.uob.Model.Player;
import edu.uob.Utils.ClassContainer;
import edu.uob.Utils.UtilityClass;

import java.util.List;

public class InventoryCMD extends GenericCMD{
    public InventoryCMD(Player player, List<String> commands, GameEngine gameEngine) throws Exception{
        super(commands, gameEngine, player);
        CommandParser cmdParser = ClassContainer.getInstance().getCmdParser();
        for(String name : commands){
            if(cmdParser.isEntity(name)){
                throw new Exception("Entity name other than location not allowed in inv command");
            }
            if(cmdParser.isAction(name) || UtilityClass.checkIfNormalActionWord(name, "inv", "inventory")){
                throw new Exception("Action not allowed in inv command");
            }
        }
        execute(player);
    }

    private void execute(Player player) {
        StringBuilder sb = new StringBuilder();
        sb.append("You have these items in your inventory:\n");
        for(GameEntity artefact : player.getInventory()){
            sb.append(artefact.getName()).append(" [ ").append(artefact.getDescription()).append(" ]\n");
        }
        sb.deleteCharAt(sb.length()-1);
        setResponse(sb.toString());
    }
}
