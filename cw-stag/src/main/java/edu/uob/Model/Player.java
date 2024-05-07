package edu.uob.Model;

import edu.uob.Utils.CommandParser;
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
        CommandParser cmdParser = ClassContainer.getInstance().getCmdParser();
        cmdParser.setResponse(action.getNarration());
    }


    public void increaseHealth() {
        this.health = Math.min(3, this.health+1);
    }

    private void produceEntity(GameEntity entity) {
        Location entityLocation = entity.getLocation();
        entityLocation.removeEntity(entity);
        currentLocation.addEntity(entity);
    }

    public void produceLocation(GameEntity entity) {
        currentLocation.addPathTo((Location) entity);
    }

    public void removeFromInventory(GameEntity entity) {
        inventory.remove(entity);
    }

    public void decreaseHealth() throws Exception{
        if(this.health == 1) {
            playerDead();
            throw new Exception("you died and lost all of your items, you must return to the start of the game");
        }
        this.health--;
    }

    private void playerDead() throws Exception {// TODO to be implemented
        health = 3;
        inventory.forEach((e) -> currentLocation.addArtefact((Artefact) e));
        inventory.clear();
        ClassContainer classContainer = ClassContainer.getInstance();
        Location startingLocation = classContainer.getGameEngine().getStartingLocation();
        currentLocation.removeGameCharacter(this);
        classContainer.getCmdParser().setResponse("you died and lost all of your items, you must return to the start of the game");
        startingLocation.addGameCharacter(this);
        currentLocation = startingLocation;
    }

    public void consumeLocation(GameEntity entity) {
        currentLocation.removePathTo((Location) entity);
    }

    public boolean hasEntities(HashSet<GameEntity> producedEntities) {
        for(GameEntity entity : producedEntities){
            if(inventory.contains(entity)) return true;
        }
        return false;
    }
}
