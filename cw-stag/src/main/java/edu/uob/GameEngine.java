package edu.uob;

import edu.uob.Model.Location;
import edu.uob.Model.Player;

import java.util.HashMap;
import java.util.HashSet;

public class GameEngine {
    HashSet<Player> players;
    HashMap<String, GameEntity> allEntityMap;
    HashMap<String, Location> locations;
    HashMap<String, HashSet<GameAction>> actions;

    GameEngine(){
        players = new HashSet<>();
        locations = new HashMap<>();
        actions = new HashMap<>();
        allEntityMap = new HashMap<>();
    }

    public void addLocations(HashMap<String, Location> allLocations) {
        this.locations = allLocations;
    }

    public void addAction(GameAction gameAction) {
        for(String phrase : gameAction.getTriggers()){
            actions.putIfAbsent(phrase, new HashSet<>());
            actions.get(phrase).add(gameAction);
        }
    }

    public void addEntity(GameEntity entity) {
        allEntityMap.put(entity.getName(), entity);
    }

    public GameEntity getEntityByName(String entityName) throws Exception{
        if(allEntityMap.containsKey(entityName)) return allEntityMap.get(entityName);
        throw new Exception("entity not found in map");
    }
}
