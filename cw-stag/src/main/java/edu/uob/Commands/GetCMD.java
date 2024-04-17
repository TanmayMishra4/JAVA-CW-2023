package edu.uob.Commands;

import edu.uob.GameEngine;
import edu.uob.Model.Player;

import java.util.List;

public class GetCMD extends GenericCMD{
    public GetCMD(Player player, List<String> commands, GameEngine gameEngine) {
        super(commands, gameEngine, player);
    }
}
