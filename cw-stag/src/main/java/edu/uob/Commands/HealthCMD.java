package edu.uob.Commands;

import edu.uob.CommandParser;
import edu.uob.GameEngine;
import edu.uob.Model.Player;
import edu.uob.Utils.ClassContainer;

import java.util.List;

public class HealthCMD extends GenericCMD{
    // TODO check if action words can be part of basic commands as decorators
    public HealthCMD(List<String> commands, GameEngine gameEngine, Player player) throws Exception{
        super(commands, gameEngine, player);
        CommandParser cmdParser = ClassContainer.getInstance().getCmdParser();
        for(String name : commands){
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
