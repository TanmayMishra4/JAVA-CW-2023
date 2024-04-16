package edu.uob;

import edu.uob.Model.Location;
import edu.uob.Model.Player;

import java.util.HashMap;
import java.util.HashSet;

public class GameEngine {
    HashSet<Player> players;
    HashMap<String, Location> locations;
    HashMap<String, HashSet<GameAction>> actions;

    GameEngine(){
        players = new HashSet<>();
        locations = new HashMap<>();
        actions = new HashMap<>();
    }

    public void addLocations(HashMap<String, Location> allLocations) {
        this.locations = allLocations;
    }
}
