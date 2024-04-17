package edu.uob.Model;

import edu.uob.CommandParser;
import edu.uob.GameAction;
import edu.uob.GameEntity;
import edu.uob.Utils.ClassContainer;

import java.util.HashSet;

public class Player extends GameCharacter {
    private Location currentLocation;
    private HashSet<GameEntity> inventory;
    private int health;
    private Location storeRoom;

    public Player(String name, String description, Location currentLocation, Location storeRoom) {
        super(name, description, EntityType.PLAYER);
        health = 3;
        this.storeRoom = storeRoom;
        inventory = new HashSet<>();
        this.currentLocation = currentLocation;// TODO figure out the initial location;
    }

    @Override
    public String toString(){
        return this.getName();
    }

    public Location getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(Location currentLocation) {
        this.currentLocation = currentLocation;
    }

    public HashSet<GameEntity> getInventory() {
        return inventory;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public boolean equals(Player another){
        return this.getName().equals(another.getName());
    }

    public void addToInventory(GameEntity artefact) {
        inventory.add(artefact);
    }

    public void dropArtefact(GameEntity artefact) throws Exception{
        if(inventory.contains(artefact)){
            inventory.remove(artefact);
        }
        else throw new Exception("Player does not have "+artefact.getName());
    }

    public HashSet<GameEntity> getAvailableSubjects() {
        HashSet<GameEntity> avblSubjects = currentLocation.getAvailableSubjects();
        avblSubjects.addAll(inventory);
        return avblSubjects;
    }

    public void performAction(GameAction action, GameEntity entity) throws Exception{
        HashSet<GameEntity> consumed = action.getConsumed();
        HashSet<GameEntity> produced = action.getProduced();
        consumeEntities(consumed);
        produceEntities(produced);
        CommandParser cmdParser = ClassContainer.getInstance().getCmdParser();
        cmdParser.setResponse(action.getNarration());
    }

    private void produceEntities(HashSet<GameEntity> entities) {
        for(GameEntity entity : entities){
            // TODO implement these
            if(entity.getEntityType() == EntityType.LOCATION) produceLocation(entity);
            else if(entity.getEntityType() == EntityType.ARTEFACT) produceEntity(entity);
            else if(entity.getEntityType() == EntityType.FURNITURE) produceEntity(entity);
            else if(entity.getEntityType() == EntityType.CHARACTER) produceEntity(entity);
            else if(entity.getEntityType() == EntityType.HEALTH) increaseHealth();
        }
    }

    private void increaseHealth() {
        this.health = Math.max(3, this.health+1);
    }

    private void produceEntity(GameEntity entity) {
        Location entityLocation = entity.getLocation();
        entityLocation.removeEntity(entity);
        currentLocation.addEntity(entity);
    }

    private void produceLocation(GameEntity entity) {
        currentLocation.addPathTo((Location) entity);
    }

    private void consumeEntities(HashSet<GameEntity> entities) throws Exception{
        for(GameEntity entity : entities){
            // TODO implement these
            if(entity.getEntityType() == EntityType.LOCATION) consumeLocation(entity);
            else if(entity.getEntityType() == EntityType.ARTEFACT) consumeEntity(entity);
            else if(entity.getEntityType() == EntityType.FURNITURE) consumeEntity(entity);
            else if(entity.getEntityType() == EntityType.CHARACTER) consumeEntity(entity);
            else if(entity.getEntityType() == EntityType.HEALTH) decreaseHealth();
        }
    }

    private void consumeEntity(GameEntity entity) {
        Location currentLocation = entity.getLocation();
        currentLocation.removeEntity(entity);
        storeRoom.addEntity(entity);
    }

    private void decreaseHealth() throws Exception{
        if(this.health == 1) {
            playerDead();
            throw new Exception("you died and lost all of your items, you must return to the start of the game");
        }
        this.health--;
    }

    private void playerDead() {// TODO to be implemented
        health = 3;
        inventory.forEach((e) -> currentLocation.addArtefact((Artefact) e));
        inventory.clear();
        ClassContainer classContainer = ClassContainer.getInstance();
        currentLocation = classContainer.getGameEngine().getStartingLocation();
        classContainer.getCmdParser().setResponse("you died and lost all of your items, you must return to the start of the game");
    }

    private void consumeLocation(GameEntity entity) {
        currentLocation.removePathTo((Location) entity);
    }
}
