package edu.uob.Utils;

import edu.uob.AllEnums.BoolOperator;
import edu.uob.AllEnums.ResponseType;
import edu.uob.AllEnums.SQLComparator;
import edu.uob.AllExceptions.QueryExceptions.SQLQueryException;
import edu.uob.AllExceptions.QueryExceptions.IllegalValueTypeException;
import edu.uob.Model.NULLObject;
import edu.uob.Model.Value;

import java.io.File;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import static edu.uob.AllEnums.ResponseType.ERROR;

public class Utils {

    private final static String dbPath = Paths.get("databases").toAbsolutePath().toString();
    private static final Set<String> keyWords = new HashSet<>(Arrays.asList("USE", "CREATE", "DATABASE", "TABLE", "DROP", "ALTER", "INSERT", "INTO", "SELECT", "UPDATE", "DELETE", "FROM", "JOIN", "ADD", "TRUE", "FALSE", "NULL", "AND", "OR"));
    private static final Set<Character> symbols = new HashSet<>(Arrays.asList('!' , '#' , '$' , '%' , '&' , '(' , ')' , '*' , '+' , ',' , '-' , '.' , '/' , ':' , ';' , '>' , '=' , '<' , '?' , '@' , '[' , '\\' , ']' , '^' , '_' , '`' , '{' , '}' , '~'));
    public static String generateResponse(ResponseType type, String message){
        if(type == ERROR){
            return "[ERROR]: " + message;
        }
        else{
            int len = message.length();
            int index = len-1;
            for(;index>=0;index--){
                if(message.charAt(index) != '\n') break;
            }
            message = message.substring(0, Math.min(len, index+1));
            return "[OK]\n" + message;
        }
    }
    public static boolean isNotKeyWord(String word){
        return keyWords.contains(word.toUpperCase());
    }

    public static boolean isSymbol(char charVal) {
        return symbols.contains(charVal);
    }

    public static Value getValue(String token) throws IllegalValueTypeException {
        try{
            return getValueLiteral(token);
        }catch(Exception ignored){}

        try(Value val = getStringFromDB(token)){
            return val;
        } catch (Exception ignored) {}

        throw new IllegalValueTypeException();
    }

    private static Value getStringFromDB(String token) {
        return new Value(token);
    }

    public static Value getValueLiteral(String token) throws IllegalValueTypeException {
        try(Value val = getBooleanLiteral(token)){
            return val;
        } catch (Exception ignored) {}

        try(Value val = getIntegerLiteral(token)){
            return val;
        } catch (Exception ignored) {}

        try(Value val = getFloatLiteral(token)){
            return val;
        } catch (Exception ignored) {}

        try(Value val = getNullLiteral(token)){
            return val;
        }
        catch(Exception ignored){}

        try(Value val = getStringLiteral(token)){
            return val;
        } catch (Exception ignored) {}

        throw new IllegalValueTypeException();
    }

    public static Value getIntegerLiteral(String token) throws Exception{
        Integer val;
        val = Integer.parseInt(token);
        return new Value(val);
    }
    public static Value getFloatLiteral(String token) throws Exception{
        Double val;
        val = Double.parseDouble(token);
        return new Value(val);
    }

    public static Value getBooleanLiteral(String token) throws Exception{
        Boolean val;
        if(token.equalsIgnoreCase("TRUE")) val = Boolean.valueOf("true");
        else if(token.equalsIgnoreCase("FALSE")) val = Boolean.valueOf("false");
        else{
            throw new Exception("Not a Boolean Literal");
        }
        return new Value(val);
    }

    public static Value getStringLiteral(String token) throws Exception{
        int stringLength = token.length();
        char closingBracket = token.charAt(stringLength - 1);
        char openingBracket = token.charAt(0);
        String stringVal;
        if(openingBracket == '\'' && closingBracket ==  '\''){
            stringVal = token.substring(1, stringLength - 1);
            for(char charVal : stringVal.toCharArray()){
                if(Character.isDigit(charVal) || Character.isLetter(charVal) || charVal == ' ' || Utils.isSymbol(charVal)) continue;
                else throw new SQLQueryException("String contains Illegal characters");
            }
        }
        else throw new Exception("Not a String Literal");
        return new Value(stringVal);
    }

    public static void populateComparatorMap(HashMap<String, SQLComparator> comparatorMap){
        if(!comparatorMap.isEmpty()) return;
        comparatorMap.put("==", SQLComparator.EQUALS);
        comparatorMap.put("!=", SQLComparator.NOT_EQUALS);
        comparatorMap.put(">", SQLComparator.GREATER_THAN);
        comparatorMap.put("<", SQLComparator.LESS_THAN);
        comparatorMap.put(">=", SQLComparator.GREATER_EQUALS);
        comparatorMap.put("<=", SQLComparator.LESS_EQUALS);
        comparatorMap.put("LIKE", SQLComparator.LIKE);
    }

    public static Value getNullLiteral(String token) throws Exception{
        if(!token.equalsIgnoreCase("NULL")) {
            throw new Exception("Not NULL Value in NULL Object");
        }
        return new Value(new NULLObject());
    }

    public static void populateBoolOperatorMap(HashMap<String, BoolOperator> boolOpMap) {
        if(!boolOpMap.isEmpty()) return;
        boolOpMap.put("AND", BoolOperator.AND);
        boolOpMap.put("OR", BoolOperator.OR);
    }

    public static String getDBFilePathName(String dbName){
        return dbPath + File.separator + dbName + File.separator;
    }
    public static String getDBFilePathTable(String dbName, String tableName){
        return getDBFilePathName(dbName) + tableName + ".tab";
    }
}
