package edu.uob.Commands;

import edu.uob.Utils.CommandParser;
import edu.uob.GameEngine;
import edu.uob.Model.Player;
import edu.uob.Utils.ClassContainer;

import java.util.List;

public class GotoCMD extends GenericCMD{
    public GotoCMD(Player player, List<String> commands, GameEngine gameEngine) throws Exception{
        super(commands, gameEngine, player);
        CommandParser cmdParser = ClassContainer.getInstance().getCmdParser();
        boolean destNameFound = false;
        String destName = "";
//        if(commands.size() != 2) throw new Exception("Goto CMD has more than or less than 2 args");
        for(String name : commands){
            if(cmdParser.isEntity(name)){
                if(gameEngine.hasDestinationName(name)){
                    if(destNameFound) throw new Exception("Two destination names not allowed in goto command");
                    destNameFound = true;
                    destName = name;
//                    execute(name);
//                    break;
                }
                else{
                    throw new Exception("Entity name other than location not allowed in goto command");
                }
            }
            if(cmdParser.isAction(name)){
                throw new Exception("Action not allowed in goto command");
            }
        }
        if(destNameFound) execute(destName);
        else throw new Exception("Destination name not found");
    }

    private void execute(String destinationName) throws Exception{
        getGameEngine().movePlayer(getPlayer(), destinationName);
        setResponse("You are now in "+destinationName);
    }
}
