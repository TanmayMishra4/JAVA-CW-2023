package edu.uob;

import edu.uob.Model.Artefact;
import edu.uob.Model.Health;
import edu.uob.Model.Location;
import edu.uob.Model.Player;

import java.util.HashMap;
import java.util.HashSet;

public class GameEngine {
    private HashMap<String, Player> players;
    private Location startingLocation;
    private HashMap<String, GameEntity> allEntityMap;
    private HashMap<String, Location> locations;
    private HashMap<String, HashSet<GameAction>> actions; // maps trigger word to actions

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
        Player player = new Player(playerName, "PlayerName = "+playerName, startingLocation, locations.get("storeroom"));
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
        else{
            throw new Exception("Player cannot access "+destinationName+" from "+currentLocation.getName());
        }
    }

    public boolean hasEntity(String token) {
        return allEntityMap.containsKey(token);
    }

    public void pickArtefact(Player player, GameEntity artefact) throws Exception{
        Location currentLocation = player.getCurrentLocation();
        if(!currentLocation.hasArtefact(artefact)) throw new Exception("Artefact"+ artefact.getName() +" not present at players location");
        currentLocation.removeArtefact(artefact);
        player.addToInventory(artefact);
    }

    public void dropArtefact(Player player, GameEntity artefact) throws Exception{
        Location currentLocation  = player.getCurrentLocation();
        player.dropArtefact(artefact);
        currentLocation.addArtefact((Artefact) artefact);
    }

    public HashMap<String, HashSet<GameAction>> getActions() {
        return this.actions;
    }

    public void performAction(Player player, GameAction action, HashSet<GameEntity> entitySet) {
        for(GameEntity entity : entitySet){
            player.performAction(action, entity);
        }
    }
}
