package edu.uob.Commands;

import edu.uob.GameEngine;
import edu.uob.GameEntity;
import edu.uob.Model.Player;

import java.util.List;

public class InventoryCMD extends GenericCMD{
    public InventoryCMD(Player player, List<String> commands, GameEngine gameEngine) throws Exception{
        super(commands, gameEngine, player);
        //TODO check if length check for commands is necessary or not
        if(commands.size() != 1) throw new Exception("Invalid inventory commands");
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
