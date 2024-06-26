package edu.uob;

import edu.uob.Model.*;

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

    public Location getLocation(String name) throws Exception{
        if(!locations.containsKey(name)) throw new Exception("Location with name" +name+ " does not exist");
        return locations.get(name);
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
        startingLocation.addGameCharacter(player);
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
            currentLocation.removeCharacter(player);
            player.setCurrentLocation(locations.get(destinationName));
            locations.get(destinationName).addGameCharacter(player);
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
        if(!currentLocation.hasArtefact(artefact)) throw new Exception("Artefact "+ artefact.getName() +" not present at players location");
        currentLocation.removeArtefact(artefact);
        player.addToInventory(artefact);
    }

    public void dropArtefact(Player player, GameEntity artefact) throws Exception{
        Location currentLocation  = player.getCurrentLocation();
        player.dropArtefact(artefact);
        currentLocation.addArtefact((Artefact) artefact);
        artefact.setLocation(currentLocation);
    }

    public HashMap<String, HashSet<GameAction>> getActions() {
        return this.actions;
    }

    public void performAction(Player player, GameAction action, HashSet<GameEntity> entitySet) throws Exception{
        HashSet<GameEntity> actionSubjects = action.getSubjects();
        if(!checkAllEntitiesInCMD(actionSubjects, entitySet)) throw new Exception("Could not match command");
        HashSet<GameEntity> consumedEntities = action.getConsumed();
        HashSet<GameEntity> producedEntities = action.getProduced();
        checkEntityAvbl(producedEntities, consumedEntities, player);
        checkSubjectsAvbl(actionSubjects, player);
        for(GameEntity entity : entitySet){
            player.performAction(action, entity);
        }
        consumeEntities(consumedEntities, player);
        produceEntities(producedEntities, player);
    }

    private void checkSubjectsAvbl(HashSet<GameEntity> actionSubjects, Player player) throws Exception{
        HashSet<GameEntity> avblItems = player.getAvailableSubjects();
        for(GameEntity subjectEntity : actionSubjects){
            if(!avblItems.contains(subjectEntity)) throw new Exception("Subject not avbl");
        }
    }

    private void checkEntityAvbl(HashSet<GameEntity> producedEntities, HashSet<GameEntity> consumedEntities, Player player) throws Exception {
        for(Player otherPlayer : players.values()){
            if(otherPlayer.getName().equals(player.getName())) continue;
            if(otherPlayer.hasEntities(producedEntities) || otherPlayer.hasEntities(consumedEntities)){
                throw new Exception("Consumed or produced entity in another player's inv");
            }
        }
    }

    private boolean checkAllEntitiesInCMD(HashSet<GameEntity> actionSubjects, HashSet<GameEntity> entitySet) {
        for(GameEntity entity : entitySet){
            if(!actionSubjects.contains(entity)) return false;
        }
        return true;
    }

    private void consumeEntities(HashSet<GameEntity> consumedEntities, Player player) throws Exception {
        for(GameEntity entity : consumedEntities){
            if(!allEntityMap.containsKey(entity.getName())) throw new Exception("entity does not exist");
            if(entity.getEntityType() == EntityType.LOCATION){
                player.consumeLocation(entity);
            }
            else if(entity.getEntityType() == EntityType.HEALTH){
                player.decreaseHealth();
            }
            else{
                Location currentLocation = entity.getLocation();
                Location storeRoom = locations.get("storeroom");
                currentLocation.removeEntity(entity);
                player.removeFromInventory(entity);
                storeRoom.addEntity(entity);
                entity.setLocation(storeRoom);
            }
        }
    }

    private void produceEntities(HashSet<GameEntity> consumedEntities, Player player) throws Exception {
        for(GameEntity entity : consumedEntities){
            if(!allEntityMap.containsKey(entity.getName())) throw new Exception("entity does not exist");
            if(entity.getEntityType() == EntityType.LOCATION)
                player.produceLocation(entity);
            else if(entity.getEntityType() == EntityType.HEALTH)
                player.increaseHealth();
            else{
                Location entityLocation = entity.getLocation();
                Location currentLocation = player.getCurrentLocation();
                entityLocation.removeEntity(entity);
                currentLocation.addEntity(entity);
                entity.setLocation(currentLocation);
            }
        }
    }

    public boolean hasDestinationName(String name) {
        return locations.containsKey(name);
    }
}
