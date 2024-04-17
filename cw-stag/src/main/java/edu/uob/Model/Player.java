package edu.uob.Model;

import edu.uob.GameEntity;

import java.util.HashSet;

public class Player extends GameEntity {
    private Location currentLocation;
    private HashSet<GameEntity> inventory;
    private int health;

    public Player(String name, String description, Location currentLocation) {
        super(name, description, EntityType.PLAYER);
        health = 3;
        inventory = new HashSet<>();
        this.currentLocation = currentLocation; // TODO figure out the initial location;
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
}
