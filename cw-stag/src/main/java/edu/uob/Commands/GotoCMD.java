package edu.uob.Commands;

import edu.uob.GameEngine;
import edu.uob.Model.Player;

import java.util.List;

public class GotoCMD extends GenericCMD{
    public GotoCMD(Player player, List<String> commands, GameEngine gameEngine) throws Exception{
        super(commands, gameEngine, player);
        if(commands.size() != 2) throw new Exception("Goto CMD has more than or less than 2 args");
        String destinationName = commands.get(1);
        execute(destinationName);
    }

    private void execute(String destinationName) throws Exception{
        getGameEngine().movePlayer(getPlayer(), destinationName);
        setResponse("You are now in "+destinationName);
    }
}
