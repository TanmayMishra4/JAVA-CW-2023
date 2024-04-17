package edu.uob.Commands;

import edu.uob.GameEngine;
import edu.uob.Model.Player;

import java.util.List;

public class GenericCMD {
    private String response;
    private Player player;
    private List<String> commands;

    private GameEngine gameEngine;

    public void setResponse(String response) {
        this.response = response;
    }

    public GameEngine getGameEngine() {
        return gameEngine;
    }

    public Player getPlayer() {
        return player;
    }

    GenericCMD(List<String> commands, GameEngine gameEngine, Player player){
        this.commands = commands;
        response = "";
        this.gameEngine = gameEngine;
        this.player = player;
    }

    public String getResponse(){
        return this.response;
    }
}
