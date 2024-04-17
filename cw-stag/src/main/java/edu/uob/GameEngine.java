package edu.uob;

import edu.uob.Model.Health;
import edu.uob.Model.Location;
import edu.uob.Model.Player;

import java.util.HashMap;
import java.util.HashSet;

public class GameEngine {
    HashMap<String, Player> players;
    Location startingLocation;
    HashMap<String, GameEntity> allEntityMap;
    HashMap<String, Location> locations;
    HashMap<String, HashSet<GameAction>> actions;

    GameEngine(){
        players = new HashMap<>();
        locations = new HashMap<>();
        actions = new HashMap<>();
        allEntityMap = new HashMap<>();
        allEntityMap.put("health", new Health());
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

    public Player getPlayerByName(String playerName) {
        if(players.containsKey(playerName)) return players.get(playerName);
        Player player = new Player(playerName, "PlayerName = "+playerName, startingLocation);
        addPlayer(player);
        return player;
    }

    public void addPlayer(Player player) {
        players.put(player.getName(), player);
    }

    public Location getStartingLocation() {
        return startingLocation;
    }

    public void setStartingLocation(Location startingLocation) {
        this.startingLocation = startingLocation;
    }

    public void movePlayer(Player player, String destinationName) throws Exception{
        Location currentLocation = player.getCurrentLocation();
        if(!locations.containsKey(destinationName)){
            throw new Exception("Location "+destinationName+" does not exist");
        }
        if(currentLocation.containsDestination(destinationName)){
            player.setCurrentLocation(locations.get(destinationName));
        }
    }
}
