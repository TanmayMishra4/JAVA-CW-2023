package edu.uob;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static edu.uob.ResponseType.ERROR;

public class Utils {
    private static final Set<String> keyWords = new HashSet<>(Arrays.asList("USE", "CREATE", "DATABASE", "TABLE", "DROP", "ALTER", "INSERT", "INTO", "SELECT", "UPDATE", "DELETE", "FROM", "JOIN", "ADD", "TRUE", "FALSE", "NULL", "AND", "OR"));
    private static final Set<Character> symbols = new HashSet<>(Arrays.asList('!' , '#' , '$' , '%' , '&' , '(' , ')' , '*' , '+' , ',' , '-' , '.' , '/' , ':' , ';' , '>' , '=' , '<' , '?' , '@' , '[' , '\\' , ']' , '^' , '_' , '`' , '{' , '}' , '~'));
    static String generateResponse(ResponseType type, String message){
        if(type == ERROR){
            return "[ERROR]: " + message;
        }
        else{
            return "[OK]: " + message;
        }
    }
    static boolean isNotKeyWord(String word){
        if(keyWords.contains(word.toUpperCase())) return true;
        return false;
    }

    public static boolean isSymbol(char charVal) {
        return symbols.contains(charVal);
    }
}
