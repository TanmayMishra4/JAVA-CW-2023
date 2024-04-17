package edu.uob.Commands;

import edu.uob.GameEngine;
import edu.uob.Model.Player;

import java.util.List;

public class LookCMD extends GenericCMD{
    public LookCMD(Player player, List<String> commands, GameEngine gameEngine) throws Exception{
        super(commands, gameEngine, player);
        if(commands.size() > 1) throw new Exception("Illegal Look Command, length greater than one");
        execute();
    }

    private void execute() {

    }
}
