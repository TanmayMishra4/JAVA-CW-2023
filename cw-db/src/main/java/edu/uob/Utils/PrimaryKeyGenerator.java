package edu.uob.Utils;

import edu.uob.Model.Value;

public class PrimaryKeyGenerator {
    private int key;
    public PrimaryKeyGenerator(){
        this(0);
    }
    public PrimaryKeyGenerator(int key){
        this.key = key;
    }

    public Value getPrimaryKey(){
        return new Value(key++);
    }
}
