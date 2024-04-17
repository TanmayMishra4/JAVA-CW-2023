package edu.uob.Commands;

import edu.uob.GameEngine;
import edu.uob.Model.Player;

import java.util.List;

public class HealthCMD extends GenericCMD{
    public HealthCMD(List<String> commands, GameEngine gameEngine, Player player) {
        super(commands, gameEngine, player);
        setResponse("Your health level is "+player.getHealth());
    }
}
