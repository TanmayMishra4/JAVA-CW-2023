package edu.uob.Model;

import edu.uob.GameEntity;

public class GameCharacter extends GameEntity {
    public GameCharacter(String name, String description) {
        super(name, description, EntityType.CHARACTER);
    }

    public GameCharacter(String name, String description, EntityType type) {
        super(name, description, type);
    }
}
