package edu.uob;

import java.util.HashMap;
import java.util.HashSet;

public class Database<PrimaryKeyType> {
    private String name;
    private HashMap<String, Table> tables;
    private String primaryKey;
    private HashSet<PrimaryKeyType> primaryKeys;
}
