package edu.uob.Commands;

import edu.uob.Utils.CommandParser;
import edu.uob.GameEngine;
import edu.uob.Model.Player;
import edu.uob.Utils.ClassContainer;
import edu.uob.Utils.UtilityClass;

import java.util.List;

public class HealthCMD extends GenericCMD{
     // TODO check open trapdoor to see if you need the key
    // TODO check if action words can be part of basic commands as decorators
    public HealthCMD(List<String> commands, GameEngine gameEngine, Player player) throws Exception{
        super(commands, gameEngine, player);
        CommandParser cmdParser = ClassContainer.getInstance().getCmdParser();
        boolean healthFound = false;
        for(String name : commands){
            if(name.equals("health") && !healthFound){
                healthFound = true;
                continue;
            }
            if(cmdParser.isEntity(name)){
                throw new Exception("Entity name not allowed in health command");
            }
            if(cmdParser.isAction(name)){
                throw new Exception("Action not allowed in health command");
            }
        }
        setResponse("Your health level is "+player.getHealth());
    }
}
