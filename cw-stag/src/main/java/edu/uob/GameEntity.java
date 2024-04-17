package edu.uob;

import edu.uob.Model.EntityType;
import edu.uob.Model.Player;

public abstract class GameEntity
{
    private String name;
    private EntityType entityType;
    private String description;

    public GameEntity(String name, String description, EntityType entityType)
    {
        this.entityType =  entityType;
        this.name = name;
        this.description = description;
    }


    public EntityType getEntityType(){
        return entityType;
    }

    public String getName()
    {
        return name;
    }

    public String getDescription()
    {
        return description;
    }

    public boolean equals(GameEntity another){
        return this.getName().equals(another.getName());
    }
}
