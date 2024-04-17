package edu.uob.Utils;

import edu.uob.CommandParser;
import edu.uob.GameEngine;

public class ClassContainer {
    private static final ClassContainer instance = new ClassContainer();
    private static CommandParser cmdParser;
    private static GameEngine gameEngine;

    public void setCommandParser(CommandParser cmdParser){
        ClassContainer.cmdParser = cmdParser;
    }

    public CommandParser getCmdParser(){
        return ClassContainer.cmdParser;
    }
    private ClassContainer(){}

    public GameEngine getGameEngine(){
        return ClassContainer.gameEngine;
    }

    public void setGameEngine(GameEngine gameEngine){
        ClassContainer.gameEngine =  gameEngine;
    }

    public static ClassContainer getInstance(){
        return instance;
    }
}
