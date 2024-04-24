package edu.uob.Model;

import edu.uob.GameEngine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class Location extends GameEntity {

    HashMap<String, Location> toLocations;
    HashMap<String, GameCharacter> gameCharacters;
    HashMap<String, Artefact> artefacts;
    HashMap<String, Furniture> furniture;

    public HashMap<String, Location> getToLocations() {
        return toLocations;
    }

    public void addToLocation(Location location) {
        this.toLocations.put(location.getName(), location);
    }

    public void removeEntity(GameEntity entity){
        switch(entity.getEntityType()){
            case ARTEFACT -> artefacts.remove(entity.getName());
            case FURNITURE -> furniture.remove(entity.getName());
            case CHARACTER -> gameCharacters.remove(entity.getName());
        }
    }

    public HashMap<String, GameCharacter> getGameCharacters() {
        return gameCharacters;
    }

    public void addGameCharacter(GameCharacter gameCharacter) {
        this.gameCharacters.put(gameCharacter.getName(), gameCharacter);
    }

    public HashMap<String, Artefact> getArtefacts() {
        return artefacts;
    }

    public void addArtefact(Artefact artefact) {
        this.artefacts.put(artefact.getName(), artefact);
    }

    public HashMap<String, Furniture> getFurniture() {
        return furniture;
    }

    public void addFurniture(Furniture furniture) {
        this.furniture.put(furniture.getName(), furniture);
    }

    public Location(String name, String description) {
        super(name, description, EntityType.LOCATION);
        toLocations = new HashMap<>();
        furniture = new HashMap<>();
        artefacts = new HashMap<>();
        gameCharacters = new HashMap<>();
    }

    public boolean containsDestination(String destinationName) {
        return toLocations.containsKey(destinationName);
    }

    public boolean hasArtefact(GameEntity artefact) {
        return artefacts.containsKey(artefact.getName());
    }

    public void removeArtefact(GameEntity artefact) {
        artefacts.remove(artefact.getName());
    }

    public HashSet<GameEntity> getAvailableSubjects() {
        HashSet<GameEntity> avblSubjects = new HashSet<>();
        avblSubjects.add(this);
        avblSubjects.addAll(furniture.values());
        avblSubjects.addAll(artefacts.values());
        avblSubjects.addAll(toLocations.values());
        avblSubjects.addAll(gameCharacters.values());
        return avblSubjects;
    }

    public void addEntity(GameEntity entity) {
        switch(entity.getEntityType()){
            case ARTEFACT -> artefacts.put(entity.getName(), (Artefact) entity);
            case FURNITURE -> furniture.put(entity.getName(), (Furniture) entity);
            case CHARACTER -> gameCharacters.put(entity.getName(), (GameCharacter)entity);
        }
    }

    public void removePathTo(Location entity) {
        if(!toLocations.containsKey(entity.getName())) return;
        toLocations.remove(entity.getName());
    }

    public void addPathTo(Location entity) {
        if(toLocations.containsKey(entity.getName())) return;
        toLocations.put(entity.getName(), entity);
    }

    public void removeCharacter(Player player) {
        gameCharacters.remove(player.getName());
    }

    public static class LocationBuilder {
        private String name, description;
        private ArrayList<Location> toLocations;
        private ArrayList<GameCharacter> gameGameCharacters;
        private ArrayList<Artefact> artefacts;
        private ArrayList<Furniture> furniture;
        private GameEngine gameEngine;


        public LocationBuilder(String name, String description, GameEngine gameEngine){
            this.name = name;
            this.description = description;
            toLocations = new ArrayList<>();
            artefacts = new ArrayList<>();
            furniture = new ArrayList<>();
            this.gameEngine = gameEngine;
            gameGameCharacters = new ArrayList<>();
        }

        public LocationBuilder setArtefacts(ArrayList<Artefact> artefacts){
            this.artefacts = artefacts;
            return this;
        }

        public LocationBuilder setToLocations(ArrayList<Location> toLocations) {
            this.toLocations = toLocations;
            return this;
        }

        public LocationBuilder setGameCharacters(ArrayList<GameCharacter> gameGameCharacters) {
            this.gameGameCharacters = gameGameCharacters;
            return this;
        }

        public LocationBuilder setFurniture(ArrayList<Furniture> furniture) {
            this.furniture = furniture;
            return this;
        }

        public Location build(){
            Location location = new Location(name, description);
            gameEngine.addEntity(location);
            toLocations.forEach((e) -> {location.addToLocation(e);gameEngine.addEntity(e);});
            furniture.forEach((e) -> {location.addFurniture(e);gameEngine.addEntity(e);});
            artefacts.forEach((e) -> {location.addArtefact(e);gameEngine.addEntity(e);});
            gameGameCharacters.forEach((e) -> {location.addGameCharacter(e);gameEngine.addEntity(e);});
            return location;
        }
    }
}
