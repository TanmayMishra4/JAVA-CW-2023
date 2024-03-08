package edu.uob;

import java.util.HashMap;
import java.util.HashSet;

public class Database<PrimaryKeyType> {
    String name;
    HashMap<String, Table> tables;
    String primaryKey;
    HashSet<PrimaryKeyType> primaryKeys;
}
