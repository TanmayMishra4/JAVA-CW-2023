package edu.uob.Model;

import edu.uob.GameEntity;

public class Furniture extends GameEntity {
    public Furniture(String name, String description) {
        super(name, description, EntityType.FURNITURE);
    }
}
